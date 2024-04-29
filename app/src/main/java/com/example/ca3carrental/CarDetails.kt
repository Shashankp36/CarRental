package com.example.ca3carrental

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import com.bumptech.glide.Glide
import com.example.ca3carrental.databinding.ActivityCarDetailsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CarDetails : AppCompatActivity() {
    private lateinit var binding: ActivityCarDetailsBinding
    private lateinit var database: DatabaseReference
    private var carId: String? = null
    private var carPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference.child("cars")
        carId = intent.getStringExtra("CAR_ID")

        carId?.let { fetchCarDetails(it) }
        binding.bookThisCar.setOnClickListener {
            // Get the selected insurance option
            val selectedInsuranceOptionId = binding.insuranceOption.checkedRadioButtonId
            val selectedInsuranceOptionPrice = when (selectedInsuranceOptionId) {
                R.id.option_none -> 0
                R.id.option_basic -> 900
                R.id.option_premium -> 1200
                else -> 0 // Default value if no option is selected or unexpected ID
            }
            // Create an Intent to start the Booking activity
            val intent = Intent(this, Bookings::class.java)

            // Put the car ID, car price, and the selected insurance option as extras in the Intent
            intent.putExtra("CAR_ID", carId)
            intent.putExtra("CAR_PRICE", carPrice.toString())
            intent.putExtra("INSURANCE_PRICE", selectedInsuranceOptionPrice)

            // Start the Booking activity
            startActivity(intent)
        }
    }

    private fun fetchCarDetails(carId: String) {
        database.child(carId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val car = snapshot.getValue(Car::class.java)
                    car?.let {
                        displayCarDetails(it)
                        carPrice = it.price
                    // Store car price
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun displayCarDetails(car: Car) {
        // Populate UI elements with car details
        binding.carName.text = car.carName.toString()
        binding.vehiclePrice.text = "\u20B9${car.price.toString()}/day"
        binding.year.text = car.year.toString()
        binding.type.text = car.fuelType
        binding.seats.text = car.seats.toString()
        binding.mileage.text = "${car.mileage}Kmpl"
        binding.model.text = car.model
        binding.manufacturer.text = car.manufacturer

        // Load image using Glide
        Glide.with(this)
            .load(car.imageUrl)
            .into(binding.vehicleImage)
    }
    fun onBackButtonClick(view: View) {
        super.onBackPressed()
    }
}