//// file: com/dholsagar/app/data/repository/impl/ProviderRepositoryImpl.kt
//package com.dholsagar.app.data.repository.impl
//
//import android.net.Uri
//import com.dholsagar.app.core.util.Resource
//import com.dholsagar.app.data.remote.dto.ServiceProviderDto
//import com.dholsagar.app.domain.model.ServiceProvider
//import com.dholsagar.app.domain.repository.ProviderRepository
//import com.dholsagar.app.presentation.onboarding_provider.TeamMember
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.storage.StorageMetadata
//import com.google.firebase.storage.FirebaseStorage
//import kotlinx.coroutines.tasks.await
//import java.util.UUID
//import javax.inject.Inject
//import kotlin.coroutines.cancellation.CancellationException
//// ... other imports
//import com.dholsagar.app.data.remote.dto.BookingDto
//import com.dholsagar.app.domain.model.Booking
//import com.google.firebase.Timestamp
//import com.google.firebase.firestore.Query
//import java.text.SimpleDateFormat
//import java.util.Date     // <-- FIX
//import java.util.Locale   // <-- FIX
////import java.util.UUID
////import java.util.*
//import java.util.concurrent.TimeUnit
//
//class ProviderRepositoryImpl @Inject constructor(
//    private val firestore: FirebaseFirestore,
//    private val storage: FirebaseStorage,
//    private val auth: FirebaseAuth
//) : ProviderRepository {
//
//
//    override suspend fun getProviders(): Resource<List<ServiceProvider>> {
//        return try {
//            val snapshot = firestore.collection("serviceProviders")
//                .whereEqualTo("kycStatus", "APPROVED")
//                .get()
//                .await()
//
//            val providers = snapshot.documents.mapNotNull { doc ->
//                val dto = doc.toObject(ServiceProviderDto::class.java)
//                dto?.let {
//                    ServiceProvider(
//                        uid = it.providerUid ?: "",
//                        bandName = it.bandName ?: "Unknown Band",
//                        leadProviderName = it.leadProviderName ?: "",
//                        leadProviderRole = it.leadProviderRole ?: "",
//                        location = it.location ?: "Unknown Location",
//                        portfolioImageUrls = it.portfolioImages ?: emptyList(),
//                        // --- MAP NEW FIELDS (even if just default) ---
//                        description = it.description ?: "",
//                        specialty = it.specialty ?: "",
//                        perDayCharge = it.perDayCharge ?: "",
//                        chargeDescription = it.chargeDescription ?: ""
//                    )
//                }
//            }
//            Resource.Success(providers)
//        } catch (e: Exception) {
//            if (e is CancellationException) throw e
//            Resource.Error(e.message ?: "Failed to fetch providers.")
//        }
//    }
//
//    // THIS IS THE MISSING FUNCTION IMPLEMENTATION
//    override suspend fun getProviderDetails(providerId: String): Resource<ServiceProvider> {
//        return try {
//            val document =
//                firestore.collection("serviceProviders").document(providerId).get().await()
//            val dto = document.toObject(ServiceProviderDto::class.java)
//
//            if (dto != null) {
//                val provider = ServiceProvider(
//                    uid = dto.providerUid ?: "",
//                    bandName = dto.bandName ?: "Unknown Band",
//                    leadProviderName = dto.leadProviderName ?: "",
//                    leadProviderRole = dto.leadProviderRole ?: "",
//                    location = dto.location ?: "Unknown Location",
//                    experienceYears = dto.experienceYears ?: 0,
//                    portfolioImageUrls = dto.portfolioImages ?: emptyList(),
//                    portfolioVideoUrl = dto.portfolioVideoUrl,
//                    youtubeLink = dto.youtubeLink,
//                    teamMembers = dto.teamMembers?.map {
//                        TeamMember(it["name"] ?: "", it["role"] ?: "")
//                    } ?: emptyList(),
//                    kycStatus = dto.kycStatus ?: "PENDING",
//                    // --- MAP NEW FIELDS ---
//                    description = dto.description ?: "",
//                    specialty = dto.specialty ?: "",
//                    perDayCharge = dto.perDayCharge ?: "",
//                    chargeDescription = dto.chargeDescription ?: ""
//                )
//                Resource.Success(provider)
//            } else {
//                Resource.Error("Provider details not found.")
//            }
//        } catch (e: Exception) {
//            if (e is CancellationException) throw e
//            Resource.Error(e.message ?: "Failed to fetch provider details.")
//        }
//    }
//
//
////    override suspend fun uploadFile(uri: Uri, path: String, mimeType: String?): Resource<String> {
////        return try {
////            val fileName = UUID.randomUUID().toString()
////            val storageRef = storage.reference.child("$path/$fileName")
////
////            // Create metadata for the file, including the content type (MIME type)
////            val metadata = StorageMetadata.Builder()
////                .setContentType(mimeType)
////                .build()
////
////            // Pass the metadata with the upload request
////            val downloadUrl = storageRef.putFile(uri, metadata).await()
////                .storage.downloadUrl.await()
////
////            Resource.Success(downloadUrl.toString())
////        } catch (e: Exception) {
////            if (e is CancellationException) throw e
////            Resource.Error(e.message ?: "File upload failed.")
////        }
////    }
//
//
//    // THIS IS THE FIX: We separate the upload from the URL retrieval.
//    override suspend fun uploadFile(uri: Uri, path: String, mimeType: String?): Resource<String> {
//        return try {
//            val fileName = UUID.randomUUID().toString()
//            val storageRef = storage.reference.child("$path/$fileName")
//
//            val metadata = StorageMetadata.Builder()
//                .setContentType(mimeType)
//                .build()
//
//            // Step 1: Upload the file and wait for it to complete.
//            val uploadTask = storageRef.putFile(uri, metadata).await()
//
//            // Step 2: If the upload was successful, THEN get the download URL.
//            val downloadUrl = uploadTask.storage.downloadUrl.await()
//
//            Resource.Success(downloadUrl.toString())
//        } catch (e: Exception) {
//            if (e is CancellationException) throw e
//            // Provide a more specific error message from the exception
//            Resource.Error(e.localizedMessage ?: "File upload failed.")
//        }
//    }
//
//    //    override suspend fun createProviderProfile(
////        uid: String,
////        name: String,
////        bandName: String,
////        experience: Int,
////        teamMembers: List<TeamMember>,
////        portfolioImageUrls: List<String>,
////        portfolioVideoUrl: String?,
////        kycDocUrls: Map<String, Map<String, String>>
////    ): Resource<Unit> {
////        return try {
////            val providerProfile = hashMapOf(
////                "providerUid" to uid,
////                "bandName" to bandName,
////                "description" to "", // Add a default or get from ViewModel
////                "experienceYears" to experience,
////                "pricePerDay" to 0, // Add a default or get from ViewModel
////                "location" to "", // Add a default or get from ViewModel
////                "avgRating" to 0.0,
////                "portfolioImages" to portfolioImageUrls,
////                "portfolioVideoUrl" to portfolioVideoUrl,
////                "kycStatus" to "PENDING",
////                "teamMembers" to teamMembers.map { mapOf("name" to it.name, "role" to it.role) }
////            )
////            firestore.collection("serviceProviders").document(uid).set(providerProfile).await()
////
////            // Also, update the main user profile
////            val userProfileUpdates = mapOf(
////                "name" to name
////            )
////            firestore.collection("users").document(uid).update(userProfileUpdates).await()
////
////            Resource.Success(Unit)
////        } catch (e: Exception) {
////            if (e is CancellationException) throw e
////            Resource.Error(e.message ?: "Failed to create provider profile.")
////        }
////    }
////}
//    override suspend fun createProviderProfile(
//        uid: String,
//        name: String,
//        bandName: String,
//        gmail: String?, // ADD THIS
//        role: String,   // ADD THIS
//        phone: String,
//        experience: Int,
//        teamMembers: List<TeamMember>,
//        portfolioImageUrls: List<String>,
//        portfolioVideoUrl: String?, // ADD THIS
//        youtubeLink: String?,       // ADD THIS
//        kycDocUrls: Map<String, Map<String, String>>
//    ): Resource<Unit> {
//        return try {
//            val providerProfileDto = ServiceProviderDto(
//                providerUid = uid,
//                bandName = bandName,
//                leadProviderName = name,
//                leadProviderRole = role,
//                leadProviderGmail = gmail,
//                experienceYears = experience,
//                portfolioImages = portfolioImageUrls,
//                portfolioVideoUrl = portfolioVideoUrl,
//                youtubeLink = youtubeLink,
//                kycStatus = "PENDING",
//                teamMembers = teamMembers.map { mapOf("name" to it.name, "role" to it.role) },
//                kycDocuments = kycDocUrls
//                // Other fields can be set to default values
//            )
//            firestore.collection("serviceProviders").document(uid).set(providerProfileDto).await()
//
////        val userProfileUpdates = mapOf(
////            "name" to name,
////            "email" to gmail
////        ).filterValues { it != null }
////        firestore.collection("users").document(uid).update(userProfileUpdates).await()
//            val authUser = auth.currentUser!!
//            val userProfileUpdates = mapOf(
//                "name" to name,
//                "email" to (gmail ?: authUser.email), // Use form gmail, fallback to auth email
//                "phone" to authUser.phoneNumber // This will be pre-filled from auth
//            ).filterValues { it != null && it.isNotBlank() } // Don't save null or blank values
//
//            firestore.collection("users").document(uid).update(userProfileUpdates).await()
//
//            Resource.Success(Unit)
//        } catch (e: Exception) {
//            if (e is CancellationException) throw e
//            Resource.Error(e.message ?: "Failed to create provider profile.")
//        }
//    }
//
//    // --- ADD THIS NEW FUNCTION ---
//    override suspend fun updateProviderDetails(
//        uid: String,
//        description: String,
//        specialty: String,
//        perDayCharge: String,
//        chargeDescription: String
//    ): Resource<Unit> {
//        return try {
//            val updates = mapOf(
//                "description" to description,
//                "specialty" to specialty,
//                "perDayCharge" to perDayCharge,
//                "chargeDescription" to chargeDescription
//            )
//            firestore.collection("serviceProviders").document(uid)
//                .update(updates)
//                .await()
//            Resource.Success(Unit)
//        } catch (e: Exception) {
//            if (e is CancellationException) throw e
//            Resource.Error(e.message ?: "Failed to update details.")
//        }
//    }
//
//    // --- ADD THIS NEW FUNCTION ---
//    override suspend fun getProviderBookings(): Resource<List<Booking>> {
//        return try {
//            val uid = auth.currentUser?.uid
//                ?: return Resource.Error("User not logged in")
//
//            val snapshot = firestore.collection("bookings")
//                .whereEqualTo("providerId", uid)
//                .orderBy("startDate", Query.Direction.DESCENDING)
//                .get()
//                .await()
//
//            val bookings = snapshot.documents.mapNotNull { doc ->
//                val dto = doc.toObject(BookingDto::class.java)
//                dto?.let { mapDtoToBooking(it) } // Use a mapper function
//            }
//
//            Resource.Success(bookings)
//        } catch (e: Exception) {
//            if (e is CancellationException) throw e
//            Resource.Error(e.message ?: "Failed to fetch bookings.")
//        }
//    }
//
//    // --- Helper function to map DTO to Domain Model ---
//    // --- Helper function to map DTO to Domain Model ---
//    private fun mapDtoToBooking(dto: BookingDto): Booking {
//        val startDate = dto.startDate?.toDate() ?: Date()
//        val endDate = dto.endDate?.toDate() ?: Date()
//
//        // Calculate duration
//        val diffInMillis = endDate.time - startDate.time
//        // Use coerceAtLeast(1) to ensure at least 1 day is shown
//        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis).coerceAtLeast(1)
//        val duration = if (days == 1L) "1 Day" else "$days Days"
//
//        // Format dates
//        val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
//        val dateRange = if (days == 1L) {
//            dateFormatter.format(startDate)
//        } else {
//            // Format start and end for the range
//            val startFormat = SimpleDateFormat("MMM d", Locale.getDefault())
//            val endFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
//            "${startFormat.format(startDate)} - ${endFormat.format(endDate)}"
//        }
//
//        return Booking(
//            bookingId = dto.bookingId ?: UUID.randomUUID().toString(),
//            userName = dto.userName ?: "Unknown User",
//            userPhone = dto.userPhone ?: "No Phone",
//            dateRange = dateRange,
//            durationInDays = duration,
//            totalCharge = dto.totalCharge ?: 0.0,
//            status = dto.status ?: "PENDING",
//            startDate = startDate // <-- ADD THIS FIELD
//        )
//    }
//}


// file: com/dholsagar/app/data/repository/impl/ProviderRepositoryImpl.kt
package com.dholsagar.app.data.repository.impl

import android.net.Uri
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.data.remote.dto.BookingDto
import com.dholsagar.app.data.remote.dto.ServiceProviderDto
import com.dholsagar.app.domain.model.Booking
import com.dholsagar.app.domain.model.ServiceProvider
import com.dholsagar.app.domain.repository.ProviderRepository
import com.dholsagar.app.presentation.onboarding_provider.TeamMember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class ProviderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
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
                        portfolioImageUrls = it.portfolioImages ?: emptyList(),
                        description = it.description ?: "",
                        specialty = it.specialty ?: "",
                        perDayCharge = it.perDayCharge ?: "",
                        chargeDescription = it.chargeDescription ?: ""
                    )
                }
            }
            Resource.Success(providers)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Failed to fetch providers.")
        }
    }

    override suspend fun getProviderDetails(providerId: String): Resource<ServiceProvider> {
        return try {
            val document = firestore.collection("serviceProviders").document(providerId).get().await()
            val dto = document.toObject(ServiceProviderDto::class.java)

            if (dto != null) {
                val provider = ServiceProvider(
                    uid = dto.providerUid ?: "",
                    bandName = dto.bandName ?: "Unknown Band",
                    leadProviderName = dto.leadProviderName ?: "",
                    leadProviderRole = dto.leadProviderRole ?: "",
                    location = dto.location ?: "Unknown Location",
                    experienceYears = dto.experienceYears ?: 0,
                    portfolioImageUrls = dto.portfolioImages ?: emptyList(),
                    portfolioVideoUrl = dto.portfolioVideoUrl,
                    youtubeLink = dto.youtubeLink,
                    teamMembers = dto.teamMembers?.map {
                        TeamMember(it["name"] ?: "", it["role"] ?: "")
                    } ?: emptyList(),
                    kycStatus = dto.kycStatus ?: "PENDING",
                    description = dto.description ?: "",
                    specialty = dto.specialty ?: "",
                    perDayCharge = dto.perDayCharge ?: "",
                    chargeDescription = dto.chargeDescription ?: ""
                )
                Resource.Success(provider)
            } else {
                Resource.Error("Provider details not found.")
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Failed to fetch provider details.")
        }
    }

    override suspend fun uploadFile(uri: Uri, path: String, mimeType: String?): Resource<String> {
        return try {
            val fileName = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("$path/$fileName")

            val metadata = StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()

            val uploadTask = storageRef.putFile(uri, metadata).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()

            Resource.Success(downloadUrl.toString())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.localizedMessage ?: "File upload failed.")
        }
    }

    override suspend fun createProviderProfile(
        uid: String,
        name: String,
        bandName: String,
        gmail: String?,
        role: String,
        phone: String,
        experience: Int,
        teamMembers: List<TeamMember>,
        portfolioImageUrls: List<String>,
        portfolioVideoUrl: String?,
        youtubeLink: String?,
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
            )
            firestore.collection("serviceProviders").document(uid).set(providerProfileDto).await()

            val authUser = auth.currentUser!!
            val userProfileUpdates = mapOf(
                "name" to name,
                "email" to (gmail ?: authUser.email),
                "phone" to "+91$phone"
            ).filterValues { it != null && it.isNotBlank() }

            firestore.collection("users").document(uid).update(userProfileUpdates).await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Failed to create provider profile.")
        }
    }

    override suspend fun updateProviderDetails(
        uid: String,
        description: String,
        specialty: String,
        perDayCharge: String,
        chargeDescription: String
    ): Resource<Unit> {
        return try {
            val updates = mapOf(
                "description" to description,
                "specialty" to specialty,
                "perDayCharge" to perDayCharge,
                "chargeDescription" to chargeDescription
            )
            firestore.collection("serviceProviders").document(uid)
                .update(updates)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Failed to update details.")
        }
    }

    override suspend fun getProviderBookings(): Resource<List<Booking>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Resource.Error("User not logged in")

            val snapshot = firestore.collection("bookings")
                .whereEqualTo("providerId", uid)
                .orderBy("startDate", Query.Direction.DESCENDING)
                .get()
                .await()

            val bookings = snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(BookingDto::class.java)
                dto?.let { mapDtoToBooking(it) }
            }

            Resource.Success(bookings)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Failed to fetch bookings.")
        }
    }

    private fun mapDtoToBooking(dto: BookingDto): Booking {
        val startDate = dto.startDate?.toDate() ?: Date()
        val endDate = dto.endDate?.toDate() ?: Date()

        val diffInMillis = endDate.time - startDate.time
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis).coerceAtLeast(1)
        val duration = if (days == 1L) "1 Day" else "$days Days"

        val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val dateRange = if (days == 1L) {
            dateFormatter.format(startDate)
        } else {
            val startFormat = SimpleDateFormat("MMM d", Locale.getDefault())
            val endFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            "${startFormat.format(startDate)} - ${endFormat.format(endDate)}"
        }

        return Booking(
            bookingId = dto.bookingId ?: UUID.randomUUID().toString(),
            userName = dto.userName ?: "Unknown User",
            userPhone = dto.userPhone ?: "No Phone",
            dateRange = dateRange,
            durationInDays = duration,
            totalCharge = dto.totalCharge ?: 0.0,
            status = dto.status ?: "PENDING",
            startDate = startDate
        )
    }
}