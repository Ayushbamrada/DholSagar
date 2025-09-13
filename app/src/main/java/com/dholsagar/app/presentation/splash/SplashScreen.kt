//// file: com/dholsagar/app/presentation/splash/SplashScreen.kt
//package com.dholsagar.app.presentation.splash
//
//import androidx.compose.animation.core.*
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.dholsagar.app.R
//import com.dholsagar.app.presentation.ui.theme.SetSystemBarColor
//import kotlinx.coroutines.async
//import kotlinx.coroutines.coroutineScope
//import kotlinx.coroutines.delay
//
//@Composable
//fun SplashScreen(
//    navController: NavController,
//    viewModel: SplashViewModel = hiltViewModel()
//) {
//    // Set status bar color to white with dark icons
//    SetSystemBarColor(color = Color.White)
//
//    var startAnimation by remember { mutableStateOf(false) }
//    var animationFinished by remember { mutableStateOf(false) }
//
//    // Animation states for the image
//    val scale by animateFloatAsState(
//        targetValue = if (startAnimation) 1f else 0.5f,
//        animationSpec = tween(durationMillis = 1500),
//        label = "scale"
//    )
//    val alpha by animateFloatAsState(
//        targetValue = if (startAnimation) 1f else 0f,
//        animationSpec = tween(durationMillis = 1500),
//        label = "alpha"
//    )
//
//    // Typewriter text states
//    val line1 = "अपनी संस्कृति, अपनी पहचान"
//    val line2 = "ढोल - दमाऊ"
//    var text1 by remember { mutableStateOf("") }
//    var text2 by remember { mutableStateOf("") }
//
//    val isLoading by viewModel.isLoading.collectAsState()
//    val startDestination by viewModel.startDestination.collectAsState()
//
//    // Trigger for all animations
//    LaunchedEffect(key1 = true) {
//        startAnimation = true
//        // Run typewriter effect in a parallel coroutine
//        coroutineScope {
//            async {
//                line1.forEachIndexed { index, _ ->
//                    text1 = line1.substring(0, index + 1)
//                    delay(100)
//                }
//                delay(200)
//                line2.forEachIndexed { index, _ ->
//                    text2 = line2.substring(0, index + 1)
//                    delay(120)
//                }
//                // Signal that the text animation is done
//                animationFinished = true
//            }
//        }
//    }
//
//    // Navigation logic: Triggers only when BOTH animations are finished AND data is loaded.
//    LaunchedEffect(key1 = isLoading, key2 = animationFinished) {
//        if (!isLoading && animationFinished) {
//            // Add a small extra delay for a smoother transition
//            delay(300)
//            navController.popBackStack()
//            navController.navigate(startDestination)
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.TopCenter)
//                .padding(top = 80.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = text1,
//                color = Color.Black,
//                fontSize = 26.sp,
//                fontWeight = FontWeight.Bold,
//                style = MaterialTheme.typography.headlineMedium
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = text2,
//                color = Color.Black,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                style = MaterialTheme.typography.headlineSmall
//            )
//        }
//
//        Image(
//            painter = painterResource(id = R.drawable.dhol_damau),
//            contentDescription = "App Logo",
//            modifier = Modifier
//                .size(250.dp)
//                .scale(scale)
//                .alpha(alpha)
//        )
//    }
//}

package com.dholsagar.app.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.dholsagar.app.R
import com.dholsagar.app.presentation.ui.theme.SetSystemBarColor
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    // Set status bar color to white with dark icons
    SetSystemBarColor(color = Color.White)

    var startAnimation by remember { mutableStateOf(false) }
    var animationFinished by remember { mutableStateOf(false) }

    // Animation states for the image
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1500),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "alpha"
    )

    // Typewriter text states
    val line1 = "अपनी संस्कृति, अपनी पहचान"
    val line2 = "ढोल - दमाऊ"
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }

    val event by viewModel.eventFlow.collectAsState()

    // Trigger for all animations
    LaunchedEffect(key1 = true) {
        startAnimation = true
        // Run typewriter effect in a parallel coroutine
        coroutineScope {
            async {
                line1.forEachIndexed { index, _ ->
                    text1 = line1.substring(0, index + 1)
                    delay(100)
                }
                delay(200)
                line2.forEachIndexed { index, _ ->
                    text2 = line2.substring(0, index + 1)
                    delay(120)
                }
                // Signal that the text animation is done
                animationFinished = true
            }
        }
    }

    // UPDATED Navigation logic: Triggers when animations are finished and an event is received.
    // Uses the modern popUpTo method to clear the back stack.
    LaunchedEffect(key1 = event, key2 = animationFinished) {
        if (animationFinished) {
            when (val currentEvent = event) {
                is SplashEvent.Navigate -> {
                    delay(300) // Keep small delay for smoother UX
                    navController.navigate(currentEvent.route) {
                        // Pop up to the start destination of the graph to remove splash screen from back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
                null -> {
                    // Event hasn't been emitted yet, do nothing.
                }
            }
        }
    }

    // UPDATED UI: Using a single centered Column for better responsiveness.
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Text block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = text1,
                    color = Color.Black,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = text2,
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            // Image with animations
            Image(
                painter = painterResource(id = R.drawable.dhol_damau),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(250.dp)
                    .scale(scale)
                    .alpha(alpha)
            )
        }
    }
}

