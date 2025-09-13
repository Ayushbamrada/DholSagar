// file: com/dholsagar/app/presentation/provider_details/ProviderDetailsScreen.kt
package com.dholsagar.app.presentation.provider_details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dholsagar.app.presentation.onboarding_provider.TeamMember

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProviderDetailsScreen(
    navController: NavController,
    viewModel: ProviderDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            // The "Book Now" button will always be visible at the bottom
            BottomAppBar {
                Button(
                    onClick = { /* TODO: Navigate to Booking Calendar */ },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text("Book Now")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.provider != null) {
                val provider = state.provider!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // --- Image Gallery Pager ---
                    item {
                        val pagerState = rememberPagerState(pageCount = { provider.portfolioImageUrls.size })
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth().height(250.dp)
                        ) { page ->
                            AsyncImage(
                                model = provider.portfolioImageUrls[page],
                                contentDescription = "Portfolio Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // --- Info Section ---
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(provider.bandName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(provider.location, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoChip("⭐ ${provider.avgRating} Rating")
                                InfoChip("${provider.experienceYears}+ Yrs Exp.")
                            }
                            Divider(modifier = Modifier.padding(vertical = 24.dp))

                            // --- Team Members Section ---
                            Text("Team Members", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            provider.teamMembers.forEach { member ->
                                TeamMemberItem(member)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(text: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(text = text, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun TeamMemberItem(member: TeamMember) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(member.name, fontWeight = FontWeight.Bold)
        Text(" - ${member.role}", style = MaterialTheme.typography.bodyMedium)
    }
}