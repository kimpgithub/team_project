package com.example.team_project.camerascreen


import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val executor: ExecutorService = Executors.newSingleThreadExecutor()

    var previewView: PreviewView? by remember { mutableStateOf(null) }
    var cameraControl by remember { mutableStateOf<androidx.camera.core.CameraControl?>(null) }
    val outputDirectory = getOutputDirectory(context)

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView?.surfaceProvider)

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                context as LifecycleOwner, cameraSelector, preview, imageCapture
            )
            cameraControl = camera.cameraControl
            cameraControl?.setLinearZoom(0.5f) // Fixed zoom level, adjust as necessary
        } catch (exc: Exception) {
            // Handle exception
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        AndroidView(factory = { ctx ->
            PreviewView(ctx).also { previewView = it }
        }, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Please take a picture from a distance of 30 cm",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Button(onClick = {
                navController.navigate("loadingScreen")
                takePhoto(context, imageCapture, outputDirectory, executor) { photoFile ->
                    uploadImage(photoFile) { success ->
                        if (success) {
                            val imageUrl =
                                "http://52.65.163.94:8080/output/processed_${photoFile.name}"
                            val encodedUrl = Uri.encode(imageUrl)

                            // 파일 존재 여부를 확인하는 코루틴 실행
                            navController.currentBackStackEntry?.lifecycleScope?.launch {
                                val fileExists = withContext(Dispatchers.IO) {
                                    var exists = false
                                    var retryCount = 0
                                    val maxRetries = 10  // 최대 재시도 횟수

                                    while (!exists && retryCount < maxRetries) {
                                        exists = checkFileExists(imageUrl)
                                        if (!exists) {
                                            delay(1000)  // 1초마다 확인
                                            retryCount++
                                        }
                                    }
                                    exists
                                }
                                if (fileExists) {
                                    navController.navigate("resultScreen/$encodedUrl")
                                } else {
                                    // 파일이 생성되지 않으면 오류 처리
                                    navController.navigate("errorScreen")
                                }
                            }
                        } else {
                            // Handle upload failure
                        }
                    }
                }
            }) {
                Text("Capture")
            }
        }
    }
}

// 파일 존재 여부를 확인하는 함수
private fun checkFileExists(url: String): Boolean {
    return try {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "HEAD"
        val responseCode = connection.responseCode
        responseCode == HttpURLConnection.HTTP_OK
    } catch (e: Exception) {
        false
    }
}

private fun uploadImage(imageFile: File, callback: (Boolean) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://52.65.163.94:8080/")
        .client(OkHttpClient.Builder().build())
        .build()

    val service = retrofit.create(UploadService::class.java)

    val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

    val call = service.uploadImage(body)
    call.enqueue(object : Callback<ResponseBody> {
        override fun onResponse(
            call: Call<ResponseBody>,
            response: retrofit2.Response<ResponseBody>
        ) {
            callback(response.isSuccessful)
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e(TAG, "Image upload failed: ${t.message}", t)
            callback(false)
        }
    })
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Error: File not found or processing failed")
    }
}

interface UploadService {
    @Multipart
    @POST("/infer")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ResponseBody>
}

private const val TAG = "CameraScreen"


