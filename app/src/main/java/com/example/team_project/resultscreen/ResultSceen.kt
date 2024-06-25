package com.example.team_project.resultscreen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@Composable
fun ResultScreen(navController: NavController, encodedImageUrl: String) {
    val imageUrl = Uri.decode(encodedImageUrl)
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "Detection Result",
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = { navController.navigate("cameraScreen") },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Back to Camera")
        }
        Button(
            onClick = { navController.navigate("infoScreen") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("Info")
        }
    }
}