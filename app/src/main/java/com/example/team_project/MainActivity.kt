package com.example.team_project


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            composable("infoScreen") {
                InfoScreen(navController)
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("cameraScreen") }) {
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