// file: com/dholsagar/app/domain/repository/AuthRepository.kt
package com.dholsagar.app.domain.repository

import android.app.Activity
import android.content.IntentSender
import com.dholsagar.app.core.util.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

// THIS DATA CLASS WAS MISSING. IT'S NOW DEFINED HERE.
data class UserProfile(
    val uid: String = "",
    val displayName: String? = null,
    val email: String? = null,
    val userType: String = "USER" // "USER" or "PROVIDER"
)

sealed class GoogleSignInResult {
    data class Success(val intentSender: IntentSender) : GoogleSignInResult()
    data class Error(val message: String) : GoogleSignInResult()
}

sealed class PhoneAuthResult {
    data class CodeSent(val verificationId: String) : PhoneAuthResult()
    data class Error(val message: String) : PhoneAuthResult()
    object VerificationCompleted : PhoneAuthResult()
}

// The main repository interface
interface AuthRepository {
    suspend fun getGoogleSignInIntentSender(): GoogleSignInResult
    suspend fun signInWithGoogle(idToken: String): Resource<AuthResult>
    fun sendOtp(phoneNumber: String, activity: Activity): Flow<PhoneAuthResult>
    suspend fun verifyOtp(verificationId: String, otp: String): Resource<AuthResult>
    suspend fun checkUserExists(uid: String): Resource<Boolean>
    suspend fun getUserProfile(uid: String): Resource<UserProfile> // This now resolves correctly
    suspend fun createUserProfile(firebaseUser: FirebaseUser, userType: String): Resource<Unit>
    suspend fun signOut(): Resource<Unit>
}