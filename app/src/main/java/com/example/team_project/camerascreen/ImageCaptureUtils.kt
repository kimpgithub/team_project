@file:Suppress("DEPRECATION")

package com.example.team_project.camerascreen

import android.content.ContentValues
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.team_project.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

fun getOutputDirectory(context: android.content.Context): File {
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
}
fun takePhoto(
    context: android.content.Context,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: ExecutorService,
    onImageSaved: (File) -> Unit
) {
    if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as android.app.Activity, arrayOf(android.Manifest.permission.CAMERA), 1
        )
        return
    }

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(ContentValues.TAG, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Log.d(ContentValues.TAG, "Photo capture succeeded: ${photoFile.absolutePath}")
                onImageSaved(photoFile)
            }
        })
}
