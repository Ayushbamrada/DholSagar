// file: com/dholsagar/app/data/repository/impl/ProviderRepositoryImpl.kt
package com.dholsagar.app.data.repository.impl

import android.net.Uri
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.data.remote.dto.BookingDto
import com.dholsagar.app.data.remote.dto.ServiceProviderDto
import com.dholsagar.app.domain.model.AdBanner
import com.dholsagar.app.domain.model.Booking
import com.dholsagar.app.domain.model.ServiceProvider
import com.dholsagar.app.domain.repository.ProviderRepository
import com.dholsagar.app.presentation.onboarding_provider.TeamMember
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
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
            location = "Unknown Location", // <-- THIS IS THE FIX (Default value)
            dateRange = dateRange,
            durationInDays = duration,
            totalCharge = dto.totalCharge ?: 0.0,
            status = dto.status ?: "PENDING",
            startDate = startDate,
            userRating = null,
            userFeedback = null
        )
    }
    override suspend fun getDashboardAd(): Resource<AdBanner> {
        return try {
            // We look for a collection 'app_config' and doc 'provider_ad'
            val snapshot = firestore.collection("app_config").document("provider_ad").get().await()
            if (snapshot.exists()) {
                val ad = snapshot.toObject(AdBanner::class.java)
                Resource.Success(ad ?: AdBanner())
            } else {
                // Default mock ad if nothing in database
                Resource.Success(
                    AdBanner(
                    title = "Diwali Special Boost!",
                    description = "Get 5x more bookings this week by updating your calendar.",
                    isActive = true
                )
                )
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load ad")
        }
    }
    // --- NEW PORTFOLIO FUNCTIONS START HERE ---
    // -------------------------------------------------------------------------

    override suspend fun addPortfolioImage(uid: String, uri: Uri): Resource<Unit> {
        return try {
            // 1. Upload to Storage
            val downloadUrlResult = uploadFile(uri, "portfolio_images/$uid", "image/jpeg")
            if (downloadUrlResult is Resource.Error) return Resource.Error(downloadUrlResult.message ?: "Upload failed")
            val downloadUrl = downloadUrlResult.data!!

            // 2. Add URL to Firestore Array
            firestore.collection("serviceProviders").document(uid)
                .update("portfolioImages", FieldValue.arrayUnion(downloadUrl))
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add image")
        }
    }

    override suspend fun removePortfolioImage(uid: String, imageUrl: String): Resource<Unit> {
        return try {
            // 1. Remove from Firestore Array
            firestore.collection("serviceProviders").document(uid)
                .update("portfolioImages", FieldValue.arrayRemove(imageUrl))
                .await()

            // 2. Try to delete from Storage (Fire & Forget, don't crash if fails)
            try {
                storage.getReferenceFromUrl(imageUrl).delete()
            } catch (e: Exception) { /* Log error silently */ }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove image")
        }
    }

    override suspend fun updatePortfolioVideo(uid: String, uri: Uri): Resource<Unit> {
        return try {
            val downloadUrlResult = uploadFile(uri, "portfolio_videos/$uid", "video/mp4")
            if (downloadUrlResult is Resource.Error) return Resource.Error(downloadUrlResult.message ?: "Upload failed")
            val downloadUrl = downloadUrlResult.data!!

            firestore.collection("serviceProviders").document(uid)
                .update("portfolioVideoUrl", downloadUrl)
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update video")
        }
    }

    override suspend fun removePortfolioVideo(uid: String): Resource<Unit> {
        return try {
            // Set field to null
            firestore.collection("serviceProviders").document(uid)
                .update("portfolioVideoUrl", null)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove video")
        }
    }

    // --- 1. GENERATE DUMMY DATA ---
    override suspend fun generateDummyBookings(providerId: String): Resource<Unit> {
        return try {
            val calendar = Calendar.getInstance()

            // Helper to get a date offset
            fun getDate(daysToAdd: Int): Date {
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
                return calendar.time
            }

            val dummyBookings = listOf(
                // 1. REQUEST (Needs Action)
                BookingDto(
                    bookingId = UUID.randomUUID().toString(),
                    providerId = providerId,
                    userId = "dummy_user_1",
                    userName = "Rahul Sharma",
                    userPhone = "+919876543210",
                    location = "Grand Hyatt, Mumbai",
                    startDate = Timestamp(getDate(1)), // Tomorrow
                    endDate = Timestamp(getDate(2)),
                    totalCharge = 15000.0,
                    status = "REQUESTED"
                ),
                // 2. UPCOMING (Confirmed)
                BookingDto(
                    bookingId = UUID.randomUUID().toString(),
                    providerId = providerId,
                    userId = "dummy_user_2",
                    userName = "Priya Patel",
                    userPhone = "+919876543211",
                    location = "Farmhouse, Lonavala",
                    startDate = Timestamp(getDate(5)), // 5 Days later
                    endDate = Timestamp(getDate(6)),
                    totalCharge = 25000.0,
                    status = "CONFIRMED"
                ),
                // 3. HISTORY (Completed)
                BookingDto(
                    bookingId = UUID.randomUUID().toString(),
                    providerId = providerId,
                    userId = "dummy_user_3",
                    userName = "Amit Singh",
                    userPhone = "+919876543212",
                    location = "City Hall",
                    startDate = Timestamp(getDate(-10)), // 10 Days ago
                    endDate = Timestamp(getDate(-9)),
                    totalCharge = 12000.0,
                    status = "COMPLETED"
                ),
                // 4. HISTORY (Cancelled)
                BookingDto(
                    bookingId = UUID.randomUUID().toString(),
                    providerId = providerId,
                    userId = "dummy_user_4",
                    userName = "Sneha Gupta",
                    userPhone = "+919876543213",
                    location = "Unknown",
                    startDate = Timestamp(getDate(-5)), // 5 Days ago
                    endDate = Timestamp(getDate(-5)),
                    totalCharge = 5000.0,
                    status = "CANCELLED"
                )
            )

            // Upload all to Firestore
            val batch = firestore.batch()
            dummyBookings.forEach { booking ->
                val ref = firestore.collection("bookings").document(booking.bookingId!!)
                batch.set(ref, booking)
            }
            batch.commit().await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to generate dummy data")
        }
    }

    // --- 2. UPDATE STATUS (Accept/Decline) ---
    override suspend fun updateBookingStatus(bookingId: String, newStatus: String): Resource<Unit> {
        return try {
            firestore.collection("bookings").document(bookingId)
                .update("status", newStatus)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update status")
        }
    }
}