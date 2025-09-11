//// file: com/dholsagar/app/presentation/onboarding_provider/ProviderOnboardingViewModel.kt
//package com.dholsagar.app.presentation.onboarding_provider
//
//import androidx.lifecycle.ViewModel
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import javax.inject.Inject
//
//data class TeamMember(val name: String, val role: String)
//
//@HiltViewModel
//class ProviderOnboardingViewModel @Inject constructor() : ViewModel() {
//
//    val pagerSteps = listOf("Personal", "Skills", "Portfolio", "KYC")
//
//    // State for Page 1: Personal Details
//    private val _name = MutableStateFlow("")
//    val name = _name.asStateFlow()
//
//    private val _bandName = MutableStateFlow("")
//    val bandName = _bandName.asStateFlow()
//
//    // State for Page 2: Skills
//    private val _experience = MutableStateFlow("")
//    val experience = _experience.asStateFlow()
//
//    private val _teamMembers = MutableStateFlow<List<TeamMember>>(emptyList())
//    val teamMembers = _teamMembers.asStateFlow()
//
//    // Event Handlers
//    fun onNameChange(value: String) { _name.value = value }
//    fun onBandNameChange(value: String) { _bandName.value = value }
//    fun onExperienceChange(value: String) { _experience.value = value }
//
//    fun addTeamMember() {
//        // In a real app, this would show a dialog to enter name/role
//        _teamMembers.update { it + TeamMember("New Member", "Dhol Player") }
//    }
//    // State for the Add Member Dialog
//    private val _showAddMemberDialog = MutableStateFlow(false)
//    val showAddMemberDialog = _showAddMemberDialog.asStateFlow()
//
//    private val _newMemberName = MutableStateFlow("")
//    val newMemberName = _newMemberName.asStateFlow()
//
//    private val _newMemberRole = MutableStateFlow("")
//    val newMemberRole = _newMemberRole.asStateFlow()
//
//    // Event Handlers for Dialog
//    fun onNewMemberNameChange(value: String) { _newMemberName.value = value }
//    fun onNewMemberRoleChange(value: String) { _newMemberRole.value = value }
//
//    fun onShowAddMemberDialog() { _showAddMemberDialog.value = true }
//    fun onDismissAddMemberDialog() { _showAddMemberDialog.value = false }
//
//    // Updated function
//    fun addTeamMember(name: String, role: String) {
//        if (name.isNotBlank() && role.isNotBlank()) {
//            _teamMembers.update { it + TeamMember(name, role) }
//            // Reset fields and dismiss dialog
//            _newMemberName.value = ""
//            _newMemberRole.value = ""
//            _showAddMemberDialog.value = false
//        }
//    }
//}


// file: com/dholsagar/app/presentation/onboarding_provider/ProviderOnboardingViewModel.kt
package com.dholsagar.app.presentation.onboarding_provider

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TeamMember(val name: String, val role: String)

@HiltViewModel
class ProviderOnboardingViewModel @Inject constructor() : ViewModel() {

    val pagerSteps = listOf("Personal", "Skills", "Portfolio", "KYC")

    // State for Page 1: Personal Details
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _bandName = MutableStateFlow("")
    val bandName = _bandName.asStateFlow()

    private val _gmail = MutableStateFlow("") // Added state for Gmail
    val gmail = _gmail.asStateFlow()

    // State for Page 2: Skills
    private val _experience = MutableStateFlow("")
    val experience = _experience.asStateFlow()

    private val _teamMembers = MutableStateFlow<List<TeamMember>>(emptyList())
    val teamMembers = _teamMembers.asStateFlow()

    // State for the Add Member Dialog
    private val _showAddMemberDialog = MutableStateFlow(false)
    val showAddMemberDialog = _showAddMemberDialog.asStateFlow()

    private val _newMemberName = MutableStateFlow("")
    val newMemberName = _newMemberName.asStateFlow()

    private val _newMemberRole = MutableStateFlow("")
    val newMemberRole = _newMemberRole.asStateFlow()

    // --- Event Handlers ---

    fun onNameChange(value: String) { _name.value = value }
    fun onBandNameChange(value: String) { _bandName.value = value }
    fun onGmailChange(value: String) { _gmail.value = value } // Added handler for Gmail
    fun onExperienceChange(value: String) { _experience.value = value }

    // Dialog Event Handlers
    fun onNewMemberNameChange(value: String) { _newMemberName.value = value }
    fun onNewMemberRoleChange(value: String) { _newMemberRole.value = value }
    fun onShowAddMemberDialog() { _showAddMemberDialog.value = true }
    fun onDismissAddMemberDialog() { _showAddMemberDialog.value = false }

    fun addTeamMember(name: String, role: String) {
        if (name.isNotBlank() && role.isNotBlank()) {
            _teamMembers.update { it + TeamMember(name, role) }
            // Reset fields and dismiss dialog
            _newMemberName.value = ""
            _newMemberRole.value = ""
            _showAddMemberDialog.value = false
        }
    }
}