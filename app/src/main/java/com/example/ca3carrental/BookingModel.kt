package com.example.ca3carrental

import com.google.firebase.database.Exclude

class BookingModel(
    val appointmentId: String? =null,
    val userId: String? =null,
    val carId: String? =null,
    val pickupDateTime: String? =null, // Changed to String for Firebase compatibility
    val returnDateTime: String? =null, // Changed to String for Firebase compatibility
    val price: String? =null, // Changed to a numeric data type for price
    var isConfirmed: Boolean? = null
){
    // Default constructor for Firebase
//    constructor() : this("","", "", "", "", "", false)

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "appointmentId" to appointmentId,
            "userId" to userId,
            "carId" to carId,
            "pickupDateTime" to pickupDateTime,
            "returnDateTime" to returnDateTime,
            "price" to price,
            "isConfirmed" to isConfirmed
        )
    }
}