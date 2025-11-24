// file: com/dholsagar/app/presentation/onboarding_provider/components/PagerTabIndicatorOffset.kt
package com.dholsagar.app.presentation.onboarding_provider.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.TabPosition
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

// This is a helper function to create a smooth, animated tab indicator
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
): Modifier = composed {
    val currentPage = pagerState.currentPage
    val currentTab = tabPositions[currentPage]
    val previousTab = tabPositions.getOrNull(currentPage - 1)
    val nextTab = tabPositions.getOrNull(currentPage + 1)
    val fraction = pagerState.currentPageOffsetFraction

    val indicatorWidth = if (fraction > 0 && nextTab != null) {
        androidx.compose.ui.unit.lerp(currentTab.width, nextTab.width, fraction)
    } else if (fraction < 0 && previousTab != null) {
        androidx.compose.ui.unit.lerp(currentTab.width, previousTab.width, -fraction)
    } else {
        currentTab.width
    }

    val indicatorOffset = if (fraction > 0 && nextTab != null) {
        androidx.compose.ui.unit.lerp(currentTab.left, nextTab.left, fraction)
    } else if (fraction < 0 && previousTab != null) {
        androidx.compose.ui.unit.lerp(currentTab.left, previousTab.left, -fraction)
    } else {
        currentTab.left
    }

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(indicatorWidth)
}