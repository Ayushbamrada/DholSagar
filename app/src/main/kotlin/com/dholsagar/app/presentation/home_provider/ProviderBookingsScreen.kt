//package com.dholsagar.app.presentation.home_provider
//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.expandVertically
//import androidx.compose.animation.shrinkVertically
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.BugReport
//import androidx.compose.material.icons.filled.CalendarMonth
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material3.*
//import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.dholsagar.app.core.navigation.Screen
//import com.dholsagar.app.domain.model.Booking
//import java.util.Date
//
//// Colors matching the Brown/White theme
////val BrownPrimary = Color(0xFF5D4037)
//val StatusGreen = Color(0xFF4CAF50)
//val StatusRed = Color(0xFFE53935)
//val StatusYellow = Color(0xFFFFB300)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProviderBookingsScreen(
//    navController: NavController,
//    viewModel: ProviderBookingsViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsState()
//    var selectedTabIndex by remember { mutableStateOf(1) } // Default to "Upcoming"
//    val tabs = listOf("Requests", "Upcoming", "History")
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("My Bookings", fontWeight = FontWeight.Bold, color = BrownPrimary) },
//                actions = {
//                    // --- 1. DUMMY DATA BUTTON (Temporary) ---
//                    IconButton(onClick = { viewModel.createDummyData() }) {
//                        Icon(Icons.Filled.BugReport, "Debug Data", tint = Color.Red)
//                    }
//                    IconButton(onClick = { viewModel.toggleCalendar() }) {
//                        Icon(Icons.Filled.CalendarMonth, "Calendar", tint = if(state.isCalendarVisible) BrownPrimary else Color.Gray)
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
//            )
//        },
//        containerColor = Color(0xFFFAFAFA)
//    ) { padding ->
//        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
//
//            // 1. Calendar View (Collapsible)
//            AnimatedVisibility(
//                visible = state.isCalendarVisible,
//                enter = expandVertically(),
//                exit = shrinkVertically()
//            ) {
//                Card(
//                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
//                    colors = CardDefaults.cardColors(containerColor = Color.White),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                ) {
//                    val datePickerState = rememberDatePickerState()
//                    DatePicker(
//                        state = datePickerState,
//                        title = null,
//                        headline = null,
//                        showModeToggle = false,
//                        colors = DatePickerDefaults.colors(containerColor = Color.White)
//                    )
//                    // Note: Logic to highlight busy dates would go here by customizing the DatePicker colors
//                }
//            }
//
//            // 2. Tabs
//            TabRow(
//                selectedTabIndex = selectedTabIndex,
//                containerColor = Color.White,
//                contentColor = BrownPrimary,
//                indicator = { tabPositions ->
//                    TabRowDefaults.Indicator(
//                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
//                        color = BrownPrimary
//                    )
//                }
//            ) {
//                tabs.forEachIndexed { index, title ->
//                    Tab(
//                        selected = selectedTabIndex == index,
//                        onClick = { selectedTabIndex = index },
//                        text = { Text(title, fontWeight = if(selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
//                    )
//                }
//            }
//
//            // 3. List Content
//            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
//                if (state.isLoading) {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = BrownPrimary)
//                } else {
//                    val listToShow = when(selectedTabIndex) {
//                        0 -> state.requests
//                        1 -> state.upcoming
//                        2 -> state.completed
//                        else -> emptyList()
//                    }
//
//                    if (listToShow.isEmpty()) {
//                        EmptyStateMessage(selectedTabIndex)
//                    } else {
//                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                            items(listToShow) { booking ->
//                                BookingItemCard(
//                                    booking = booking,
//                                    // Pass the ViewModel actions
//                                    onAccept = { viewModel.onAcceptBooking(booking.bookingId) },
//                                    onDecline = { viewModel.onDeclineBooking(booking.bookingId) },
//                                    onClick = { navController.navigate(Screen.ProviderBookingDetailScreen.createRoute(booking.bookingId)) }
//                                )
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun EmptyStateMessage(tabIndex: Int) {
//    val message = when(tabIndex) {
//        0 -> "No new booking requests."
//        1 -> "No upcoming bookings scheduled."
//        else -> "No past booking history."
//    }
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Text(message, color = Color.Gray)
//    }
//}
//
//@Composable
//fun BookingItemCard(
//    booking: Booking,
//    onClick: () -> Unit,
//    onAccept: () -> Unit = {}, // Default empty for non-requests
//    onDecline: () -> Unit = {}
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            // Header: Name & Status
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = booking.userName,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = BrownPrimary
//                )
//                StatusBadge(status = booking.status)
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Date Range
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(Icons.Filled.CalendarMonth, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = booking.dateRange,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.DarkGray
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // --- NEW: ACTION BUTTONS FOR REQUESTS ---
//            if (booking.status == "REQUESTED") {
//                Text(
//                    text = "Expires in 6 hours",
//                    style = MaterialTheme.typography.labelSmall,
//                    color = Color.Red
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    Button(
//                        onClick = onAccept,
//                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
//                        modifier = Modifier.weight(1f).height(36.dp),
//                        contentPadding = PaddingValues(0.dp)
//                    ) {
//                        Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp))
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("Accept")
//                    }
//                    OutlinedButton(
//                        onClick = onDecline,
//                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
//                        border = BorderStroke(1.dp, StatusRed),
//                        modifier = Modifier.weight(1f).height(36.dp),
//                        contentPadding = PaddingValues(0.dp)
//                    ) {
//                        Icon(Icons.Filled.Close, null, modifier = Modifier.size(16.dp))
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("Decline")
//                    }
//                }
//                Spacer(modifier = Modifier.height(12.dp))
//            }
//
//            Divider(color = Color.LightGray.copy(alpha = 0.3f))
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Footer
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = "Paid: ₹${booking.totalCharge.toInt()}",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = StatusGreen
//                )
//                Text(
//                    text = booking.durationInDays,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.Gray
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun StatusBadge(status: String) {
//    val (bgColor, textColor, text) = when (status.uppercase()) {
//        "CONFIRMED" -> Triple(StatusGreen.copy(alpha = 0.1f), StatusGreen, "Upcoming")
//        "COMPLETED" -> Triple(StatusGreen.copy(alpha = 0.1f), StatusGreen, "Completed")
//        "CANCELLED" -> Triple(StatusRed.copy(alpha = 0.1f), StatusRed, "Cancelled")
//        "REQUESTED", "PENDING" -> Triple(StatusYellow.copy(alpha = 0.1f), StatusYellow, "Request")
//        else -> Triple(Color.Gray.copy(alpha = 0.1f), Color.Gray, status)
//    }
//
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(4.dp))
//            .background(bgColor)
//            .padding(horizontal = 8.dp, vertical = 4.dp)
//    ) {
//        Text(
//            text = text,
//            color = textColor,
//            style = MaterialTheme.typography.labelSmall,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}

package com.dholsagar.app.presentation.home_provider

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dholsagar.app.core.navigation.Screen
import com.dholsagar.app.domain.model.Booking
import java.util.Date

// Colors matching the Brown/White theme
// val BrownPrimary = Color(0xFF5D4037)
val StatusGreen = Color(0xFF4CAF50)
val StatusRed = Color(0xFFE53935)
val StatusYellow = Color(0xFFFFB300)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderBookingsScreen(
    navController: NavController,
    viewModel: ProviderBookingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(1) } // Default to "Upcoming"
    val tabs = listOf("Requests", "Upcoming", "History")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings", fontWeight = FontWeight.Bold, color = BrownPrimary) },
                actions = {
                    // --- 1. DUMMY DATA BUTTON (Temporary) ---
                    IconButton(onClick = { viewModel.createDummyData() }) {
                        Icon(Icons.Filled.BugReport, "Debug Data", tint = Color.Red)
                    }
                    IconButton(onClick = { viewModel.toggleCalendar() }) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            "Calendar",
                            tint = if (state.isCalendarVisible) BrownPrimary else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // 1. Calendar View (Collapsible)
            AnimatedVisibility(
                visible = state.isCalendarVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    val datePickerState = rememberDatePickerState()
                    DatePicker(
                        state = datePickerState,
                        title = null,
                        headline = null,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(containerColor = Color.White)
                    )
                    // Note: Logic to highlight busy dates would go here by customizing the DatePicker colors
                }
            }

            // 2. Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = BrownPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = BrownPrimary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // 3. List Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BrownPrimary
                    )
                } else {
                    val listToShow = when (selectedTabIndex) {
                        0 -> state.requests
                        1 -> state.upcoming
                        2 -> state.completed
                        else -> emptyList()
                    }

                    if (listToShow.isEmpty()) {
                        EmptyStateMessage(selectedTabIndex)
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(listToShow) { booking ->
                                BookingItemCard(
                                    booking = booking,
                                    // Pass the ViewModel actions
                                    onAccept = { viewModel.onAcceptBooking(booking.bookingId) },
                                    onDecline = { viewModel.onDeclineBooking(booking.bookingId) },
                                    onClick = {
                                        navController.navigate(
                                            Screen.ProviderBookingDetailScreen.createRoute(
                                                booking.bookingId
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(tabIndex: Int) {
    val message = when (tabIndex) {
        0 -> "No new booking requests."
        1 -> "No upcoming bookings scheduled."
        else -> "No past booking history."
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = Color.Gray)
    }
}

@Composable
fun BookingItemCard(
    booking: Booking,
    onClick: () -> Unit,
    onAccept: () -> Unit = {}, // Default empty for non-requests
    onDecline: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Name & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrownPrimary
                )
                StatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date Range
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = booking.dateRange,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- NEW: ACTION BUTTONS FOR REQUESTS ---
            if (booking.status == "REQUESTED") {
                Text(
                    text = "Expires in 6 hours",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accept")
                    }
                    OutlinedButton(
                        onClick = onDecline,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                        border = BorderStroke(1.dp, StatusRed),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Filled.Close, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Decline")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Paid: ₹${booking.totalCharge.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = StatusGreen
                )
                Text(
                    text = booking.durationInDays,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, text) = when (status.uppercase()) {
        "CONFIRMED" -> Triple(StatusGreen.copy(alpha = 0.1f), StatusGreen, "Upcoming")
        "COMPLETED" -> Triple(StatusGreen.copy(alpha = 0.1f), StatusGreen, "Completed")
        "CANCELLED" -> Triple(StatusRed.copy(alpha = 0.1f), StatusRed, "Cancelled")
        "REQUESTED", "PENDING" -> Triple(StatusYellow.copy(alpha = 0.1f), StatusYellow, "Request")
        else -> Triple(Color.Gray.copy(alpha = 0.1f), Color.Gray, status)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
