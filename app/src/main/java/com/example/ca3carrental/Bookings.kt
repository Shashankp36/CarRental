package com.example.ca3carrental

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID
import kotlin.math.ceil

class Bookings : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var pickupDateText: TextView
    private lateinit var pickupTimeText: TextView
    private lateinit var returnDateText: TextView
    private lateinit var returnTimeText: TextView
    private lateinit var booking: Button
    private lateinit var showPriceText: TextView
    private lateinit var durationHours: Duration
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var userId: String = ""
    private var pickupValue: String = ""
    private var returnValue: String = ""
    private var finalPrice: Double = 0.0 // Changed to Double
    private lateinit var Calculate:Button
    private var insurancePrice: Int = 0


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookings)

        pickupDateText = findViewById(R.id.pickupDate)
        pickupTimeText = findViewById(R.id.pickupTime)
        returnDateText = findViewById(R.id.returnDate)
        returnTimeText = findViewById(R.id.returnTime)
        showPriceText = findViewById(R.id.showPrice)
        booking = findViewById(R.id.button)
        database = FirebaseDatabase.getInstance().reference.child("bookings")
        sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "").toString()
        insurancePrice = intent.getIntExtra("INSURANCE_PRICE", 0)

//        Toast.makeText(this,"User:${userId}",Toast.LENGTH_LONG).show()

        pickupDateText.setOnClickListener {
            showPickupDatePicker()
        }

        pickupTimeText.setOnClickListener {
            showPickupTimePicker()
        }

        returnDateText.setOnClickListener {
            showReturnDatePicker()
        }

        returnTimeText.setOnClickListener {
            showReturnTimePicker()
        }
        Calculate=findViewById(R.id.calculateButton)
        Calculate.setOnClickListener {
            calculateDifferenceAndUpdateUI()

        }
        booking.setOnClickListener {

            val carId = intent.getStringExtra("CAR_ID")
            val pricePerDay = intent.getStringExtra("CAR_PRICE")?.toDouble()


            if (carId != null && pricePerDay != null) {
                bookAppointment(userId, carId, finalPrice.toString(), returnValue, pickupValue)
            }
        }
    }

    private fun bookAppointment(userId: String, carId: String, price: String, returnDateTime: String, pickupDateTime: String) {
        val appointmentId = UUID.randomUUID().toString()

        val appointment = BookingModel(
            appointmentId,
            userId,
            carId,
            pickupDateTime,
            returnDateTime,
            price,
            false
        )

        database.child(appointmentId).setValue(appointment)
            .addOnSuccessListener {
                Toast.makeText(this, "Booked request for: $pickupDateTime", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to book request. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPickupDatePicker() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0)
                val formattedDate = selectedDate.format(dateFormatter)
                pickupDateText.text = formattedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.show()
    }

    private fun showPickupTimePicker() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val selectedTime = LocalDateTime.of(1, 1, 1, selectedHour, selectedMinute)
                val formattedTime = selectedTime.format(timeFormatter)
                pickupTimeText.text = formattedTime
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun showReturnDatePicker() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0)
                val formattedDate = selectedDate.format(dateFormatter)
                returnDateText.text = formattedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.show()
    }

    private fun showReturnTimePicker() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val selectedTime = LocalDateTime.of(1, 1, 1, selectedHour, selectedMinute)
                val formattedTime = selectedTime.format(timeFormatter)
                returnTimeText.text = formattedTime
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun calculateDifferenceAndUpdateUI() {
        val pickupDateTime = LocalDateTime.parse("${pickupDateText.text} ${pickupTimeText.text}", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val returnDateTime = LocalDateTime.parse("${returnDateText.text} ${returnTimeText.text}", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        pickupValue = pickupDateTime.toString()
        returnValue = returnDateTime.toString()
        durationHours = calculateDifference(pickupDateTime, returnDateTime)
        val pricePerDay = intent.getStringExtra("CAR_PRICE")?.toDouble()
//        Toast.makeText(this,"${pricePerDay}",Toast.LENGTH_SHORT).show()
        val days=ceil(durationHours.toDays().toDouble())
        finalPrice = days * ((pricePerDay ?: 0.0)+insurancePrice)
        showPriceText.text = " Total Price : ₹$finalPrice  \n Total insurance price: ₹${days*insurancePrice}"
        if (durationHours.toHours() < 1) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Invalid Time Selection")
            builder.setMessage("Please select a valid pickup and return time.")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun calculateDifference(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Duration {
        return Duration.between(dateTime1, dateTime2)
    }
    fun onBackButtonClick(view: View) {
        super.onBackPressed()
    }
}
