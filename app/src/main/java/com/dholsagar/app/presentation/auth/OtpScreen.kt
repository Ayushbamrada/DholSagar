// file: com/dholsagar/app/presentation/auth/OtpScreen.kt
package com.dholsagar.app.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dholsagar.app.core.navigation.Screen

@Composable
fun OtpScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val otp by viewModel.otp.collectAsState()
    val context = LocalContext.current

    // This LaunchedEffect listens for one-time events like navigation
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AuthUiEvent.Navigate -> {
                    // Navigate to the destination provided by the ViewModel
                    navController.navigate(event.route) {
                        // Clear the entire back stack up to the splash screen
                        // so the user can't go back to the auth flow.
                        popUpTo(Screen.SplashScreen.route)
                    }
                }
                // Ignore other events not relevant to this screen
                else -> {}
            }
        }
    }

    // This LaunchedEffect is for showing error messages as Toasts
    LaunchedEffect(key1 = state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorShown() // Reset the error so it doesn't show again
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .safeDrawingPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Enter Verification Code", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Enter the 6-digit code sent to your phone.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = viewModel::onOtpChange,
                    label = { Text("6-Digit OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = viewModel::onVerifyOtpClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading // Disable button while loading
                ) {
                    Text("Verify & Continue")
                }
            }

            // Show a loading spinner when an operation is in progress
            if (state.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}