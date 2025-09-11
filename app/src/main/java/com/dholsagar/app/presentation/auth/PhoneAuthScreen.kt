//// file: com/dholsagar/app/presentation/auth/PhoneAuthScreen.kt
//package com.dholsagar.app.presentation.auth
//
//import android.app.Activity
//import android.widget.Toast
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//
//@Composable
//fun PhoneAuthScreen(
//    navController: NavController,
//    viewModel: AuthViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsState()
//    val phoneNumber by viewModel.phoneNumber.collectAsState()
//    val context = LocalContext.current
//
//    // This LaunchedEffect LISTENS for events from the ViewModel
//    LaunchedEffect(key1 = Unit) {
//        viewModel.eventFlow.collect { event ->
//            when (event) {
//                is AuthUiEvent.Navigate -> {
//                    navController.navigate(event.route)
//                }
//                else -> { /* Other events like Google Sign-In are ignored here */ }
//            }
//        }
//    }
//
//    LaunchedEffect(key1 = state.error) {
//        state.error?.let {
//            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
//            viewModel.onErrorShown()
//        }
//    }
//
//    Surface(modifier = Modifier.fillMaxSize()) {
//        Box(contentAlignment = Alignment.Center) {
//            Column(
//                modifier = Modifier.fillMaxSize().padding(24.dp).safeDrawingPadding(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Text("Enter Your Phone Number", style = MaterialTheme.typography.headlineSmall)
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    "We'll send you a verification code.",
//                    textAlign = TextAlign.Center,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//                Spacer(modifier = Modifier.height(32.dp))
//
//                OutlinedTextField(
//                    value = phoneNumber,
//                    onValueChange = viewModel::onPhoneNumberChange,
//                    label = { Text("Phone Number") },
//                    prefix = { Text("+91 ") },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//                    singleLine = true,
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(24.dp))
//                Button(
//                    onClick = { viewModel.onSendOtpClick(context as Activity) },
//                    modifier = Modifier.fillMaxWidth(),
//                    enabled = !state.isLoading
//                ) {
//                    Text("Send OTP")
//                }
//            }
//            if (state.isLoading) {
//                CircularProgressIndicator()
//            }
//        }
//    }
//}


// file: com/dholsagar/app/presentation/auth/PhoneAuthScreen.kt
package com.dholsagar.app.presentation.auth

import android.app.Activity
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
import androidx.navigation.NavController

@Composable
fun PhoneAuthScreen(
    navController: NavController,
    viewModel: AuthViewModel // IMPORTANT: ViewModel is now passed as a parameter
) {
    val state by viewModel.state.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val context = LocalContext.current

    // This screen does not need to handle navigation events itself,
    // as AuthScreen (the start of the graph) is already observing
    // the shared ViewModel's eventFlow. We only need to listen for errors.
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
                Text("Enter Your Phone Number", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "We'll send you a verification code.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    label = { Text("Phone Number") },
                    prefix = { Text("+91 ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    // The cast to Activity is required by the Firebase SDK.
                    // It's safe here as composables are hosted in an Activity.
                    onClick = { viewModel.onSendOtpClick(context as Activity) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && phoneNumber.length == 10
                ) {
                    Text("Send OTP")
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}