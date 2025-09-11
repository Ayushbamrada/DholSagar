// file: com/dholsagar/app/data/remote/dto/UserDto.kt
package com.dholsagar.app.data.remote.dto

// This class represents how a User is stored in Firestore
data class UserDto(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val userType: String? = null
)