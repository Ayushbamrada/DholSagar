//// file: com/dholsagar/app/data/repository/impl/FakeProviderRepositoryImpl.kt
//package com.dholsagar.app.data.repository.impl
//
//import android.net.Uri
//import com.dholsagar.app.core.util.Resource
//import com.dholsagar.app.domain.model.ServiceProvider
//import com.dholsagar.app.domain.repository.ProviderRepository
//import com.dholsagar.app.presentation.onboarding_provider.TeamMember
//import kotlinx.coroutines.delay
//import javax.inject.Inject
//
//class FakeProviderRepositoryImpl @Inject constructor() : ProviderRepository {
//
//    // THIS IS THE MISSING FUNCTION
//    // For our fake repository, it just successfully returns an empty list.
//    override suspend fun getProviders(): Resource<List<ServiceProvider>> {
//        delay(1000) // Simulate a network delay
//        return Resource.Success(emptyList())
//    }
//
//    override suspend fun uploadFile(uri: Uri, path: String): Resource<String> {
//        delay(500)
//        println("FAKE UPLOAD: Uploaded $uri to $path")
//        return Resource.Success("https://fake-storage.com/$path/fake_image.jpg")
//    }
//
//    override suspend fun createProviderProfile(
//        uid: String,
//        name: String,
//        bandName: String,
//        gmail: String?,
//        role: String,
//        experience: Int,
//        teamMembers: List<TeamMember>,
//        portfolioImageUrls: List<String>,
//        portfolioVideoUrl: String?,
//        youtubeLink: String?,
//        kycDocUrls: Map<String, Map<String, String>>
//    ): Resource<Unit> {
//        delay(1500)
//        println("FAKE PROFILE CREATION: Saving profile for UID: $uid")
//        return Resource.Success(Unit)
//    }
//}

// file: com/dholsagar/app/data/repository/impl/FakeProviderRepositoryImpl.kt
package com.dholsagar.app.data.repository.impl

import android.net.Uri
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.model.ServiceProvider
import com.dholsagar.app.domain.repository.ProviderRepository
import com.dholsagar.app.presentation.onboarding_provider.TeamMember
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeProviderRepositoryImpl @Inject constructor() : ProviderRepository {

    override suspend fun getProviderDetails(providerId: String): Resource<ServiceProvider> {
        delay(500) // Simulate network delay
        val fakeDetail = ServiceProvider(
            uid = providerId,
            bandName = "Himalayan Drummers",
            leadProviderName = "Chandra Singh",
            leadProviderRole = "Lead Dhol Player",

            location = "Dehradun, Uttarakhand",
            experienceYears = 15,
            portfolioImageUrls = listOf(
                "https://picsum.photos/seed/himalayan/400/300",
                "https://picsum.photos/seed/mountains/400/300",
                "https://picsum.photos/seed/uttarakhand/400/300",
                "https://picsum.photos/seed/culture/400/300"
            ),
            teamMembers = listOf(
                TeamMember("Ramesh Kumar", "Damau Player"),
                TeamMember("Suresh Singh", "Masak Been Player")
            )
        )
        return Resource.Success(fakeDetail)
    }

    // THIS IS THE KEY CHANGE: We now return a list of fake providers.
    override suspend fun getProviders(): Resource<List<ServiceProvider>> {
        delay(1000) // Simulate a network delay
        val fakeProviders = listOf(
            ServiceProvider(
                uid = "fake_provider_1",
                bandName = "Himalayan Drummers",
                location = "Dehradun, Uttarakhand",
                portfolioImageUrls = listOf("https://picsum.photos/seed/himalayan/400/300")
            ),
            ServiceProvider(
                uid = "fake_provider_2",
                bandName = "Garhwal Beats",
                location = "Pauri, Uttarakhand",
                portfolioImageUrls = listOf("https://picsum.photos/seed/garhwal/400/300")
            ),
            ServiceProvider(
                uid = "fake_provider_3",
                bandName = "Kumaon Fusion",
                location = "Nainital, Uttarakhand",
                portfolioImageUrls = listOf("https://picsum.photos/seed/kumaon/400/300")
            )
        )
        return Resource.Success(fakeProviders)
    }

    override suspend fun uploadFile(uri: Uri, path: String): Resource<String> {
        delay(500)
        return Resource.Success("https://fake-storage.com/$path/fake_image.jpg")
    }

    override suspend fun createProviderProfile(
        uid: String,
        name: String,
        bandName: String,
        gmail: String?,
        role: String,
        experience: Int,
        teamMembers: List<TeamMember>,
        portfolioImageUrls: List<String>,
        portfolioVideoUrl: String?,
        youtubeLink: String?,
        kycDocUrls: Map<String, Map<String, String>>
    ): Resource<Unit> {
        delay(1500)
        return Resource.Success(Unit)
    }
}