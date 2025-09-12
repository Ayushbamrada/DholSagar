// file: com/dholsagar/app/data/remote/dto/ServiceProviderDto.kt
package com.dholsagar.app.data.remote.dto

data class ServiceProviderDto(
    val providerUid: String? = null,
    val bandName: String? = null,
    val leadProviderName: String? = null,
    val leadProviderRole: String? = null,
    val leadProviderGmail: String? = null, // ADD THIS
    val description: String? = null,
    val experienceYears: Int? = null,
    val pricePerDay: Int? = null,
    val location: String? = null,
    val avgRating: Double? = null,
    val portfolioImages: List<String>? = null,
    val portfolioVideoUrl: String? = null, // ADD THIS
    val youtubeLink: String? = null, // ADD THIS
    val kycStatus: String? = null,
    val teamMembers: List<Map<String, String>>? = null,
    val kycDocuments: Map<String, Map<String, String>>? = null
)