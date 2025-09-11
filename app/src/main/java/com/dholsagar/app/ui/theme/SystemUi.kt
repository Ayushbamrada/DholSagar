// file: com/dholsagar/app/presentation/ui/theme/SystemUi.kt
package com.dholsagar.app.presentation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SetSystemBarColor(
    color: Color,
    darkIcons: Boolean = true // Most light backgrounds need dark icons
) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = color,
            darkIcons = darkIcons
        )
    }
}