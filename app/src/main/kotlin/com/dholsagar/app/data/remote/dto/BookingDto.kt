// file: com/dholsagar/app/data/remote/dto/BookingDto.kt
package com.dholsagar.app.data.remote.dto

import com.google.firebase.Timestamp

data class BookingDto(
    val bookingId: String? = null,
    val userId: String? = null,
    val providerId: String? = null,
    val userName: String? = null,
    val userPhone: String? = null,
    val location: String? = null,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val totalCharge: Double? = null,
    val status: String? = null, // e.g., "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"
    val bookedAt: Timestamp? = null
)