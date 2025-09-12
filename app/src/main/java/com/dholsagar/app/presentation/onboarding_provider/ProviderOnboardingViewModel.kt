//// file: com/dholsagar/app/presentation/onboarding_provider/ProviderOnboardingViewModel.kt
//package com.dholsagar.app.presentation.onboarding_provider
//
//import android.net.Uri
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.dholsagar.app.core.navigation.Route
//import com.dholsagar.app.core.util.Resource
//import com.dholsagar.app.domain.repository.AuthRepository
//import com.dholsagar.app.domain.repository.ProviderRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.async
//import kotlinx.coroutines.awaitAll
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.receiveAsFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//data class TeamMember(val name: String, val role: String)
//
//sealed class OnboardingEvent {
//    data class Navigate(val route: String) : OnboardingEvent()
//}
//
//@HiltViewModel
//class ProviderOnboardingViewModel @Inject constructor(
//    private val providerRepository: ProviderRepository,
//    private val authRepository: AuthRepository
//) : ViewModel() {
//
//    val pagerSteps = listOf("Personal", "Skills", "Portfolio", "KYC")
//
//    // --- State & Events ---
//    private val _eventChannel = Channel<OnboardingEvent>()
//    val eventFlow = _eventChannel.receiveAsFlow()
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading = _isLoading.asStateFlow()
//    private val _error = MutableStateFlow<String?>(null)
//    val error = _error.asStateFlow()
//
//    // --- Page 1: Personal Details ---
//    private val _name = MutableStateFlow("")
//    val name = _name.asStateFlow()
//    private val _bandName = MutableStateFlow("")
//    val bandName = _bandName.asStateFlow()
//    private val _gmail = MutableStateFlow("")
//    val gmail = _gmail.asStateFlow()
//    private val _role = MutableStateFlow("")
//    val role = _role.asStateFlow()
//
//    // --- Page 2: Skills ---
//    private val _experience = MutableStateFlow("")
//    val experience = _experience.asStateFlow()
//    private val _teamMembers = MutableStateFlow<List<TeamMember>>(emptyList())
//    val teamMembers = _teamMembers.asStateFlow()
//
//    // --- Page 3: Portfolio ---
//    private val _portfolioImageUris = MutableStateFlow<List<Uri>>(emptyList())
//    val portfolioImageUris = _portfolioImageUris.asStateFlow()
//    private val _portfolioVideoUri = MutableStateFlow<Uri?>(null)
//    val portfolioVideoUri = _portfolioVideoUri.asStateFlow()
//    private val _youtubeLink = MutableStateFlow("")
//    val youtubeLink = _youtubeLink.asStateFlow()
//
//    // --- Page 4: KYC ---
//    private val _kycDocumentUris = MutableStateFlow<Map<String, Uri>>(emptyMap())
//    val kycDocumentUris = _kycDocumentUris.asStateFlow()
//
//    // --- Dialog State ---
//    private val _showAddMemberDialog = MutableStateFlow(false)
//    val showAddMemberDialog = _showAddMemberDialog.asStateFlow()
//    private val _newMemberName = MutableStateFlow("")
//    val newMemberName = _newMemberName.asStateFlow()
//    private val _newMemberRole = MutableStateFlow("")
//    val newMemberRole = _newMemberRole.asStateFlow()
//
//    // --- Event Handlers ---
//    fun onNameChange(value: String) { _name.value = value }
//    fun onBandNameChange(value: String) { _bandName.value = value }
//    fun onGmailChange(value: String) { _gmail.value = value }
//    fun onRoleChange(value: String) { _role.value = value }
//    fun onExperienceChange(value: String) { _experience.value = value }
//    fun onYoutubeLinkChange(value: String) { _youtubeLink.value = value }
//    fun onPortfolioImagesSelected(uris: List<Uri>) {
//        // Only add up to a max limit, e.g., 10 photos
//        val currentCount = _portfolioImageUris.value.size
//        val remainingSpace = 10 - currentCount
//        if (remainingSpace > 0) {
//            _portfolioImageUris.update { it + uris.take(remainingSpace) }
//        }
//    }
//
//    // ADD THIS FUNCTION TO REMOVE AN IMAGE
//    fun onRemovePortfolioImage(uri: Uri) {
//        _portfolioImageUris.update { it - uri }
//    }
//    fun onPortfolioVideoSelected(uri: Uri?) { _portfolioVideoUri.value = uri }
//    fun onKycDocumentSelected(docId: String, uri: Uri) { _kycDocumentUris.update { it + (docId to uri) } }
//    fun onErrorShown() { _error.value = null }
//    fun onRemoveKycDocument(docId: String) {
//        _kycDocumentUris.update {
//            val mutableMap = it.toMutableMap()
//            mutableMap.remove(docId)
//            mutableMap
//        }
//    }
//    fun onShowAddMemberDialog() { _showAddMemberDialog.value = true }
//    fun onDismissAddMemberDialog() { _showAddMemberDialog.value = false }
//    fun onNewMemberNameChange(value: String) { _newMemberName.value = value }
//    fun onNewMemberRoleChange(value: String) { _newMemberRole.value = value }
//    fun addTeamMember(name: String, role: String) {
//        if (name.isNotBlank() && role.isNotBlank()) {
//            _teamMembers.update { it + TeamMember(name, role) }
//            _newMemberName.value = ""
//            _newMemberRole.value = ""
//            _showAddMemberDialog.value = false
//        }
//    }
//
//    fun onSubmit() {
//        viewModelScope.launch {
//            // --- VALIDATION ---
//            if (_portfolioImageUris.value.size < 5) {
//                _error.value = "Please upload at least 5 portfolio photos."
//                return@launch
//            }
//            // Add other validation as needed
//
//            _isLoading.value = true
//            val uid = authRepository.currentUser?.uid
//            if (uid == null) {
//                _error.value = "User not found. Please log in again."
//                _isLoading.value = false
//                return@launch
//            }
//
//            // Step 1: Upload all files concurrently for max speed
//            val portfolioImageUrlsDeferred = _portfolioImageUris.value.map { uri ->
//                async { providerRepository.uploadFile(uri, "portfolio_images/$uid") }
//            }
//            val portfolioVideoUrlDeferred = _portfolioVideoUri.value?.let { uri ->
//                async { providerRepository.uploadFile(uri, "portfolio_videos/$uid") }
//            }
//            // Add KYC upload logic here if needed, similar to above
//
//            val portfolioImageResults = portfolioImageUrlsDeferred.awaitAll()
//            val portfolioVideoResult = portfolioVideoUrlDeferred?.await()
//
//            // Step 2: Check for any upload failures
//            val failedUploads = portfolioImageResults.any { it is Resource.Error } || (portfolioVideoResult != null && portfolioVideoResult is Resource.Error)
//            if (failedUploads) {
//                _error.value = "Some files failed to upload. Please try again."
//                _isLoading.value = false
//                return@launch
//            }
//
//            // Step 3: Extract successful URLs
//            val imageUrls = portfolioImageResults.mapNotNull { (it as? Resource.Success)?.data }
//            val videoUrl = (portfolioVideoResult as? Resource.Success)?.data
//
//            // Step 4: Create the profile in Firestore with all the data
//            val createProfileResult = providerRepository.createProviderProfile(
//                uid = uid,
//                name = _name.value,
//                bandName = _bandName.value,
//                gmail = _gmail.value.takeIf { it.isNotBlank() },
//                role = _role.value,
//                experience = _experience.value.toIntOrNull() ?: 0,
//                teamMembers = _teamMembers.value,
//                portfolioImageUrls = imageUrls,
//                portfolioVideoUrl = videoUrl,
//                youtubeLink = _youtubeLink.value.takeIf { it.isNotBlank() },
//                kycDocUrls = emptyMap() // Placeholder
//            )
//
//            when (createProfileResult) {
//                is Resource.Success -> _eventChannel.send(OnboardingEvent.Navigate(Route.PROVIDER_HOME))
//                is Resource.Error -> _error.value = createProfileResult.message
//                else -> {}
//            }
//            _isLoading.value = false
//        }
//    }
//}

// file: com/dholsagar/app/presentation/onboarding_provider/ProviderOnboardingViewModel.kt
package com.dholsagar.app.presentation.onboarding_provider

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.navigation.Route
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.repository.AuthRepository
import com.dholsagar.app.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeamMember(val name: String, val role: String)

sealed class OnboardingEvent {
    data class Navigate(val route: String) : OnboardingEvent()
}

@HiltViewModel
class ProviderOnboardingViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val pagerSteps = listOf("Personal", "Skills", "Portfolio", "KYC")

    // --- State & Events ---
    private val _eventChannel = Channel<OnboardingEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // --- Page State ---
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    private val _bandName = MutableStateFlow("")
    val bandName = _bandName.asStateFlow()
    private val _gmail = MutableStateFlow("")
    val gmail = _gmail.asStateFlow()
    private val _role = MutableStateFlow("")
    val role = _role.asStateFlow()
    private val _experience = MutableStateFlow("")
    val experience = _experience.asStateFlow()
    private val _teamMembers = MutableStateFlow<List<TeamMember>>(emptyList())
    val teamMembers = _teamMembers.asStateFlow()
    private val _portfolioImageUris = MutableStateFlow<List<Uri>>(emptyList())
    val portfolioImageUris = _portfolioImageUris.asStateFlow()
    private val _portfolioVideoUri = MutableStateFlow<Uri?>(null)
    val portfolioVideoUri = _portfolioVideoUri.asStateFlow()
    private val _youtubeLink = MutableStateFlow("")
    val youtubeLink = _youtubeLink.asStateFlow()
    private val _kycDocumentUris = MutableStateFlow<Map<String, Uri>>(emptyMap())
    val kycDocumentUris = _kycDocumentUris.asStateFlow()

    // --- Dialog State ---
    private val _showAddMemberDialog = MutableStateFlow(false)
    val showAddMemberDialog = _showAddMemberDialog.asStateFlow()
    private val _newMemberName = MutableStateFlow("")
    val newMemberName = _newMemberName.asStateFlow()
    private val _newMemberRole = MutableStateFlow("")
    val newMemberRole = _newMemberRole.asStateFlow()

    // --- Event Handlers ---
    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onBandNameChange(value: String) {
        _bandName.value = value
    }

    fun onGmailChange(value: String) {
        _gmail.value = value
    }

    fun onRoleChange(value: String) {
        _role.value = value
    }

    fun onExperienceChange(value: String) {
        _experience.value = value
    }

    fun onYoutubeLinkChange(value: String) {
        _youtubeLink.value = value
    }

    fun onPortfolioImagesSelected(uris: List<Uri>) {
        _portfolioImageUris.update { it + uris }
    }

    fun onRemovePortfolioImage(uri: Uri) {
        _portfolioImageUris.update { it - uri }
    }

    fun onPortfolioVideoSelected(uri: Uri?) {
        _portfolioVideoUri.value = uri
    }

    fun onKycDocumentSelected(docId: String, uri: Uri) {
        _kycDocumentUris.update { it + (docId to uri) }
    }

    fun onRemoveKycDocument(docId: String) {
        _kycDocumentUris.update { it - docId }
    }

    fun onErrorShown() {
        _error.value = null
    }

    fun onShowAddMemberDialog() {
        _showAddMemberDialog.value = true
    }

    fun onDismissAddMemberDialog() {
        _showAddMemberDialog.value = false
    }

    fun onNewMemberNameChange(value: String) {
        _newMemberName.value = value
    }

    fun onNewMemberRoleChange(value: String) {
        _newMemberRole.value = value
    }

    fun addTeamMember(name: String, role: String) {
        if (name.isNotBlank() && role.isNotBlank()) {
            _teamMembers.update { it + TeamMember(name, role) }
            _newMemberName.value = ""
            _newMemberRole.value = ""
            _showAddMemberDialog.value = false
        }
    }

    fun onSubmit() {
        viewModelScope.launch {
            if (portfolioImageUris.value.size < 5) {
                _error.value = "Please upload at least 5 portfolio photos."
                return@launch
            }
            val uid = authRepository.currentUser?.uid
            if (uid == null) {
                _error.value = "User not found. Please log in again."
                return@launch
            }
            _isLoading.value = true

            // --- Step 1 & 2: Upload files and check for errors (This logic is correct) ---
            val portfolioImageUrlsDeferred = _portfolioImageUris.value.map { uri ->
                async { providerRepository.uploadFile(uri, "portfolio_images/$uid") }
            }
            val portfolioVideoUrlDeferred = _portfolioVideoUri.value?.let { uri ->
                async { providerRepository.uploadFile(uri, "portfolio_videos/$uid") }
            }
            val kycDocUrlsDeferred = _kycDocumentUris.value.map { (docId, uri) ->
                async { docId to providerRepository.uploadFile(uri, "kyc_documents/$uid") }
            }
            val portfolioImageResults = portfolioImageUrlsDeferred.awaitAll()
            val portfolioVideoResult = portfolioVideoUrlDeferred?.await()
            val kycDocResults = kycDocUrlsDeferred.awaitAll()

            val failedUploads = portfolioImageResults.any { it is Resource.Error } ||
                    (portfolioVideoResult != null && portfolioVideoResult is Resource.Error) ||
                    kycDocResults.any { it.second is Resource.Error }
            if (failedUploads) {
                _error.value = "Some files failed to upload. Please try again."
                _isLoading.value = false
                return@launch
            }

            // --- Step 3: Extract URLs (This logic is correct) ---
            val imageUrls = portfolioImageResults.mapNotNull { (it as? Resource.Success)?.data }
            val videoUrl = (portfolioVideoResult as? Resource.Success)?.data
            val kycUrlsFlatMap =
                kycDocResults.associate { (docId, result) -> docId to ((result as Resource.Success).data!!) }

            // --- THIS IS THE FIX ---
            // We need to transform the flat map of KYC URLs into a nested map.
            // FROM: {"Ayush_aadhaar": "url1", "Ramesh_pan": "url2"}
            // TO:   {"Ayush": {"aadhaar": "url1"}, "Ramesh": {"pan": "url2"}}
//            val kycUrlsNestedMap = kycUrlsFlatMap.entries
//                .groupBy(
//                    keySelector = { it.key.substringBefore('_') }, // Group by member name
//                    valueTransform = { it.key.substringAfter('_') to it.value } // Transform value to (docType to url)
//                )
//                .mapValues { it.value.toMap() } // Convert the list of pairs to a map

            // --- Step 4: Create the profile in Firestore ---
            val KycUrlsNestedMap = kycUrlsFlatMap.entries
                .groupBy(
                    keySelector = { it.key.substringBefore('_') }, // Group by member name
                    valueTransform = { it.key.substringAfter('_') to it.value } // Transform value to (docType to url)
                )
                .mapValues { it.value.toMap() } // Convert the list of pairs to a map
            val createProfileResult = providerRepository.createProviderProfile(
                uid = uid,
                name = _name.value,
                bandName = _bandName.value,
                gmail = _gmail.value.takeIf { it.isNotBlank() },
                role = _role.value,
                experience = _experience.value.toIntOrNull() ?: 0,
                teamMembers = _teamMembers.value,
                portfolioImageUrls = imageUrls,
                portfolioVideoUrl = videoUrl,
                youtubeLink = _youtubeLink.value.takeIf { it.isNotBlank() },
                kycDocUrls = KycUrlsNestedMap // Use the correctly transformed map
            )

            when (createProfileResult) {
                is Resource.Success -> _eventChannel.send(OnboardingEvent.Navigate(Route.PROVIDER_HOME))
                is Resource.Error -> _error.value = createProfileResult.message
                else -> {}
            }
            _isLoading.value = false
        }
    }
}