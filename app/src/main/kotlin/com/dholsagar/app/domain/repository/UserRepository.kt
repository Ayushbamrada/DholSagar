package com.dholsagar.app.domain.repository

import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.model.User

interface UserRepository {
    // Used during initial Sign Up / Login
    suspend fun saveUser(user: User): Resource<Unit>

    // Used to display the Profile Screen
    suspend fun getUserProfile(uid: String): Resource<User>

    // Used to Edit the Profile
    suspend fun updateUserProfile(uid: String, name: String, email: String, phone: String): Resource<Unit>
}