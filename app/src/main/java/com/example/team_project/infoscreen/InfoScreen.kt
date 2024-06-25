package com.example.team_project.infoscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun InfoScreen(navController: NavController, initialImageUrl: String?) {
    var apartmentName by remember { mutableStateOf("") }
    var complexName by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf(initialImageUrl ?: "") }
    var annotationType by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Button(onClick = {
            sendApartmentData(
                apartmentName,
                complexName,
                imageUrl,
                annotationType
            ) { success, message ->
                if (success) {
                    // Handle success, e.g., show a success message and navigate back
                } else {
                    // Handle error, e.g., show an error message
                }
            }
        }) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("cameraScreen") }) {
            Text("Back to Camera")
        }
    }
}