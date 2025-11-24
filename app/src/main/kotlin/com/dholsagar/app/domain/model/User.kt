package com.dholsagar.app.domain.model

data class User(
    val uid: String = "",
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null, // ADD THIS
    val userType: String = "USER"
)