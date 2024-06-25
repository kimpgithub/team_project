package com.example.team_project.infoscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController




@Composable
fun InfoScreen(navController: NavController) {
    var apartmentName by remember { mutableStateOf("") }
    var complexName by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var annotationType by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)) // 배경색 추가
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TextField(
            value = apartmentName,
            onValueChange = { apartmentName = it },
            label = { Text("Apartment Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = complexName,
            onValueChange = { complexName = it },
            label = { Text("Complex Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Image URL") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = annotationType,
            onValueChange = { annotationType = it },
            label = { Text("Annotation Type") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    isLoading = true
                    sendApartmentData(
                        apartmentName,
                        complexName,
                        imageUrl,
                        annotationType
                    ) { success, message ->
                        isLoading = false
                        if (success) {
                            // 성공 처리, 예: 성공 메시지를 보여주고 뒤로 이동
                        } else {
                            // 오류 처리, 예: 오류 메시지를 보여줌
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            ) {
                Text("Submit", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("cameraScreen") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
        ) {
            Text("Back to Camera", color = Color.White)
        }
    }
}