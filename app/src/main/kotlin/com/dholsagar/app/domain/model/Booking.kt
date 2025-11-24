// file: com/dholsagar/app/domain/model/Booking.kt
package com.dholsagar.app.domain.model
import java.util.Date

data class Booking(
    val bookingId: String,
    val userName: String,
    val userPhone: String,
    val dateRange: String, // e.g., "Nov 15 - Nov 17, 2025"
    val durationInDays: String, // e.g., "3 Days"
    val totalCharge: Double,
    val status: String,
    val startDate: Date
)