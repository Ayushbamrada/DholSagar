// file: com/dholsagar/app/presentation/home_provider/ProviderDashboardScreen.kt
package com.dholsagar.app.presentation.home_provider

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Add // <-- IMPORT FIX
import androidx.compose.material.icons.outlined.* // <-- IMPORT FIX (gets all outlined icons)
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun ProviderDashboardScreen(
    viewModel: ProviderDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val provider = state.provider
    val context = LocalContext.current

    // --- Listen for Save Success ---
    LaunchedEffect(key1 = state.saveSuccess) {
        if (state.saveSuccess) {
            Toast.makeText(context, "Details Saved!", Toast.LENGTH_SHORT).show()
            viewModel.onSaveSuccessShown()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (state.isLoading) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else if (provider != null) {
            item {
                Text(
                    "Welcome, ${provider.leadProviderName}!",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item { KycStatusCard(status = provider.kycStatus) }

            item { AdminOfferCard() } // The hardcoded admin offer

            item {
                MyServicesCard(
                    viewModel = viewModel,
                    isSaving = state.isSaving
                )
            }

            item {
                ManagePortfolioCard(
                    images = provider.portfolioImageUrls,
                    videoUrl = provider.portfolioVideoUrl
                )
            }

        } else if (state.error != null) {
            item {
                Text(
                    "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// --- Card Composables ---

@Composable
fun KycStatusCard(status: String) {
    // THIS IS THE FIX: Correctly declared variables, not using destructuring
    val backgroundColor: Color
    val contentColor: Color
    val icon: ImageVector
    val text: String

    when (status) {
        "APPROVED" -> {
            backgroundColor = Color(0xFFE6F4EA)
            contentColor = Color(0xFF1E8E3E)
            icon = Icons.Outlined.CheckCircle
            text = "Your KYC is Approved!"
        }
        "PENDING" -> {
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            icon = Icons.Outlined.Warning
            text = "Your KYC is Pending Review"
        }
        else -> { // REJECTED or other
            backgroundColor = MaterialTheme.colorScheme.errorContainer
            contentColor = MaterialTheme.colorScheme.onErrorContainer
            icon = Icons.Outlined.Warning
            text = "Your KYC was Rejected"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor, contentColor = contentColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            // This Text composable is now correct
            Text(text = text, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun AdminOfferCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Offer",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Special Offer!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Give 30% off this weekend to get a top spot on the user home screen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun MyServicesCard(viewModel: ProviderDashboardViewModel, isSaving: Boolean) {
    val description by viewModel.description.collectAsState()
    val specialty by viewModel.specialty.collectAsState()
    val perDayCharge by viewModel.perDayCharge.collectAsState()
    val chargeDescription by viewModel.chargeDescription.collectAsState()

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("My Services & Pricing", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = perDayCharge,
                onValueChange = viewModel::onPerDayChargeChange,
                label = { Text("Per Day Charge (e.g., 3000)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.Create, "Rupee") } // <-- IMPORT FIX
            )

            OutlinedTextField(
                value = chargeDescription,
                onValueChange = viewModel::onChargeDescriptionChange,
                label = { Text("Charge Description (e.g., per jagar)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = specialty,
                onValueChange = viewModel::onSpecialtyChange,
                label = { Text("My Specialty (e.g., Traditional Dhol)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("About Me / My Band") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 4
            )

            Button(
                onClick = viewModel::onSaveDetails,
                enabled = !isSaving,
                modifier = Modifier.align(Alignment.End)
            ) {
                if(isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Details")
                }
            }
        }
    }
}

@Composable
fun ManagePortfolioCard(images: List<String>, videoUrl: String?) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Manage Portfolio", style = MaterialTheme.typography.titleLarge)
                Button(onClick = { /* TODO: Handle Upload New */ }) {
                    Icon(Icons.Filled.Add, contentDescription = "Upload") // <-- IMPORT FIX
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New")
                }
            }

            Text("My Images", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(images) { imageUrl ->
                    PortfolioItem(
                        url = imageUrl,
                        onDelete = { /* TODO: Handle Delete Image */ }
                    )
                }
            }

            videoUrl?.let {
                Text("My Video", style = MaterialTheme.typography.titleMedium)
                PortfolioItem(
                    url = it, // You can show a thumbnail here
                    isImage = false,
                    onDelete = { /* TODO: Handle Delete Video */ }
                )
            }
        }
    }
}

@Composable
fun PortfolioItem(url: String, isImage: Boolean = true, onDelete: () -> Unit) {
    Box(modifier = Modifier.size(100.dp)) {
        AsyncImage(
            model = url,
            contentDescription = "Portfolio Item",
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        if (!isImage) {
            // Overlay for video
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.CheckCircle, // <-- IMPORT FIX
                    contentDescription = "Video",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .size(20.dp)
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}