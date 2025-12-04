package com.dholsagar.app.data.repository.impl

import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.data.remote.dto.UserDto
import com.dholsagar.app.domain.model.User
import com.dholsagar.app.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    // --- 1. SAVE USER (For Sign Up) ---
    override suspend fun saveUser(user: User): Resource<Unit> {
        return try {
            val userDto = UserDto(
                uid = user.uid,
                name = user.name,
                email = user.email,
                userType = user.userType,
                phone = user.phone
            )
            firestore.collection("users").document(user.uid).set(userDto).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Failed to save user")
        }
    }

    // --- 2. GET PROFILE (For Profile Screen) ---
    override suspend fun getUserProfile(uid: String): Resource<User> {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            val userDto = document.toObject(UserDto::class.java)
            if (userDto != null) {
                Resource.Success(
                    User(
                        uid = userDto.uid ?: "",
                        name = userDto.name ?: "",
                        email = userDto.email ?: "",
                        userType = userDto.userType ?: "",
                        phone = userDto.phone
                    )
                )
            } else {
                Resource.Error("User not found")
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Failed to fetch user profile")
        }
    }

    // --- 3. UPDATE PROFILE (For Edit Screen) ---
    override suspend fun updateUserProfile(uid: String, name: String, email: String, phone: String): Resource<Unit> {
        return try {
            // Ensure we don't double-add the country code
            val formattedPhone = if (phone.startsWith("+91")) phone else "+91$phone"

            val userProfileUpdates = mapOf(
                "name" to name,
                "email" to email,
                "phone" to formattedPhone
            ).filterValues { it.isNotBlank() } // Only update fields that are not blank

            firestore.collection("users").document(uid).update(userProfileUpdates).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Failed to update profile.")
        }
    }
}