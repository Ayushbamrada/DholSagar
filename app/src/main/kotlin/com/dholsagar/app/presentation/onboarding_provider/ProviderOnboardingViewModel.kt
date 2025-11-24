//
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
//import kotlinx.coroutines.flow.*
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
//    private val _phone = MutableStateFlow("")
//    val phone = _phone.asStateFlow()
//
//    val isPhoneEditable = authRepository.currentUser?.phoneNumber.isNullOrEmpty()
//    val isEmailEditable = authRepository.currentUser?.email.isNullOrEmpty()
//
//    // --- Page State ---
//    private val _name = MutableStateFlow("")
//    val name = _name.asStateFlow()
//    private val _bandName = MutableStateFlow("")
//    val bandName = _bandName.asStateFlow()
//    private val _gmail = MutableStateFlow("")
//    val gmail = _gmail.asStateFlow()
//    private val _role = MutableStateFlow("")
//    val role = _role.asStateFlow()
//    private val _experience = MutableStateFlow("")
//    val experience = _experience.asStateFlow()
//    private val _teamMembers = MutableStateFlow<List<TeamMember>>(emptyList())
//    val teamMembers = _teamMembers.asStateFlow()
//    private val _portfolioImageUris = MutableStateFlow<List<Uri>>(emptyList())
//    val portfolioImageUris = _portfolioImageUris.asStateFlow()
//    private val _portfolioVideoUri = MutableStateFlow<Uri?>(null)
//    val portfolioVideoUri = _portfolioVideoUri.asStateFlow()
//    private val _youtubeLink = MutableStateFlow("")
//    val youtubeLink = _youtubeLink.asStateFlow()
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
//
//    init {
//        authRepository.currentUser?.let { user ->
//            _name.value = user.displayName ?: ""
//            _gmail.value = user.email ?: ""
//            _phone.value = user.phoneNumber?.removePrefix("+91") ?: ""
//        }
//    }
//
//    fun onPhoneChange(value: String) {
//        if (value.all { it.isDigit() } && value.length <= 10) {
//            _phone.value = value
//        }
//    }
//
//    // --- Event Handlers ---
//    fun onNameChange(value: String) {
//        _name.value = value
//    }
//
//    fun onBandNameChange(value: String) {
//        _bandName.value = value
//    }
//
//    fun onGmailChange(value: String) {
//        _gmail.value = value
//    }
//
//    fun onRoleChange(value: String) {
//        _role.value = value
//    }
//
//    fun onExperienceChange(value: String) {
//        _experience.value = value
//    }
//
//    fun onYoutubeLinkChange(value: String) {
//        _youtubeLink.value = value
//    }
//
//    fun onPortfolioImagesSelected(uris: List<Uri>) {
//        _portfolioImageUris.update { it + uris }
//    }
//
//    fun onRemovePortfolioImage(uri: Uri) {
//        _portfolioImageUris.update { it - uri }
//    }
//
//    fun onPortfolioVideoSelected(uri: Uri?) {
//        _portfolioVideoUri.value = uri
//    }
//
//    fun onKycDocumentSelected(docId: String, uri: Uri) {
//        _kycDocumentUris.update { it + (docId to uri) }
//    }
//
//    fun onRemoveKycDocument(docId: String) {
//        _kycDocumentUris.update { it - docId }
//    }
//
//
//
//    fun onErrorShown() {
//        _error.value = null
//    }
//
//    fun onShowAddMemberDialog() {
//        _showAddMemberDialog.value = true
//    }
//
//    fun onDismissAddMemberDialog() {
//        _showAddMemberDialog.value = false
//    }
//
//    fun onNewMemberNameChange(value: String) {
//        _newMemberName.value = value
//    }
//
//    fun onNewMemberRoleChange(value: String) {
//        _newMemberRole.value = value
//    }
//
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
//            if (portfolioImageUris.value.size < 5) {
//                _error.value = "Please upload at least 5 portfolio photos."
//                return@launch
//            }
//            val uid = authRepository.currentUser?.uid
//            if (uid == null) {
//                _error.value = "User not found. Please log in again."
//                return@launch
//            }
//            _isLoading.value = true
//
//            // --- Step 1 & 2: Upload files and check for errors (This logic is correct) ---
//            val portfolioImageUrlsDeferred = _portfolioImageUris.value.map { uri ->
//                async { providerRepository.uploadFile(uri, "portfolio_images/$uid") }
//            }
//            val portfolioVideoUrlDeferred = _portfolioVideoUri.value?.let { uri ->
//                async { providerRepository.uploadFile(uri, "portfolio_videos/$uid") }
//            }
//            val kycDocUrlsDeferred = _kycDocumentUris.value.map { (docId, uri) ->
//                async { docId to providerRepository.uploadFile(uri, "kyc_documents/$uid") }
//            }
//            val portfolioImageResults = portfolioImageUrlsDeferred.awaitAll()
//            val portfolioVideoResult = portfolioVideoUrlDeferred?.await()
//            val kycDocResults = kycDocUrlsDeferred.awaitAll()
//
//            val failedUploads = portfolioImageResults.any { it is Resource.Error } ||
//                    (portfolioVideoResult != null && portfolioVideoResult is Resource.Error) ||
//                    kycDocResults.any { it.second is Resource.Error }
//            if (failedUploads) {
//                _error.value = "Some files failed to upload. Please try again."
//                _isLoading.value = false
//                return@launch
//            }
//
//            // --- Step 3: Extract URLs (This logic is correct) ---
//            val imageUrls = portfolioImageResults.mapNotNull { (it as? Resource.Success)?.data }
//            val videoUrl = (portfolioVideoResult as? Resource.Success)?.data
//            val kycUrlsFlatMap =
//                kycDocResults.associate { (docId, result) -> docId to ((result as Resource.Success).data!!) }
//
//            // --- THIS IS THE FIX ---
//            // We need to transform the flat map of KYC URLs into a nested map.
//            // FROM: {"Ayush_aadhaar": "url1", "Ramesh_pan": "url2"}
//            // TO:   {"Ayush": {"aadhaar": "url1"}, "Ramesh": {"pan": "url2"}}
////            val kycUrlsNestedMap = kycUrlsFlatMap.entries
////                .groupBy(
////                    keySelector = { it.key.substringBefore('_') }, // Group by member name
////                    valueTransform = { it.key.substringAfter('_') to it.value } // Transform value to (docType to url)
////                )
////                .mapValues { it.value.toMap() } // Convert the list of pairs to a map
//
//            // --- Step 4: Create the profile in Firestore ---
//            val KycUrlsNestedMap = kycUrlsFlatMap.entries
//                .groupBy(
//                    keySelector = { it.key.substringBefore('_') }, // Group by member name
//                    valueTransform = { it.key.substringAfter('_') to it.value } // Transform value to (docType to url)
//                )
//                .mapValues { it.value.toMap() } // Convert the list of pairs to a map
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
//                kycDocUrls = KycUrlsNestedMap // Use the correctly transformed map
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
import android.content.ContentResolver // ADD THIS IMPORT
import com.dholsagar.app.core.util.FileUtils // ADD THIS IMPORT
import dagger.hilt.android.qualifiers.ApplicationContext // ADD THIS IMPORT
import android.content.Context // ADD THIS IMPORT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeamMember(val name: String, val role: String)

// THIS IS THE FIX: Add the missing event
sealed class OnboardingEvent {
    data class Navigate(val route: String) : OnboardingEvent()
    data class NavigateAndPopUp(val route: String, val popUpTo: String) : OnboardingEvent()
}

@HiltViewModel
class ProviderOnboardingViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val pagerSteps = listOf("Personal", "Skills", "Portfolio", "KYC")

    // --- State & Events ---
    private val _eventChannel = Channel<OnboardingEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    val isPhoneEditable = authRepository.currentUser?.phoneNumber.isNullOrEmpty()
    val isEmailEditable = authRepository.currentUser?.email.isNullOrEmpty()

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


    init {
        authRepository.currentUser?.let { user ->
            _name.value = user.displayName ?: ""
            _gmail.value = user.email ?: ""
            _phone.value = user.phoneNumber?.removePrefix("+91") ?: ""
        }
    }

    fun onPhoneChange(value: String) {
        if (value.all { it.isDigit() } && value.length <= 10) {
            _phone.value = value
        }
    }

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

    //    fun onSubmit() {
//        viewModelScope.launch {
//            if (portfolioImageUris.value.size < 5) {
//                _error.value = "Please upload at least 5 portfolio photos."
//                return@launch
//            }
//            val uid = authRepository.currentUser?.uid
//            if (uid == null) {
//                _error.value = "User not found. Please log in again."
//                return@launch
//            }
//
//            _isLoading.value = true
//            // THIS IS THE FIX: Add the 'try' block before 'finally'
//            try {
//                val uid = authRepository.currentUser?.uid!!
//                val contentResolver = context.contentResolver
//
//                // ... (All the logic for uploading files and creating the profile is correct)
//                val portfolioImageUrlsDeferred = _portfolioImageUris.value.map { uri ->
//                    val mimeType = FileUtils.getMimeType(contentResolver, uri)
//                    async { providerRepository.uploadFile(uri, "portfolio_images/$uid", mimeType) }
//                }
//                val portfolioVideoUrlDeferred = _portfolioVideoUri.value?.let { uri ->
//                    val mimeType = FileUtils.getMimeType(contentResolver, uri)
//                    async { providerRepository.uploadFile(uri, "portfolio_videos/$uid", mimeType) }
//                }
//                val kycDocUrlsDeferred = _kycDocumentUris.value.map { (docId, uri) ->
//                    val mimeType = FileUtils.getMimeType(contentResolver, uri)
//                    async { docId to providerRepository.uploadFile(uri, "kyc_documents/$uid", mimeType) }
//                }
//                val portfolioImageResults = portfolioImageUrlsDeferred.awaitAll()
//                val portfolioVideoResult = portfolioVideoUrlDeferred?.await()
//                val kycDocResults = kycDocUrlsDeferred.awaitAll()
//
//                val failedUploads = portfolioImageResults.any { it is Resource.Error } ||
//                        (portfolioVideoResult != null && portfolioVideoResult is Resource.Error) ||
//                        kycDocResults.any { it.second is Resource.Error }
//                if (failedUploads) {
//                    _error.value = "Some files failed to upload. Please try again."
//                    // No need to set isLoading false here, finally block will do it
//                    return@launch
//                }
//
//
//                val imageUrls = portfolioImageResults.mapNotNull { (it as? Resource.Success)?.data }
//                val videoUrl = (portfolioVideoResult as? Resource.Success)?.data
//                val kycUrlsFlatMap = kycDocResults.associate { (docId, result) -> docId to ((result as Resource.Success).data!!) }
//                val kycUrlsNestedMap = kycUrlsFlatMap.entries.groupBy(
//                    keySelector = { it.key.substringBefore('_') },
//                    valueTransform = { it.key.substringAfter('_') to it.value }
//                ).mapValues { it.value.toMap() }
//
//                val createProfileResult = providerRepository.createProviderProfile(
//                    uid = uid, name = _name.value, bandName = _bandName.value,
//                    gmail = _gmail.value.takeIf { it.isNotBlank() }, role = _role.value,
//                    experience = _experience.value.toIntOrNull() ?: 0, teamMembers = _teamMembers.value,
//                    portfolioImageUrls = imageUrls, portfolioVideoUrl = videoUrl,
//                    youtubeLink = _youtubeLink.value.takeIf { it.isNotBlank() }, kycDocUrls = kycUrlsNestedMap
//                )
//
//                when (createProfileResult) {
//                    is Resource.Success -> {
//                        _eventChannel.send(OnboardingEvent.NavigateAndPopUp(Route.PROVIDER_HOME, Route.AUTH_GRAPH))
//                    }
//                    is Resource.Error -> {
//                        _error.value = createProfileResult.message
//                    }
//                    else -> {}
//                }
//            } finally {
//                _isLoading.value = false // This will ALWAYS run
//            }
//        }
//    }
//}
    fun onSubmit() {
        viewModelScope.launch {
            if (_name.value.isBlank() || _bandName.value.isBlank() || _role.value.isBlank()) {
                _error.value = "Please fill all fields on the 'Personal' page."
                return@launch
            }
            if (_portfolioImageUris.value.size < 5) {
                _error.value = "Please upload at least 5 portfolio photos."
                return@launch
            }
            val uid = authRepository.currentUser?.uid
            if (uid == null) {
                _error.value = "User not found. Please log in again."
                return@launch
            }

            _isLoading.value = true
            try {
                val contentResolver = context.contentResolver

                // --- File Upload Logic (remains the same) ---
                val portfolioImageUrlsDeferred = _portfolioImageUris.value.map { uri ->
                    val mimeType = FileUtils.getMimeType(contentResolver, uri)
                    async { providerRepository.uploadFile(uri, "portfolio_images/$uid", mimeType) }
                }
                val portfolioVideoUrlDeferred = _portfolioVideoUri.value?.let { uri ->
                    val mimeType = FileUtils.getMimeType(contentResolver, uri)
                    async { providerRepository.uploadFile(uri, "portfolio_videos/$uid", mimeType) }
                }
                val kycDocUrlsDeferred = _kycDocumentUris.value.map { (docId, uri) ->
                    val mimeType = FileUtils.getMimeType(contentResolver, uri)
                    async {
                        docId to providerRepository.uploadFile(
                            uri,
                            "kyc_documents/$uid",
                            mimeType
                        )
                    }
                }
                val portfolioImageResults = portfolioImageUrlsDeferred.awaitAll()
                val portfolioVideoResult = portfolioVideoUrlDeferred?.await()
                val kycDocResults = kycDocUrlsDeferred.awaitAll()

                // --- THIS IS THE FIX: A more robust way to check for failures and extract data ---

                // 1. Check for failures
                val hasImageError = portfolioImageResults.any { it is Resource.Error }
                val hasVideoError =
                    portfolioVideoResult != null && portfolioVideoResult is Resource.Error
                val hasKycError = kycDocResults.any { it.second is Resource.Error }

                if (hasImageError || hasVideoError || hasKycError) {
                    _error.value =
                        "Some files failed to upload. Please check your connection and try again."
                    return@launch
                }

                // 2. Safely extract the successful URLs
                val imageUrls = portfolioImageResults.mapNotNull { (it as? Resource.Success)?.data }
                val videoUrl = (portfolioVideoResult as? Resource.Success)?.data
                val kycUrlsFlatMap = kycDocResults.mapNotNull { (docId, result) ->
                    (result as? Resource.Success)?.data?.let { url -> docId to url }
                }.associate { it }

                // 3. Transform KYC data (this logic is correct)
                val kycUrlsNestedMap = kycUrlsFlatMap.entries.groupBy(
                    keySelector = { it.key.substringBefore('_') },
                    valueTransform = { it.key.substringAfter('_') to it.value }
                ).mapValues { it.value.toMap() }

                // 4. Create the profile in Firestore
                val createProfileResult = providerRepository.createProviderProfile(
                    uid = uid,
                    name = _name.value,
                    bandName = _bandName.value,
                    gmail = _gmail.value.takeIf { it.isNotBlank() },
                    role = _role.value,
                    phone = _phone.value, // <-- THIS IS THE FIX
                    experience = _experience.value.toIntOrNull() ?: 0,
                    teamMembers = _teamMembers.value,
                    portfolioImageUrls = imageUrls,
                    portfolioVideoUrl = videoUrl,
                    youtubeLink = _youtubeLink.value.takeIf { it.isNotBlank() },
                    kycDocUrls = kycUrlsNestedMap
                )

                when (createProfileResult) {
                    is Resource.Success -> {
                        _eventChannel.send(
                            OnboardingEvent.NavigateAndPopUp(
                                Route.PROVIDER_HOME,
                                Route.AUTH_GRAPH
                            )
                        )
                    }

                    is Resource.Error -> {
                        _error.value = createProfileResult.message
                    }

                    else -> {}
                }
            } finally {
                _isLoading.value = false // This will ALWAYS run
            }
        }
    }
}