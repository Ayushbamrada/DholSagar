package com.dholsagar.app.domain.model

import java.util.Date

data class Booking(
    val bookingId: String,
    val userName: String,
    val userPhone: String,
    val location: String, // New Field
    val dateRange: String,
    val durationInDays: String,
    val totalCharge: Double,
    val status: String, // "REQUESTED", "CONFIRMED", "COMPLETED", "CANCELLED"
    val startDate: Date,
    val userRating: Double? = null, // New Field
    val userFeedback: String? = null // New Field
)