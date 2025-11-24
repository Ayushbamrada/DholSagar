// file: com/dholsagar/app/presentation/home_provider/ProviderProfileScreen.kt
package com.dholsagar.app.presentation.home_provider

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // IMPORT FIX
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProviderProfileScreen(
    viewModel: ProviderDashboardViewModel = hiltViewModel() // Can reuse the dashboard VM
) {
    val state by viewModel.state.collectAsState()
    val provider = state.provider

    // IMPORT FIX
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        // CONTENT IS NOW WRAPPED IN 'item { }'
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("My Profile", style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = { /* TODO: Navigate to Edit Profile Screen */ }) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit Profile")
                }
            }
        }

        // CONTENT IS NOW WRAPPED IN 'item { }'
        item {
            if (provider != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Name: ${provider.leadProviderName}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Band: ${provider.bandName}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Role: ${provider.leadProviderRole}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Experience: ${provider.experienceYears} years", style = MaterialTheme.typography.bodyLarge)
            } else if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}