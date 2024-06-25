package com.example.team_project.infoscreen

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import android.util.Log

fun sendApartmentData(
    apartmentName: String,
    complexName: String,
    imageUrl: String,
    annotationType: String,
    callback: (Boolean, String) -> Unit
) {
    val client = OkHttpClient()

    val json = JSONObject()
    json.put("apartment_name", apartmentName)
    json.put("complex_name", complexName)
    json.put("image_url", imageUrl)
    json.put("annotation_type", annotationType)

    val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json.toString())
    val request = Request.Builder()
        .url("http://192.168.45.119:5000/add_apartment")  // Ensure this URL matches your Flask server
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("sendApartmentData", "Failed to send data", e)
            callback(false, e.message ?: "Unknown error")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                Log.d("sendApartmentData", "Successfully added apartment information")
                callback(true, "Successfully added apartment information")
            } else {
                Log.e("sendApartmentData", "Failed to add apartment information: ${response.message}")
                callback(false, response.message ?: "Failed to add apartment information")
            }
        }
    })
}
