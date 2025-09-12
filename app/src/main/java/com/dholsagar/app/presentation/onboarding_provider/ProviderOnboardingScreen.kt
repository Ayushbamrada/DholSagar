//// file: com/dholsagar/app/presentation/onboarding_provider/ProviderOnboardingScreen.kt
//package com.dholsagar.app.presentation.onboarding_provider
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.dholsagar.app.presentation.onboarding_provider.components.KycPage
//import com.dholsagar.app.presentation.onboarding_provider.components.PersonalDetailsPage
//import com.dholsagar.app.presentation.onboarding_provider.components.PortfolioPage
//import com.dholsagar.app.presentation.onboarding_provider.components.SkillsAndExperiencePage
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun ProviderOnboardingScreen(
//    navController: NavController,
//    viewModel: ProviderOnboardingViewModel = hiltViewModel()
//) {
//    val pagerState = rememberPagerState(pageCount = { viewModel.pagerSteps.size })
//    val scope = rememberCoroutineScope()
//
//    // Collect state from ViewModel
//    val name by viewModel.name.collectAsState()
//    val bandName by viewModel.bandName.collectAsState()
//    val experience by viewModel.experience.collectAsState()
//    val teamMembers by viewModel.teamMembers.collectAsState()
//
//    Scaffold(
//        bottomBar = {
//            Row(
//                modifier = Modifier.fillMaxWidth().padding(16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Button(
//                    onClick = {
//                        scope.launch {
//                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
//                        }
//                    },
//                    enabled = pagerState.currentPage > 0
//                ) {
//                    Text("Back")
//                }
//                Button(onClick = {
//                    if (pagerState.currentPage < viewModel.pagerSteps.size - 1) {
//                        scope.launch {
//                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
//                        }
//                    } else {
//                        // TODO: Final Submit Logic
//                    }
//                }) {
//                    Text(if (pagerState.currentPage == viewModel.pagerSteps.size - 1) "Submit" else "Next")
//                }
//            }
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .safeDrawingPadding()
//                .padding(16.dp)
//        ) {
//            Text("Become a Provider", style = MaterialTheme.typography.headlineMedium)
//            Spacer(modifier = Modifier.height(8.dp))
//            TabRow(selectedTabIndex = pagerState.currentPage) {
//                viewModel.pagerSteps.forEachIndexed { index, title ->
//                    Tab(
//                        selected = pagerState.currentPage == index,
//                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
//                        text = { Text(text = title) }
//                    )
//                }
//            }
//
//            HorizontalPager(
//                state = pagerState,
//                modifier = Modifier.weight(1f)
//            ) { page ->
//                when (page) {
//                    0 -> PersonalDetailsPage(
//                        name = name,
//                        onNameChange = viewModel::onNameChange,
//                        bandName = bandName,
//                        onBandNameChange = viewModel::onBandNameChange
//                    )
//                    1 -> SkillsAndExperiencePage(
//                        experience = experience,
//                        onExperienceChange = viewModel::onExperienceChange,
//                        teamMembers = teamMembers,
//                        onAddMember = viewModel::addTeamMember
//                    )
//                    2 -> PortfolioPage() // ADD THIS
//                    3 -> KycPage()
//                }
//            }
//        }
//    }
//}


// file: com/dholsagar/app/presentation/onboarding_provider/ProviderOnboardingScreen.kt
package com.dholsagar.app.presentation.onboarding_provider

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dholsagar.app.core.navigation.Route
import com.dholsagar.app.presentation.onboarding_provider.components.AddTeamMemberDialog
import com.dholsagar.app.presentation.onboarding_provider.components.KycPage
import com.dholsagar.app.presentation.onboarding_provider.components.PersonalDetailsPage
import com.dholsagar.app.presentation.onboarding_provider.components.PortfolioPage
import com.dholsagar.app.presentation.onboarding_provider.components.SkillsAndExperiencePage
import com.dholsagar.app.presentation.onboarding_provider.components.pagerTabIndicatorOffset
import kotlinx.coroutines.launch
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProviderOnboardingScreen(
    navController: NavController,
    viewModel: ProviderOnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { viewModel.pagerSteps.size })
    val scope = rememberCoroutineScope()
    val showAddMemberDialog by viewModel.showAddMemberDialog.collectAsState()

    // Collect ALL state from ViewModel
    val name by viewModel.name.collectAsState()
    val bandName by viewModel.bandName.collectAsState()
    val role by viewModel.role.collectAsState()
    val gmail by viewModel.gmail.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val experience by viewModel.experience.collectAsState()
    val teamMembers by viewModel.teamMembers.collectAsState()
    val portfolioImageUris by viewModel.portfolioImageUris.collectAsState()
    val portfolioVideoUri by viewModel.portfolioVideoUri.collectAsState()
    val youtubeLink by viewModel.youtubeLink.collectAsState()
    val kycDocumentUris by viewModel.kycDocumentUris.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // --- THIS IS THE FIX: Listen for navigation and error events ---
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is OnboardingEvent.Navigate -> {
                    navController.navigate(event.route) {
                        popUpTo(Route.AUTH_GRAPH) { inclusive = true }
                    }
                }
            }
        }
    }
//    LaunchedEffect(key1 = error) {
//        error?.let {
//            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
//            viewModel.onErrorShown()
//        }
//        }


    Scaffold(
        modifier = Modifier
            .fillMaxSize() // MODIFICATION: Added fillMaxSize() here
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Become a Provider",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // MODIFICATION
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                            height = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    divider = {},
                    edgePadding = 16.dp // Adds some padding to the start and end of the tabs list
                ) {
                    viewModel.pagerSteps.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(text = title, style = MaterialTheme.typography.bodyLarge) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                if (pagerState.currentPage > 0) {
                    TextButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }) {
                        Text("Back")
                    }
                } else {
                    Spacer(modifier = Modifier)
                }

                // Next / Submit Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < viewModel.pagerSteps.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            viewModel.onSubmit() // CONNECTED
                        }
                    },
                    enabled = !isLoading // Disable while submitting
                ) {
                    Text(if (pagerState.currentPage == viewModel.pagerSteps.size - 1) "Submit" else "Next")
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalAlignment = Alignment.Top
        ) { page ->
            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it }) + fadeIn() togetherWith
                            slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { -it }) + fadeOut()
                }, label = "page_animation"
            ) { targetPage ->
                when (targetPage) {
                    0 -> PersonalDetailsPage(
                        name = name,
                        onNameChange = viewModel::onNameChange,
                        bandName = bandName,
                        onBandNameChange = viewModel::onBandNameChange,
                        gmail = gmail,
                        onGmailChange = viewModel::onGmailChange,
                        role = role,
                        onRoleChange = viewModel::onRoleChange,phone = phone, onPhoneChange = viewModel::onPhoneChange, // Pass new state
                        isPhoneEditable = viewModel.isPhoneEditable, // Pass new flags
                        isEmailEditable = viewModel.isEmailEditable
                    )
                    1 -> SkillsAndExperiencePage(
                        experience = experience,
                        onExperienceChange = viewModel::onExperienceChange,
                        teamMembers = teamMembers,
                        onAddMemberClicked = viewModel::onShowAddMemberDialog
                    )
                    2 -> PortfolioPage(
                        selectedImageUris = portfolioImageUris,
                        onImagesSelected = viewModel::onPortfolioImagesSelected,
                        onRemoveImage = viewModel::onRemovePortfolioImage,
                        selectedVideoUri = portfolioVideoUri,
                        onVideoSelected = viewModel::onPortfolioVideoSelected,
                        youtubeLink = youtubeLink,
                        onYoutubeLinkChange = viewModel::onYoutubeLinkChange
                    )
                    3 -> KycPage(
                        providerName = name,
                        teamMembers = teamMembers,
                        selectedKycUris = kycDocumentUris,
                        onDocumentSelected = viewModel::onKycDocumentSelected,
                        onRemoveDocument = viewModel::onRemoveKycDocument
                    )
                }
            }
        }
    }

    if (showAddMemberDialog) {
        AddTeamMemberDialog(
            onDismiss = viewModel::onDismissAddMemberDialog,
            onAddMember = viewModel::addTeamMember
        )
    }
}