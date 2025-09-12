// file: com/dholsagar/app/data/repository/impl/ProviderRepositoryImpl.kt
package com.dholsagar.app.data.repository.impl

import android.net.Uri
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.data.remote.dto.ServiceProviderDto
import com.dholsagar.app.domain.model.ServiceProvider
import com.dholsagar.app.domain.repository.ProviderRepository
import com.dholsagar.app.presentation.onboarding_provider.TeamMember
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class ProviderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ProviderRepository {

    override suspend fun getProviders(): Resource<List<ServiceProvider>> {
        return try {
            val snapshot = firestore.collection("serviceProviders")
                .whereEqualTo("kycStatus", "APPROVED")
                .get()
                .await()

            val providers = snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(ServiceProviderDto::class.java)
                dto?.let {
                    ServiceProvider(
                        uid = it.providerUid ?: "",
                        bandName = it.bandName ?: "Unknown Band",
                        leadProviderName = it.leadProviderName ?: "",
                        leadProviderRole = it.leadProviderRole ?: "",
                        location = it.location ?: "Unknown Location",
                        portfolioImageUrls = it.portfolioImages ?: emptyList()
                    )
                }
            }
            Resource.Success(providers)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Failed to fetch providers.")
        }
    }


    override suspend fun uploadFile(uri: Uri, path: String): Resource<String> {
        return try {
            val fileName = UUID.randomUUID().toString()
            val downloadUrl = storage.reference.child("$path/$fileName")
                .putFile(uri).await()
                .storage.downloadUrl.await()
            Resource.Success(downloadUrl.toString())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "File upload failed.")
        }
    }

//    override suspend fun createProviderProfile(
//        uid: String,
//        name: String,
//        bandName: String,
//        experience: Int,
//        teamMembers: List<TeamMember>,
//        portfolioImageUrls: List<String>,
//        portfolioVideoUrl: String?,
//        kycDocUrls: Map<String, Map<String, String>>
//    ): Resource<Unit> {
//        return try {
//            val providerProfile = hashMapOf(
//                "providerUid" to uid,
//                "bandName" to bandName,
//                "description" to "", // Add a default or get from ViewModel
//                "experienceYears" to experience,
//                "pricePerDay" to 0, // Add a default or get from ViewModel
//                "location" to "", // Add a default or get from ViewModel
//                "avgRating" to 0.0,
//                "portfolioImages" to portfolioImageUrls,
//                "portfolioVideoUrl" to portfolioVideoUrl,
//                "kycStatus" to "PENDING",
//                "teamMembers" to teamMembers.map { mapOf("name" to it.name, "role" to it.role) }
//            )
//            firestore.collection("serviceProviders").document(uid).set(providerProfile).await()
//
//            // Also, update the main user profile
//            val userProfileUpdates = mapOf(
//                "name" to name
//            )
//            firestore.collection("users").document(uid).update(userProfileUpdates).await()
//
//            Resource.Success(Unit)
//        } catch (e: Exception) {
//            if (e is CancellationException) throw e
//            Resource.Error(e.message ?: "Failed to create provider profile.")
//        }
//    }
//}
override suspend fun createProviderProfile(
    uid: String,
    name: String,
    bandName: String,
    gmail: String?, // ADD THIS
    role: String,   // ADD THIS
    experience: Int,
    teamMembers: List<TeamMember>,
    portfolioImageUrls: List<String>,
    portfolioVideoUrl: String?, // ADD THIS
    youtubeLink: String?,       // ADD THIS
    kycDocUrls: Map<String, Map<String, String>>
): Resource<Unit> {
    return try {
        val providerProfileDto = ServiceProviderDto(
            providerUid = uid,
            bandName = bandName,
            leadProviderName = name,
            leadProviderRole = role,
            leadProviderGmail = gmail,
            experienceYears = experience,
            portfolioImages = portfolioImageUrls,
            portfolioVideoUrl = portfolioVideoUrl,
            youtubeLink = youtubeLink,
            kycStatus = "PENDING",
            teamMembers = teamMembers.map { mapOf("name" to it.name, "role" to it.role) },
            kycDocuments = kycDocUrls
            // Other fields can be set to default values
        )
        firestore.collection("serviceProviders").document(uid).set(providerProfileDto).await()

        val userProfileUpdates = mapOf(
            "name" to name,
            "email" to gmail
        ).filterValues { it != null }
        firestore.collection("users").document(uid).update(userProfileUpdates).await()

        Resource.Success(Unit)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Resource.Error(e.message ?: "Failed to create provider profile.")
    }
}
}