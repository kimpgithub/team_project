package com.example.team_project


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.team_project.loginsceen.LoginScreen
import com.example.team_project.camerascreen.CameraScreen
import com.example.team_project.camerascreen.ErrorScreen
import com.example.team_project.camerascreen.LoadingScreen
import com.example.team_project.infoscreen.InfoScreen
import com.example.team_project.resultscreen.ResultScreen
import com.example.team_project.ui.theme.Team_projectTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, proceed with setting up the camera
                setContent {
                    AppContent()
                }
            } else {
                // Permission denied, show a message to the user
                // You can show a Snackbar or a dialog
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase 초기화
        FirebaseApp.initializeApp(this)
        analytics = Firebase.analytics

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted
            setContent {
                AppContent()
            }
        } else {
            // Request the permission
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

@Composable
fun AppContent() {
    Team_projectTheme {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "loginScreen") {
            composable("loginScreen") { LoginScreen(navController) }
            composable("mainScreen") { MainScreen(navController) }
            composable("cameraScreen") { CameraScreen(navController) }
            composable("loadingScreen") { LoadingScreen() }
            composable("errorScreen") { ErrorScreen() }
            composable(
                "resultScreen/{imageUrl}",
                arguments = listOf(navArgument("imageUrl") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                ResultScreen(
                    navController,
                    backStackEntry.arguments?.getString("imageUrl") ?: ""
                )
            }
            composable(
                "infoScreen/{initialImageUrl}",
                arguments = listOf(navArgument("initialImageUrl") { type = NavType.StringType })
            ) { backStackEntry ->
                val initialImageUrl = backStackEntry.arguments?.getString("initialImageUrl")
                InfoScreen(navController, initialImageUrl)
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.house), // Replace with your image resource
            contentDescription = "Cracker Image",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Cracker",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.navigate("cameraScreen") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
        ) {
            Text("Start Camera")
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    Team_projectTheme {
        MainScreen(navController)
    }
}


@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
fun LoginScreenPreview() {
    val navController = rememberNavController()
    Team_projectTheme {
        LoginScreen(navController)
    }
}