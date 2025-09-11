//// file: com/dholsagar/app/presentation/auth/UserTypeSelectionScreen.kt
//package com.dholsagar.app.presentation.auth
//
//import androidx.compose.animation.core.*
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.interaction.collectIsPressedAsState
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.dholsagar.app.R
//import com.dholsagar.app.core.navigation.Screen
//import com.dholsagar.app.presentation.ui.theme.SetSystemBarColor
//import kotlinx.coroutines.delay
//
//@Composable
//fun UserTypeSelectionScreen(navController: NavController) {
//    // Set status bar to the surface color for a seamless look
//    SetSystemBarColor(color = MaterialTheme.colorScheme.surface)
//
//    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(24.dp)
//                .safeDrawingPadding(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.weight(1f))
//
//            Image(
//                painter = painterResource(id = R.drawable.dhol_damau),
//                contentDescription = "DholSagar Logo",
//                modifier = Modifier.size(100.dp)
//            )
//            Spacer(modifier = Modifier.height(24.dp))
//            Text(
//                text = "Welcome to DholSagar",
//                style = MaterialTheme.typography.headlineLarge,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = "How would you like to join our community?",
//                style = MaterialTheme.typography.bodyLarge,
//                textAlign = TextAlign.Center,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//
//            Spacer(modifier = Modifier.weight(1.5f))
//
//            // Animated Selection Cards
//            SelectionCard(
//                title = "I'm looking for Artists",
//                subtitle = "Book authentic cultural bands for your event.",
//                iconResId = R.drawable.baseline_person_24,
//                onClick = {
//                    navController.navigate(Screen.AuthScreen.createRoute("USER"))
//                },
//                delay = 0L // First card appears immediately
//            )
//            Spacer(modifier = Modifier.height(24.dp))
//            SelectionCard(
//                title = "I'm a Service Provider",
//                subtitle = "List your band and get bookings from across the state.",
//                iconResId = R.drawable.baseline_storefront_24,
//                onClick = {
//                    navController.navigate(Screen.AuthScreen.createRoute("PROVIDER"))
//                },
//                delay = 200L // Second card animates in slightly later
//            )
//
//            Spacer(modifier = Modifier.weight(2f))
//        }
//    }
//}
//
//@Composable
//fun SelectionCard(
//    title: String,
//    subtitle: String,
//    iconResId: Int,
//    onClick: () -> Unit,
//    delay: Long
//) {
//    var startAnimation by remember { mutableStateOf(false) }
//    val interactionSource = remember { MutableInteractionSource() }
//    val isPressed by interactionSource.collectIsPressedAsState()
//
//    // Animation for card entry (slide up and fade in)
//    val offsetY by animateDpAsState(
//        targetValue = if (startAnimation) 0.dp else 50.dp,
//        animationSpec = spring(
//            dampingRatio = Spring.DampingRatioMediumBouncy,
//            stiffness = Spring.StiffnessLow
//        ),
//        label = "offsetY"
//    )
//    val alpha by animateFloatAsState(
//        targetValue = if (startAnimation) 1f else 0f,
//        animationSpec = tween(durationMillis = 300),
//        label = "alpha"
//    )
//
//    // Animation for press effect (scale down)
//    val scale by animateFloatAsState(
//        targetValue = if (isPressed) 0.98f else 1f,
//        animationSpec = tween(durationMillis = 100),
//        label = "scale"
//    )
//
//    LaunchedEffect(key1 = true) {
//        delay(delay)
//        startAnimation = true
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .scale(scale)
//            .offset(y = offsetY)
//            .alpha(alpha)
//            .clickable(
//                interactionSource = interactionSource,
//                indication = null, // Disable ripple to show our custom scale effect
//                onClick = onClick
//            ),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
//        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Modern flat look
//    ) {
//        Row(
//            modifier = Modifier.padding(24.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                painter = painterResource(id = iconResId),
//                contentDescription = null,
//                modifier = Modifier.size(40.dp),
//                tint = MaterialTheme.colorScheme.primary
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Column {
//                Text(text = title, style = MaterialTheme.typography.titleLarge)
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = subtitle,
//                    style = MaterialTheme.typography.bodyMedium,
//                    lineHeight = 18.sp,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//        }
//    }
//}
// file: com/dholsagar/app/presentation/auth/UserTypeSelectionScreen.kt
package com.dholsagar.app.presentation.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dholsagar.app.R
import com.dholsagar.app.core.navigation.Screen
import com.dholsagar.app.presentation.ui.theme.SetSystemBarColor
import kotlinx.coroutines.delay

@Composable
fun UserTypeSelectionScreen(navController: NavController) {
    SetSystemBarColor(color = MaterialTheme.colorScheme.surface)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.dhol_damau),
                contentDescription = "DholSagar Logo",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Welcome to DholSagar",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "How would you like to join our community?",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.weight(1.5f))

            SelectionCard(
                title = "I'm looking for Artists",
                subtitle = "Book authentic cultural bands for your event.",
                iconResId = R.drawable.baseline_person_24,
                onClick = {
                    navController.navigate(Screen.AuthScreen.createRoute("USER"))
                },
                delay = 0L
            )
            Spacer(modifier = Modifier.height(24.dp))
            SelectionCard(
                title = "I'm a Service Provider",
                subtitle = "List your band and get bookings from across the state.",
                iconResId = R.drawable.baseline_storefront_24,
                onClick = {
                    navController.navigate(Screen.AuthScreen.createRoute("PROVIDER"))
                },
                delay = 200L
            )

            Spacer(modifier = Modifier.weight(2f))
        }
    }
}

@Composable
fun SelectionCard(
    title: String,
    subtitle: String,
    iconResId: Int,
    onClick: () -> Unit,
    delay: Long
) {
    var startAnimation by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val offsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 50.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "offsetY"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    LaunchedEffect(key1 = true) {
        delay(delay)
        startAnimation = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .offset(y = offsetY)
            .alpha(alpha)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}