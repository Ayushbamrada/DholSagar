package com.dholsagar.app.data.remote.dto

data class UserDto(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null, // ADD THIS
    val userType: String? = null
)