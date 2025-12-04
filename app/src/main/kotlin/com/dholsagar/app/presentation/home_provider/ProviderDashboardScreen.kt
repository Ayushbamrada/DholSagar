//// file: com/dholsagar/app/presentation/home_provider/ProviderDashboardScreen.kt
//package com.dholsagar.app.presentation.home_provider
//
//import android.widget.Toast
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.Info
//import androidx.compose.material.icons.filled.Add // <-- IMPORT FIX
//import androidx.compose.material.icons.outlined.* // <-- IMPORT FIX (gets all outlined icons)
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil.compose.AsyncImage
//
//@Composable
//fun ProviderDashboardScreen(
//    viewModel: ProviderDashboardViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsState()
//    val provider = state.provider
//    val context = LocalContext.current
//
//    // --- Listen for Save Success ---
//    LaunchedEffect(key1 = state.saveSuccess) {
//        if (state.saveSuccess) {
//            Toast.makeText(context, "Details Saved!", Toast.LENGTH_SHORT).show()
//            viewModel.onSaveSuccessShown()
//        }
//    }
//
//    LazyColumn(
//        modifier = Modifier.fillMaxSize(),
//        contentPadding = PaddingValues(16.dp),
//        verticalArrangement = Arrangement.spacedBy(20.dp)
//    ) {
//        if (state.isLoading) {
//            item {
//                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator()
//                }
//            }
//        } else if (provider != null) {
//            item {
//                Text(
//                    "Welcome, ${provider.leadProviderName}!",
//                    style = MaterialTheme.typography.headlineMedium
//                )
//            }
//
//            item { KycStatusCard(status = provider.kycStatus) }
//
//            item { AdminOfferCard() } // The hardcoded admin offer
//
//            item {
//                MyServicesCard(
//                    viewModel = viewModel,
//                    isSaving = state.isSaving
//                )
//            }
//
//            item {
//                ManagePortfolioCard(
//                    images = provider.portfolioImageUrls,
//                    videoUrl = provider.portfolioVideoUrl
//                )
//            }
//
//        } else if (state.error != null) {
//            item {
//                Text(
//                    "Error: ${state.error}",
//                    color = MaterialTheme.colorScheme.error
//                )
//            }
//        }
//    }
//}
//
//// --- Card Composables ---
//
//@Composable
//fun KycStatusCard(status: String) {
//    // THIS IS THE FIX: Correctly declared variables, not using destructuring
//    val backgroundColor: Color
//    val contentColor: Color
//    val icon: ImageVector
//    val text: String
//
//    when (status) {
//        "APPROVED" -> {
//            backgroundColor = Color(0xFFE6F4EA)
//            contentColor = Color(0xFF1E8E3E)
//            icon = Icons.Outlined.CheckCircle
//            text = "Your KYC is Approved!"
//        }
//        "PENDING" -> {
//            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
//            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
//            icon = Icons.Outlined.Warning
//            text = "Your KYC is Pending Review"
//        }
//        else -> { // REJECTED or other
//            backgroundColor = MaterialTheme.colorScheme.errorContainer
//            contentColor = MaterialTheme.colorScheme.onErrorContainer
//            icon = Icons.Outlined.Warning
//            text = "Your KYC was Rejected"
//        }
//    }
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(containerColor = backgroundColor, contentColor = contentColor)
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(imageVector = icon, contentDescription = null)
//            Spacer(modifier = Modifier.width(16.dp))
//            // This Text composable is now correct
//            Text(text = text, style = MaterialTheme.typography.titleMedium)
//        }
//    }
//}
//
//@Composable
//fun AdminOfferCard() {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = Icons.Filled.Info,
//                contentDescription = "Offer",
//                tint = MaterialTheme.colorScheme.onPrimaryContainer
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Column {
//                Text(
//                    "Special Offer!",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//                Text(
//                    "Give 30% off this weekend to get a top spot on the user home screen.",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun MyServicesCard(viewModel: ProviderDashboardViewModel, isSaving: Boolean) {
//    val description by viewModel.description.collectAsState()
//    val specialty by viewModel.specialty.collectAsState()
//    val perDayCharge by viewModel.perDayCharge.collectAsState()
//    val chargeDescription by viewModel.chargeDescription.collectAsState()
//
//    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
//        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
//            Text("My Services & Pricing", style = MaterialTheme.typography.titleLarge)
//
//            OutlinedTextField(
//                value = perDayCharge,
//                onValueChange = viewModel::onPerDayChargeChange,
//                label = { Text("Per Day Charge (e.g., 3000)") },
//                modifier = Modifier.fillMaxWidth(),
//                leadingIcon = { Icon(Icons.Outlined.Create, "Rupee") } // <-- IMPORT FIX
//            )
//
//            OutlinedTextField(
//                value = chargeDescription,
//                onValueChange = viewModel::onChargeDescriptionChange,
//                label = { Text("Charge Description (e.g., per jagar)") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            OutlinedTextField(
//                value = specialty,
//                onValueChange = viewModel::onSpecialtyChange,
//                label = { Text("My Specialty (e.g., Traditional Dhol)") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            OutlinedTextField(
//                value = description,
//                onValueChange = viewModel::onDescriptionChange,
//                label = { Text("About Me / My Band") },
//                modifier = Modifier.fillMaxWidth().height(100.dp),
//                maxLines = 4
//            )
//
//            Button(
//                onClick = viewModel::onSaveDetails,
//                enabled = !isSaving,
//                modifier = Modifier.align(Alignment.End)
//            ) {
//                if(isSaving) {
//                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
//                } else {
//                    Text("Save Details")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ManagePortfolioCard(images: List<String>, videoUrl: String?) {
//    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
//        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("Manage Portfolio", style = MaterialTheme.typography.titleLarge)
//                Button(onClick = { /* TODO: Handle Upload New */ }) {
//                    Icon(Icons.Filled.Add, contentDescription = "Upload") // <-- IMPORT FIX
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Add New")
//                }
//            }
//
//            Text("My Images", style = MaterialTheme.typography.titleMedium)
//            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                items(images) { imageUrl ->
//                    PortfolioItem(
//                        url = imageUrl,
//                        onDelete = { /* TODO: Handle Delete Image */ }
//                    )
//                }
//            }
//
//            videoUrl?.let {
//                Text("My Video", style = MaterialTheme.typography.titleMedium)
//                PortfolioItem(
//                    url = it, // You can show a thumbnail here
//                    isImage = false,
//                    onDelete = { /* TODO: Handle Delete Video */ }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun PortfolioItem(url: String, isImage: Boolean = true, onDelete: () -> Unit) {
//    Box(modifier = Modifier.size(100.dp)) {
//        AsyncImage(
//            model = url,
//            contentDescription = "Portfolio Item",
//            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
//            contentScale = ContentScale.Crop
//        )
//        if (!isImage) {
//            // Overlay for video
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Black.copy(alpha = 0.4f)),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    Icons.Outlined.CheckCircle, // <-- IMPORT FIX
//                    contentDescription = "Video",
//                    tint = Color.White,
//                    modifier = Modifier.size(40.dp)
//                )
//            }
//        }
//        IconButton(
//            onClick = onDelete,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(4.dp)
//                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
//                .size(20.dp)
//        ) {
//            Icon(
//                Icons.Filled.Close,
//                contentDescription = "Delete",
//                tint = Color.White,
//                modifier = Modifier.padding(2.dp)
//            )
//        }
//    }
//}

package com.dholsagar.app.presentation.home_provider

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dholsagar.app.core.navigation.Route
import com.dholsagar.app.domain.model.AdBanner
import com.dholsagar.app.domain.model.ServiceProvider

// --- Custom Theme Colors ---
val BrownPrimary = Color(0xFF5D4037)
val BrownLight = Color(0xFFD7CCC8)
val OffWhite = Color(0xFFFAFAFA)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProviderDashboardScreen(
    navController: NavController,
    viewModel: ProviderDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DholSagar",
                            style = MaterialTheme.typography.titleLarge,
                            color = BrownPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        state.provider?.let {
                            Text(
                                text = "Hello, ${it.leadProviderName}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                        }
                    }
                },
                actions = {
                    // Notification Icon
                    IconButton(onClick = { /* TODO: Open Notifications */ }) {
                        BadgedBox(badge = { Badge { Text("3") } }) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Alerts", tint = BrownPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
            )
        },
        containerColor = OffWhite
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrownPrimary)
                }
            } else {
                // 1. Dynamic Ad Banner
                state.adBanner?.let { ad ->
                    if (ad.isActive) {
                        DynamicAdCard(ad)
                    }
                }

                // 2. Services & Pricing Section
                ServicesSection(
                    provider = state.provider,
                    onEditClick = { navController.navigate(Route.PROVIDER_EDIT_SERVICES) }
                )

                // 3. Portfolio Section
                PortfolioSection(
                    provider = state.provider,
                    onManageClick = { navController.navigate(Route.PROVIDER_MANAGE_PORTFOLIO) }
                )
            }
        }
    }
}

@Composable
fun DynamicAdCard(ad: AdBanner) {
    Card(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image or Gradient
            if (ad.imageUrl != null) {
                AsyncImage(
                    model = ad.imageUrl,
                    contentDescription = "Offer",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Dark Overlay for text readability
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
            } else {
                // Fallback Gradient
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.horizontalGradient(listOf(BrownPrimary, Color(0xFF8D6E63)))
                    )
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
            ) {
                Text(
                    text = ad.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ad.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun ServicesSection(provider: ServiceProvider?, onEditClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Services & Pricing", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrownPrimary)
            TextButton(onClick = onEditClick) {
                Text(if (provider?.perDayCharge.isNullOrBlank()) "Add Details" else "Edit", color = BrownPrimary)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (provider == null || (provider.perDayCharge.isBlank() && provider.description.isBlank())) {
                    // Empty State
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        Text("Add your pricing to get bookings", color = Color.Gray)
                        Button(
                            onClick = onEditClick,
                            colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Add Now")
                        }
                    }
                } else {
                    // Data State
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CurrencyRupee, null, tint = BrownPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${provider.perDayCharge}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BrownPrimary
                        )
                        Text(
                            text = " / ${provider.chargeDescription.ifBlank { "day" }}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    if (provider.specialty.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Verified, null, tint = Color(0xFFFFA000), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(provider.specialty, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (provider.description.isNotBlank()) {
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                        Text(
                            text = provider.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray,
                            maxLines = 3
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PortfolioSection(provider: ServiceProvider?, onManageClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Portfolio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrownPrimary)
            TextButton(onClick = onManageClick) {
                Text("Manage", color = BrownPrimary)
            }
        }

        if (provider?.portfolioImageUrls.isNullOrEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                colors = CardDefaults.cardColors(containerColor = BrownLight.copy(alpha = 0.3f)),
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No images uploaded yet", color = BrownPrimary)
                }
            }
        } else {
            // Image Carousel
            val pagerState = rememberPagerState(pageCount = { provider!!.portfolioImageUrls.size })

            Card(
                modifier = Modifier.fillMaxWidth().height(220.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box {
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        AsyncImage(
                            model = provider!!.portfolioImageUrls[page],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Page Indicator
                    Row(
                        Modifier
                            .height(50.dp)
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pagerState.pageCount) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Video Preview (Mini Player)
        if (provider?.portfolioVideoUrl != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Featured Video", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    // Simple VideoView for Dashboard Preview
                    AndroidView(
                        factory = { context ->
                            VideoView(context).apply {
                                setVideoURI(Uri.parse(provider.portfolioVideoUrl))
                                seekTo(100) // Show first frame
                                // Don't auto play on dashboard to save data/battery
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    // Play Icon Overlay
                    Icon(
                        imageVector = Icons.Filled.PlayCircleFilled,
                        contentDescription = "Play",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(64.dp).align(Alignment.Center)
                    )
                }
            }
        }
    }
}