//// file: com/dholsagar/app/presentation/onboarding_user/UserOnboardingScreen.kt
//package com.dholsagar.app.presentation.onboarding_user
//
//import android.widget.Toast
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.dholsagar.app.core.navigation.Route
//
//@Composable
//fun UserOnboardingScreen(
//    navController: NavController,
//    viewModel: UserOnboardingViewModel = hiltViewModel()
//) {
//    val name by viewModel.name.collectAsState()
//    val email by viewModel.email.collectAsState()
//    val phone by viewModel.phone.collectAsState()
//    val state by viewModel.state.collectAsState()
//    val context = LocalContext.current
//
//    LaunchedEffect(key1 = true) {
//        viewModel.eventFlow.collect { event ->
//            when (event) {
//                is OnboardingEvent.Navigate -> {
//                    navController.navigate(event.route) {
//                        // Clear the auth graph so user can't go back to it
//                        popUpTo(Route.AUTH_GRAPH) { inclusive = true }
//                    }
//                }
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
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(24.dp)
//                    .safeDrawingPadding(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Text("Just a few more details...", style = MaterialTheme.typography.headlineSmall)
//                Spacer(modifier = Modifier.height(32.dp))
//
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = viewModel::onNameChange,
//                    label = { Text("Full Name") },
//                    modifier = Modifier.fillMaxWidth(),
//                    singleLine = true
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//                OutlinedTextField(
//                    value = email,
//                    onValueChange = viewModel::onEmailChange,
//                    label = { Text("Email Address (Optional)") },
//                    modifier = Modifier.fillMaxWidth(),
//                    singleLine = true,
//                    enabled = viewModel.isEmailEditable
//                )
//
//                // Phone TextField
//                OutlinedTextField(
//                    value = phone, onValueChange = viewModel::onPhoneChange,
//                    label = { Text("Phone Number") },
//                    modifier = Modifier.fillMaxWidth(),
//                    prefix = { Text("+91 ") },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//                    singleLine = true,
//                    enabled = viewModel.isPhoneEditable // Control editing
//                )
//
//                Spacer(modifier = Modifier.height(32.dp))
//                Button(
//                    onClick = viewModel::onSaveClick,
//                    modifier = Modifier.fillMaxWidth(),
//                    enabled = !state.isLoading && name.isNotBlank()
//                ) {
//                    Text("Save and Continue")
//                }
//            }
//            if (state.isLoading) {
//                CircularProgressIndicator()
//            }
//        }
//    }
//}

package com.dholsagar.app.presentation.onboarding_user

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOnboardingScreen(
    navController: NavController,
    viewModel: UserOnboardingViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // UPDATED: Listens for the new NavigateAndPopUp event
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is OnboardingEvent.NavigateAndPopUp -> {
                    navController.navigate(event.route) {
                        popUpTo(event.popUpTo) { inclusive = true }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorShown()
        }
    }

    // UPDATED: Using Scaffold for a modern, responsive layout structure
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .safeDrawingPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Just a few more details...", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email Address (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = viewModel.isEmailEditable
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = viewModel::onPhoneChange,
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("+91 ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    enabled = viewModel.isPhoneEditable
                )

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = viewModel::onSaveClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && name.isNotBlank()
                ) {
                    Text("Save and Continue")
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}
