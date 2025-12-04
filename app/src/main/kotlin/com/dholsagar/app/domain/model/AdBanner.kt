package com.dholsagar.app.domain.model
data class AdBanner(
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val isActive: Boolean = false
)