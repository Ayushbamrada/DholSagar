//// file: com/dholsagar/app/presentation/home_user/UserHomeScreen.kt
//package com.dholsagar.app.presentation.home_user
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil.compose.AsyncImage
//import com.dholsagar.app.domain.model.ServiceProvider
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun UserHomeScreen(
//    // navController: NavController, // Will be needed later
//    viewModel: UserHomeViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("DholSagar Artists") })
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier.fillMaxSize().padding(paddingValues)
//        ) {
//            if (state.isLoading) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            }
//
//            if (state.providers.isEmpty() && !state.isLoading) {
//                Text(
//                    text = "No providers found.",
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//
//            LazyColumn(
//                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(state.providers) { provider ->
//                    ProviderCard(
//                        provider = provider,
//                        onClick = { /* TODO: Navigate to provider details screen */ }
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ProviderCard(provider: ServiceProvider, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column {
//            AsyncImage(
//                model = provider.portfolioImageUrls.firstOrNull(),
//                contentDescription = "${provider.bandName} portfolio image",
//                modifier = Modifier.fillMaxWidth().height(180.dp),
//                contentScale = ContentScale.Crop,
//                // placeholder = painterResource(id = R.drawable.placeholder) // Optional
//            )
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(
//                    text = provider.bandName,
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = provider.location,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//        }
//    }
//}

// file: com/dholsagar/app/presentation/home_user/UserHomeScreen.kt
package com.dholsagar.app.presentation.home_user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dholsagar.app.R
import com.dholsagar.app.core.navigation.Screen
import com.dholsagar.app.domain.model.ServiceProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    navController: NavController, // Add NavController for future navigation
    viewModel: UserHomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DholSagar Artists") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.providers.isEmpty()) {
                Text(
                    text = "No providers found yet. Be the first to see new artists!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.providers) { provider ->
                        ProviderCard(
                            provider = provider,
                            onClick = {
                                // THIS IS THE FIX: Navigate with the provider's UID
                                navController.navigate(
                                    Screen.ProviderDetailsScreen.createRoute(provider.uid)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderCard(provider: ServiceProvider, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = provider.portfolioImageUrls.firstOrNull(),
                contentDescription = "${provider.bandName} portfolio image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_background), // A placeholder image
                error = painterResource(id = R.drawable.ic_launcher_background) // An image to show on error
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = provider.bandName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = provider.location, // Assuming location is a field in your model
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}