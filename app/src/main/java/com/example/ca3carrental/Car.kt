package com.example.ca3carrental

class Car(
    val carId: String? = null,
    val carName: String = "",
    val manufacturer: String = "",
    val seats: Int = 0,
    val price: Double = 0.0,
    val mileage: String = "",
    val model: String = "",
    val year: Int = 0,
    val fuelType: String = "",
    val imageUrl: String = "",
    val location: String = ""
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "", "", 0, 0.0, "", "", 0, "")
}
