//// file: com/dholsagar/app/presentation/onboarding_provider/components/KycPage.kt
//package com.dholsagar.app.presentation.onboarding_provider.components
//
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.outlined.Menu
//import androidx.compose.material.icons.outlined.ShoppingCart
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Divider
//import androidx.compose.material3.FilledTonalButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import com.dholsagar.app.presentation.onboarding_provider.TeamMember
//
//@Composable
//fun KycPage(
//    providerName: String,
//    teamMembers: List<TeamMember>,
//    selectedKycUris: Map<String, Uri>, // NEW
//    onDocumentSelected: (docId: String, uri: Uri) -> Unit, // NEW
//    onRemoveDocument: (docId: String) -> Unit // NEW
//) {
//    // This list now correctly includes the main provider first, then the team members.
//    val allMembersForKyc = listOf(TeamMember(providerName, "Lead")) + teamMembers
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        item {
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                "Complete Your KYC",
//                style = MaterialTheme.typography.headlineSmall
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                "Upload documents for yourself and each team member to get a 'Verified' tag on your profile.",
//                style = MaterialTheme.typography.bodyMedium,
//                textAlign = TextAlign.Center
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//
//        items(allMembersForKyc) { member ->
//            val aadhaarId = "${member.name}_aadhaar"
//            val panId = "${member.name}_pan"
//            KycMemberItem(
//                title = if (member.name.isNotBlank()) "${member.name}'s Documents" else "Your Documents",
//                aadhaarUri = selectedKycUris[aadhaarId],
//                panUri = selectedKycUris[panId],
//                onAadhaarSelected = { uri -> if (uri != null) onDocumentSelected(aadhaarId, uri) },
//                onPanSelected = { uri -> if (uri != null) onDocumentSelected(panId, uri) },
//                onRemoveAadhaar = { onRemoveDocument(aadhaarId) },
//                onRemovePan = { onRemoveDocument(panId) }
//            )
//            Divider(modifier = Modifier.padding(vertical = 16.dp))
//        }
//    }
//}
//
//@Composable
//fun KycMemberItem(
//    title: String,
//    aadhaarUri: Uri?,
//    panUri: Uri?,
//    onAadhaarSelected: (Uri?) -> Unit,
//    onPanSelected: (Uri?) -> Unit,
//    onRemoveAadhaar: () -> Unit,
//    onRemovePan: () -> Unit
//) {
//    val filePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent(),
//        onResult = { uri -> /* handled by specific launchers below */ }
//    )
//    val aadhaarLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent(), onResult = onAadhaarSelected
//    )
//    val panLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent(), onResult = onPanSelected
//    )
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text(
//                text = title,
//                style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                FilledTonalButton(
//                    onClick = { /* TODO: Implement Aadhaar upload */ },
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Icon(Icons.Outlined.Menu, contentDescription = null)
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Aadhaar")
//                }
//                FilledTonalButton(
//                    onClick = { /* TODO: Implement PAN upload */ },
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Icon(Icons.Outlined.ShoppingCart, contentDescription = null)
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("PAN Card")
//                }
//            }
//        }
//    }
//}

// file: com/dholsagar/app/presentation/onboarding_provider/components/KycPage.kt
package com.dholsagar.app.presentation.onboarding_provider.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dholsagar.app.presentation.onboarding_provider.TeamMember

@Composable
fun KycPage(
    providerName: String,
    teamMembers: List<TeamMember>,
    selectedKycUris: Map<String, Uri>,
    onDocumentSelected: (docId: String, uri: Uri) -> Unit,
    onRemoveDocument: (docId: String) -> Unit
) {
    val allMembersForKyc = listOf(TeamMember(providerName.ifBlank { "Lead Provider" }, "Lead")) + teamMembers

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text("Complete Your KYC", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Upload documents for yourself and each team member to get a 'Verified' tag. Both sides are necessary for ID cards.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(allMembersForKyc) { member ->
            val aadhaarId = "${member.name}_aadhaar"
            val panId = "${member.name}_pan"
            KycMemberItem(
                title = if (member.name.isNotBlank()) "${member.name}'s Documents" else "Your Documents",
                aadhaarUri = selectedKycUris[aadhaarId],
                panUri = selectedKycUris[panId],
                onAadhaarSelected = { uri -> if (uri != null) onDocumentSelected(aadhaarId, uri) },
                onPanSelected = { uri -> if (uri != null) onDocumentSelected(panId, uri) },
                onRemoveAadhaar = { onRemoveDocument(aadhaarId) },
                onRemovePan = { onRemoveDocument(panId) }
            )
            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}

@Composable
fun KycMemberItem(
    title: String,
    aadhaarUri: Uri?,
    panUri: Uri?,
    onAadhaarSelected: (Uri?) -> Unit,
    onPanSelected: (Uri?) -> Unit,
    onRemoveAadhaar: () -> Unit,
    onRemovePan: () -> Unit
) {
    // THIS IS THE FIX: Use OpenDocument with a specified array of MIME types.
    // This provides a much more stable and consistent file picker.
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> /* handled by specific launchers below */ }
    )
    val aadhaarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(), onResult = onAadhaarSelected
    )
    val panLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(), onResult = onPanSelected
    )

    val supportedMimeTypes = arrayOf("image/*", "application/pdf")

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Aadhaar Section
            if (aadhaarUri != null) {
                FilePreviewItem(uri = aadhaarUri, onRemoveClick = onRemoveAadhaar)
            } else {
                Button(
                    onClick = { aadhaarLauncher.launch(supportedMimeTypes) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Person, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Aadhaar (Image/PDF)")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // PAN Section
            if (panUri != null) {
                FilePreviewItem(uri = panUri, onRemoveClick = onRemovePan)
            } else {
                Button(
                    onClick = { panLauncher.launch(supportedMimeTypes) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Person, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload PAN Card (Image/PDF)")
                }
            }
        }
    }
}