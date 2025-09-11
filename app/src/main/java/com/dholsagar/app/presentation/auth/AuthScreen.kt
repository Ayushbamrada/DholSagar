// file: com/dholsagar/app/presentation/auth/AuthScreen.kt
package com.dholsagar.app.presentation.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dholsagar.app.R
import com.dholsagar.app.core.navigation.Screen
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel // Passed from navigation graph
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val oneTapClient = Identity.getSignInClient(context)
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    viewModel.onGoogleSignInResult(credential.googleIdToken)
                } catch (e: ApiException) {
                    viewModel.onGoogleSignInResult(null)
                }
            } else {
                viewModel.onGoogleSignInResult(null)
            }
        }
    )

    // This LaunchedEffect handles ALL UI events from the ViewModel
    LaunchedEffect(key1 = Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AuthUiEvent.LaunchGoogleSignIn -> {
                    googleSignInLauncher.launch(
                        IntentSenderRequest.Builder(event.intentSender).build()
                    )
                }
                is AuthUiEvent.Navigate -> {
                    navController.navigate(event.route)
                }
                // THIS BRANCH WAS MISSING, CAUSING THE "when must be exhaustive" ERROR
                is AuthUiEvent.NavigateAndPopUpTo -> {
                    navController.navigate(event.route) {
                        popUpTo(event.popUpTo) { inclusive = true }
                    }
                }
            }
        }
    }

    // THIS LAUNCHEDEFFECT WAS USING THE OLD `signInSuccess` STATE AND IS NO LONGER NEEDED.
    // IT HAS BEEN REMOVED TO FIX THE "Unresolved reference" ERROR.
    // Error toasts are now handled by a separate, cleaner LaunchedEffect.

    LaunchedEffect(key1 = state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorShown()
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
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Let's Get Started",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in or create an account to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(64.dp))

                Button(
                    onClick = viewModel::onGoogleSignInClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    enabled = !state.isLoading
                ) {
                    // TODO: Use a proper Google Icon
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Continue with Google")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { navController.navigate(Screen.PhoneAuthScreen.route) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    enabled = !state.isLoading
                ) {
                    // TODO: Use a proper Phone Icon
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_storefront_24),
                        contentDescription = "Phone Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Continue with Phone Number")
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}