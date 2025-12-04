package com.dholsagar.app.presentation.home_provider.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dholsagar.app.presentation.home_provider.ProviderBookingsViewModel

val BrownPrimary = Color(0xFF5D4037)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderBookingDetailScreen(
    navController: NavController,
    bookingId: String?,
    viewModel: ProviderBookingsViewModel = hiltViewModel()
) {
    LaunchedEffect(bookingId) {
        if (bookingId != null) {
            viewModel.getBookingDetails(bookingId)
        }
    }

    val state by viewModel.state.collectAsState()
    val booking = state.selectedBooking

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (booking == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrownPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFFAFAFA))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. User Details Card
                DetailCard(title = "Customer Info", icon = Icons.Filled.Person) {
                    DetailRow("Name", booking.userName)
                    DetailRow("Contact", booking.userPhone)
                    DetailRow("Location", booking.location)
                }

                // 2. Schedule Card
                DetailCard(title = "Schedule", icon = Icons.Filled.Event) {
                    DetailRow("Dates", booking.dateRange)
                    DetailRow("Duration", booking.durationInDays)
                }

                // 3. Payment Card
                DetailCard(title = "Payment", icon = Icons.Filled.Payment) {
                    DetailRow("Total Paid", "₹${booking.totalCharge}", isBold = true, color = Color(0xFF4CAF50))
                    DetailRow("Status", booking.status)
                }

                // 4. Rating & Feedback (Only if completed)
                if (booking.userRating != null) {
                    DetailCard(title = "Feedback", icon = Icons.Outlined.Star) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = if (index < booking.userRating) Color(0xFFFFB300) else Color.LightGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${booking.userRating}/5.0", fontWeight = FontWeight.Bold)
                        }
                        if (!booking.userFeedback.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "\"${booking.userFeedback}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = BrownPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrownPrimary)
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, isBold: Boolean = false, color: Color = Color.Black) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if(isBold) FontWeight.Bold else FontWeight.Normal,
            color = if(isBold) color else Color.Black
        )
    }
}