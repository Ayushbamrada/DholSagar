//// file: com/dholsagar/app/presentation/onboarding_provider/components/AddTeamMemberDialog.kt
//package com.dholsagar.app.presentation.onboarding_provider.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.dholsagar.app.presentation.onboarding_provider.ProviderOnboardingViewModel
//
//@Composable
//fun AddTeamMemberDialog(
//    onDismiss: () -> Unit,
//    onAddMember: (name: String, role: String) -> Unit,
//    viewModel: ProviderOnboardingViewModel = hiltViewModel() // Use shared instance
//) {
//    val name by viewModel.newMemberName.collectAsState()
//    val role by viewModel.newMemberRole.collectAsState()
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Add New Team Member") },
//        text = {
//            Column {
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = viewModel::onNewMemberNameChange,
//                    label = { Text("Member's Name") }
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                OutlinedTextField(
//                    value = role,
//                    onValueChange = viewModel::onNewMemberRoleChange,
//                    label = { Text("Role (e.g., Dhol Player)") }
//                )
//            }
//        },
//        confirmButton = {
//            Button(
//                onClick = { onAddMember(name, role) },
//                enabled = name.isNotBlank() && role.isNotBlank()
//            ) {
//                Text("Add")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        }
//    )
//}

// file: com/dholsagar/app/presentation/onboarding_provider/components/AddTeamMemberDialog.kt
package com.dholsagar.app.presentation.onboarding_provider.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dholsagar.app.presentation.onboarding_provider.ProviderOnboardingViewModel

@Composable
fun AddTeamMemberDialog(
    onDismiss: () -> Unit,
    onAddMember: (name: String, role: String) -> Unit,
    // Use the instance of the ViewModel passed from the main screen
    // to ensure you're working with the same data.
    viewModel: ProviderOnboardingViewModel = hiltViewModel()
) {
    val name by viewModel.newMemberName.collectAsState()
    val role by viewModel.newMemberRole.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Team Member") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = viewModel::onNewMemberNameChange,
                    label = { Text("Member's Name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = role,
                    onValueChange = viewModel::onNewMemberRoleChange,
                    label = { Text("Role (e.g., Dhol Player)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddMember(name, role) },
                // The button is only enabled when both fields are filled.
                enabled = name.isNotBlank() && role.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
