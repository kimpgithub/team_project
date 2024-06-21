from flask import Flask, request, jsonify, render_template, send_file
import cv2
import numpy as np
import os
from PIL import Image, ExifTags
from ultralytics import YOLO

app = Flask(__name__)

UPLOAD_FOLDER = 'uploads'
OUTPUT_FOLDER = 'output'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['OUTPUT_FOLDER'] = OUTPUT_FOLDER

# Ensure upload and output folders exist
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(OUTPUT_FOLDER, exist_ok=True)

# Load a model
model = YOLO("best.pt")  # pretrained YOLOv8n segmentation model

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def correct_image_orientation(img):
    try:
        for orientation in ExifTags.TAGS.keys():
            if ExifTags.TAGS[orientation] == 'Orientation':
                break
        exif = img._getexif()
        if exif is not None:
            orientation = exif.get(orientation, 1)
            if orientation == 3:
                img = img.rotate(180, expand=True)
            elif orientation == 6:
                img = img.rotate(270, expand=True)
            elif orientation == 8:
                img = img.rotate(90, expand=True)
    except (AttributeError, KeyError, IndexError):
        pass
    return img

@app.route('/')
def hello_world():
    return render_template('upload.html')

@app.route('/infer', methods=['GET', 'POST'])
def infer():
    if request.method == 'GET':
        return render_template('upload.html')

    if 'file' not in request.files:
        return jsonify({'error': 'No file part'}), 400

    file = request.files['file']
    if file and allowed_file(file.filename):
        filename = file.filename
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(filepath)
        
        try:
            # Use PIL to read the image file
            with open(filepath, 'rb') as f:
                img = Image.open(f)
                img = img.convert('RGB')
                img = correct_image_orientation(img)
                img = np.array(img)
                img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
            
            if img is None:
                return jsonify({'error': 'Unable to read the image file. The file may be corrupted.'}), 400

            results = model(filepath)
            
            # Process each result
            for result in results:
                masks = result.masks  # Get the masks object
                boxes = result.boxes
                
                for i in range(len(boxes)):
                    box = boxes[i]
                    mask = masks.data[i].cpu().numpy()  # Convert mask to numpy array
                    
                    # Resize mask to match the image size
                    mask_resized = cv2.resize(mask, (img.shape[1], img.shape[0]), interpolation=cv2.INTER_NEAREST)
                    
                    x1, y1, x2, y2 = map(int, box.xyxy[0])
                    confidence = box.conf[0]
                    label = result.names[box.cls[0].item()]
                    
                    # Draw rectangle
                    cv2.rectangle(img, (x1, y1), (x2, y2), (0, 0, 255), 2)  # red rectangle
                    
                    # Draw label
                    cv2.putText(img, f'{label} {confidence:.1f}', (x1, y1 - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 0, 255), 2, cv2.LINE_AA)  # red text
                    
                    # Draw mask
                    mask_resized = (mask_resized > 0.5).astype(np.uint8)  # Binarize mask
                    mask_colored = np.zeros_like(img)
                    mask_colored[mask_resized == 1] = (0, 0, 255)  # red mask
                    img = cv2.addWeighted(img, 1, mask_colored, 0.5, 0)
            
            output_filename = f"processed_{filename}"
            output_filepath = os.path.join(app.config['OUTPUT_FOLDER'], output_filename)
            cv2.imwrite(output_filepath, img)
            
            # 파일 생성 완료 후 응답 전송
            return jsonify({'status': 'processing complete', 'filename': output_filename}), 200
        
        except Exception as e:
            return jsonify({'error': str(e)}), 500

    else:
        return jsonify({'error': 'File not allowed'}), 400

@app.route('/output/<filename>', methods=['GET'])
def get_output_file(filename):
    file_path = os.path.join(app.config['OUTPUT_FOLDER'], filename)
    if os.path.exists(file_path):
        return send_file(file_path, mimetype='image/jpeg')
    else:
        return jsonify({'error': 'File not found'}), 404

if __name__ == '__main__':
    app.run(host='192.168.45.66', port=8080, debug=True)
