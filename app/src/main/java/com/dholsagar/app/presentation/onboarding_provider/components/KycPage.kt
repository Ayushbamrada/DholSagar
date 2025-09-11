//// file: com/dholsagar/app/presentation/onboarding_provider/components/KycPage.kt
//package com.dholsagar.app.presentation.onboarding_provider.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
////import androidx.compose.material.icons.filled.FileUpload
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun KycPage() {
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text("Complete Your KYC", style = MaterialTheme.typography.headlineSmall)
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Button(onClick = { /* TODO: Implement file picker for Aadhaar */ }) {
//            Icon(imageVector = Icons.Default.Add, contentDescription = null)
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Upload Aadhaar Card")
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = { /* TODO: Implement file picker for PAN */ }) {
//            Icon(imageVector = Icons.Default.Add, contentDescription = null)
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Upload PAN Card")
//        }
//    }
//}


/// file: com/dholsagar/app/presentation/onboarding_provider/components/KycPage.kt
package com.dholsagar.app.presentation.onboarding_provider.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    teamMembers: List<TeamMember>
) {
    // This list now correctly includes the main provider first, then the team members.
    val allMembersForKyc = listOf(TeamMember(providerName, "Lead")) + teamMembers

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Complete Your KYC",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Upload documents for yourself and each team member to get a 'Verified' tag on your profile.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(allMembersForKyc) { member ->
            // Use the member's name if available, otherwise use a generic title.
            val title = if (member.name.isNotBlank()) {
                "${member.name}'s Documents"
            } else {
                "Your Documents" // Fallback for the main provider if name is not yet entered
            }
            KycMemberItem(title = title)
        }
    }
}

@Composable
fun KycMemberItem(title: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilledTonalButton(
                    onClick = { /* TODO: Implement Aadhaar upload */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Outlined.Menu, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aadhaar")
                }
                FilledTonalButton(
                    onClick = { /* TODO: Implement PAN upload */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PAN Card")
                }
            }
        }
    }
}