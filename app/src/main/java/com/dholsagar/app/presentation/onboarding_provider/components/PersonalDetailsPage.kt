//// file: com/dholsagar/app/presentation/onboarding_provider/components/PersonalDetailsPage.kt
//package com.dholsagar.app.presentation.onboarding_provider.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun PersonalDetailsPage(
//    name: String,
//    onNameChange: (String) -> Unit,
//    bandName: String,
//    onBandNameChange: (String) -> Unit,
//) {
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Spacer(modifier = Modifier.height(24.dp))
//        OutlinedTextField(
//            value = name,
//            onValueChange = onNameChange,
//            label = { Text("Your Full Name") },
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(
//            value = bandName,
//            onValueChange = onBandNameChange,
//            label = { Text("Band / Group Name") },
//            modifier = Modifier.fillMaxWidth()
//        )
//        // TODO: Add fields for Gender, Age, etc.
//    }
//}

// file: com/dholsagar/app/presentation/onboarding_provider/components/PersonalDetailsPage.kt
package com.dholsagar.app.presentation.onboarding_provider.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PersonalDetailsPage(
    name: String,
    onNameChange: (String) -> Unit,
    bandName: String,
    onBandNameChange: (String) -> Unit,
    gmail: String,
    onGmailChange: (String) -> Unit
) {
    // MODIFICATION: Added .verticalScroll to the Column.
    // This makes the entire page scrollable, solving the keyboard issue.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // 👈 THE FIX
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Let's start with the basics.",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Your Full Name") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true
        )
        OutlinedTextField(
            value = bandName,
            onValueChange = onBandNameChange,
            label = { Text("Band / Group Name") },
            modifier = Modifier.fillMaxWidth(),
            // MODIFICATION: Using a more generic icon for 'Band/Group'
            leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true
        )
        OutlinedTextField(
            value = gmail,
            onValueChange = onGmailChange,
            label = { Text("Gmail (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
    }
}