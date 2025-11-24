// file: com/dholsagar/app/domain/model/ServiceProvider.kt
package com.dholsagar.app.domain.model

import com.dholsagar.app.presentation.onboarding_provider.TeamMember

data class ServiceProvider(
    val uid: String = "",
    val bandName: String = "",
    val leadProviderName: String = "", // For clarity
    val leadProviderRole: String = "", // For clarity
    val leadProviderGmail: String? = null, // ADD THIS
    val experienceYears: Int = 0,
    val location: String = "",
    val avgRating: Double = 0.0,
    val portfolioImageUrls: List<String> = emptyList(),
    val portfolioVideoUrl: String? = null, // For the 1-min uploaded video
    val youtubeLink: String? = null, // For the YouTube link
    val teamMembers: List<TeamMember> = emptyList(),
    val kycStatus: String = "PENDING",
    val description: String = "",
    val specialty: String = "",
    val perDayCharge: String = "",
    val chargeDescription: String = ""
)