//// file: com/dholsagar/app/domain/repository/ProviderRepository.kt
//package com.dholsagar.app.domain.repository
//
//import android.net.Uri
//import com.dholsagar.app.core.util.Resource
//import com.dholsagar.app.presentation.onboarding_provider.TeamMember
//
//interface ProviderRepository {
//    suspend fun uploadFile(uri: Uri, path: String): Resource<String>
//    suspend fun createProviderProfile(
//        uid: String,
//        name: String,
//        bandName: String,
//        experience: Int,
//        teamMembers: List<TeamMember>,
//        portfolioImageUrls: List<String>,
//        portfolioVideoUrl: String?,
//        kycDocUrls: Map<String, Map<String, String>>
//    ): Resource<Unit>
//}

// file: com/dholsagar/app/domain/repository/ProviderRepository.kt
package com.dholsagar.app.domain.repository

import android.net.Uri
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.model.Booking
import com.dholsagar.app.domain.model.ServiceProvider
import com.dholsagar.app.presentation.onboarding_provider.TeamMember

interface ProviderRepository {

    suspend fun getProviders(): Resource<List<ServiceProvider>>
    suspend fun getProviderDetails(providerId: String): Resource<ServiceProvider> // ADD THIS
//    suspend fun uploadFile(uri: Uri, path: String): Resource<String>
    suspend fun uploadFile(uri: Uri, path: String, mimeType: String?): Resource<String>
    suspend fun createProviderProfile(
        uid: String,
        name: String,
        bandName: String,
        gmail: String?, // ADD THIS
        role: String,
        phone: String,// ADD THIS
        experience: Int,
        teamMembers: List<TeamMember>,
        portfolioImageUrls: List<String>,
        portfolioVideoUrl: String?, // ADD THIS
        youtubeLink: String?,       // ADD THIS
        // THIS IS THE FIX: Update the type to a nested map
        kycDocUrls: Map<String, Map<String, String>>
    ): Resource<Unit>
    // --- ADD THIS NEW FUNCTION ---
    suspend fun updateProviderDetails(
        uid: String,
        description: String,
        specialty: String,
        perDayCharge: String,
        chargeDescription: String
    ): Resource<Unit>
    suspend fun getProviderBookings(): Resource<List<Booking>>
}