package com.example.team_project.resultscreen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Detection Result",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)  // 왼쪽 하단으로 정렬
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.navigate("cameraScreen") },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(end = 8.dp)
            ) {
                Text("Back to Camera")
            }
            Button(
                onClick = { navController.navigate("infoScreen/${Uri.encode(encodedImageUrl)}") },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(start = 8.dp)
            ) {
                Text("Info")
            }
        }
    }
}