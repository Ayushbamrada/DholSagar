// file: com/dholsagar/app/data/repository/impl/AuthRepositoryImpl.kt
package com.dholsagar.app.data.repository.impl

import android.app.Activity
import android.content.Context
import com.dholsagar.app.R
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.data.remote.dto.UserDto
import com.dholsagar.app.domain.model.User
import com.dholsagar.app.domain.repository.*
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore, // Add Firestore dependency
    private val oneTapClient: SignInClient,
    @ApplicationContext private val context: Context
) : AuthRepository {


    override val currentUser: FirebaseUser?
        get() = auth.currentUser


    override suspend fun getGoogleSignInIntentSender(): GoogleSignInResult {
        return try {
            val result = oneTapClient.beginSignIn(
                BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setServerClientId(context.getString(R.string.default_web_client_id))
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                    )
                    .setAutoSelectEnabled(true)
                    .build()
            ).await()
            GoogleSignInResult.Success(result.pendingIntent.intentSender)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            GoogleSignInResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Resource<AuthResult> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Resource.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override fun sendOtp(phoneNumber: String, activity: Activity): Flow<PhoneAuthResult> {
        return callbackFlow {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    trySend(PhoneAuthResult.VerificationCompleted)
                }

                override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                    trySend(PhoneAuthResult.Error(e.message ?: "Verification failed"))
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    trySend(PhoneAuthResult.CodeSent(verificationId))
                }
            }
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            awaitClose { /* No-op */ }
        }
    }

    override suspend fun verifyOtp(verificationId: String, otp: String): Resource<AuthResult> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val result = auth.signInWithCredential(credential).await()
            Resource.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun checkUserExists(uid: String): Resource<Boolean> {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            Resource.Success(document.exists())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to check user existence")
        }
    }

    // THIS FUNCTION IS NOW CORRECTLY IMPLEMENTED
    override suspend fun getUserProfile(uid: String): Resource<User> {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            // THIS IS THE FIX: We tell Firestore to map the data to our UserDto class
            val userDto = document.toObject(UserDto::class.java)

            if (userDto?.uid != null) {
                // Convert the DTO to our clean domain model
                val user = User(
                    uid = userDto.uid,
                    name = userDto.name,
                    email = userDto.email,
                    phone = userDto.phone,
                    userType = userDto.userType ?: "USER"
                )
                Resource.Success(user)
            } else {
                Resource.Error("User profile not found.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun createUserProfile(user: FirebaseUser, userType: String): Resource<Unit> {
        return try {
            // FIX 1: Use the correct data class: UserDto
            // FIX 2: Use the correct variable for the phone number: user.phoneNumber
            val userDto = UserDto(
                uid = user.uid,
                email = user.email,
                name = user.displayName,
                phone = user.phoneNumber, // Get the phone number from the FirebaseUser object
                userType = userType
            )
            firestore.collection("users").document(user.uid).set(userDto).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Could not create user profile.")
        }
    }

    override suspend fun signOut(): Resource<Unit> {
        return try {
            auth.signOut()
            oneTapClient.signOut().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sign out failed")
        }
    }
}