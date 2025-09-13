// file: com/dholsagar/app/domain/repository/UserRepository.kt
package com.dholsagar.app.domain.repository

import com.dholsagar.app.core.util.Resource

interface UserRepository {
    suspend fun updateUserProfile(uid: String, name: String, email: String, phone: String): Resource<Unit>
}