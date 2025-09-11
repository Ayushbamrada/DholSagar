//// file: com/dholsagar/app/presentation/onboarding_provider/components/PortfolioPage.kt
//package com.dholsagar.app.presentation.onboarding_provider.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountCircle
////import androidx.compose.material.icons.filled.AddAPhoto
//import androidx.compose.material.icons.filled.AddCircle
////import androidx.compose.material.icons.filled.Videocam
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun PortfolioPage() {
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text("Showcase Your Work", style = MaterialTheme.typography.headlineSmall)
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Button(onClick = { /* TODO: Implement image picker logic */ }) {
//            Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Upload Photos")
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Upload Portfolio Photos")
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(
//            value = "",
//            onValueChange = { /* TODO: Handle video link state */ },
//            label = { Text("YouTube Video Link (Optional)") },
//            leadingIcon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null) },
//            modifier = Modifier.fillMaxWidth()
//        )
//    }
//}

// file: com/dholsagar/app/presentation/onboarding_provider/components/PortfolioPage.kt
package com.dholsagar.app.presentation.onboarding_provider.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PortfolioPage() {
    // MODIFICATION: Added .verticalScroll here.
    // This directly fixes the issue where the YouTube link field was hidden by the keyboard.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // 👈 THE FIX
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Text("Showcase Your Work", style = MaterialTheme.typography.headlineSmall)
            Text(
                "A great portfolio helps you get more bookings.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Portfolio Photos", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Upload at least 5 high-quality photos of you or your band performing.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* TODO: Implement image picker logic */ }) {
                    Icon(Icons.Outlined.Face, contentDescription = "Upload Photos")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Photos")
                }
            }
        }

        OutlinedTextField(
            value = "",
            onValueChange = { /* TODO: Handle video link state */ },
            label = { Text("YouTube Video Link (Optional)") },
            leadingIcon = { Icon(Icons.Outlined.PlayArrow, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            singleLine = true
        )
    }
}