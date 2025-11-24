// file: com/dholsagar/app/presentation/main/BottomNavItem.kt
package com.dholsagar.app.presentation.main

import androidx.compose.ui.graphics.vector.ImageVector
import com.dholsagar.app.core.navigation.Route

data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)