// file: com/dholsagar/app/domain/model/User.kt
package com.dholsagar.app.domain.model

data class User(
    val uid: String = "",
    val name: String? = null,
    val email: String? = null,
    val userType: String = "USER" // Default to USER type
)