package com.example.team_project.camerascreen

import android.content.ContentValues
import android.util.Log
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


interface UploadService {
    @Multipart
    @POST("/infer")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ResponseBody>
}

fun uploadImage(imageFile: File, callback: (Boolean) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.45.66:8080/")
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
            Log.e(ContentValues.TAG, "Image upload failed: ${t.message}", t)
            callback(false)
        }
    })
}
// 파일 존재 여부를 확인하는 함수
fun checkFileExists(url: String): Boolean {
    return try {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "HEAD"
        val responseCode = connection.responseCode
        responseCode == HttpURLConnection.HTTP_OK
    } catch (e: Exception) {
        false
    }
}

