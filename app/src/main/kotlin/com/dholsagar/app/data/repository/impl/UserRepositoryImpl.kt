// file: com/dholsagar/app/data/repository/impl/UserRepositoryImpl.kt
package com.dholsagar.app.data.repository.impl

import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun updateUserProfile(uid: String, name: String, email: String,phone: String): Resource<Unit> {
        return try {
            val userProfileUpdates = mapOf(
                "name" to name,
                "email" to email,
                "phone" to "+91$phone" // Add the phone number to the update
            ).filterValues { it.isNotBlank() } // Only update fields that are not blank
            firestore.collection("users").document(uid).update(userProfileUpdates).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Failed to update profile.")
        }
    }
}