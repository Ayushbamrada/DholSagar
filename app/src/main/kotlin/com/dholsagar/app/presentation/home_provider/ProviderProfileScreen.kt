// file: com/dholsagar/app/presentation/home_provider/ProviderProfileScreen.kt
package com.dholsagar.app.presentation.home_provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dholsagar.app.core.navigation.Route

@Composable
fun ProviderProfileScreen(
    rootNavController: NavController,
    viewModel: ProviderProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Listen for the Logout event
    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when(event) {
                is ProfileEvent.NavigateToLogin -> {
                    // Navigate back to the very start (User Selection) and clear back stack
                    rootNavController.navigate(Route.USER_TYPE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // --- Header ---
        item {
            ProfileHeader(state)
        }

        // --- Account Section ---
        item {
            SectionHeader("Account")
            ProfileMenuItem(
                icon = Icons.Outlined.Person,
                title = "Profile Details",
                subtitle = "Name, Phone, Email, Gender",
                onClick = { /* TODO: Navigate to Edit Profile */ }
            )
        }

        // --- General Section ---
        item {
            SectionHeader("General")
            ProfileMenuItem(
                icon = Icons.Outlined.Star,
                title = "Feedback",
                onClick = { /* TODO */ }
            )
            ProfileMenuItem(
                icon = Icons.Outlined.Share,
                title = "Refer a Friend",
                onClick = { /* TODO */ }
            )
        }

        // --- Support Section ---
        item {
            SectionHeader("Support")
            ProfileMenuItem(
                icon = Icons.Outlined.Description,
                title = "Terms & Conditions",
                onClick = { /* TODO */ }
            )
            ProfileMenuItem(
                icon = Icons.Outlined.PrivacyTip,
                title = "Privacy Policy",
                onClick = { /* TODO */ }
            )
            ProfileMenuItem(
                icon = Icons.Outlined.QuestionAnswer,
                title = "FAQs",
                onClick = { /* TODO */ }
            )
            ProfileMenuItem(
                icon = Icons.Outlined.SupportAgent,
                title = "Chat with Us",
                onClick = { /* TODO */ }
            )
        }

        // --- Logout Button ---
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = viewModel::onLogoutClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out")
            }
        }
    }
}

@Composable
fun ProfileHeader(state: ProfileState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.user?.name?.take(1)?.uppercase() ?: "?",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = state.user?.name ?: "Guest",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = state.user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.outline
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    )
}