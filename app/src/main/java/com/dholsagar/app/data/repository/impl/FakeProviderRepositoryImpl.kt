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

    // THIS IS THE MISSING FUNCTION
    // For our fake repository, it just successfully returns an empty list.
    override suspend fun getProviders(): Resource<List<ServiceProvider>> {
        delay(1000) // Simulate a network delay
        return Resource.Success(emptyList())
    }

    override suspend fun uploadFile(uri: Uri, path: String): Resource<String> {
        delay(500)
        println("FAKE UPLOAD: Uploaded $uri to $path")
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
        println("FAKE PROFILE CREATION: Saving profile for UID: $uid")
        return Resource.Success(Unit)
    }
}