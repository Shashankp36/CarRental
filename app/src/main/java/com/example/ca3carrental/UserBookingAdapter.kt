package com.example.ca3carrental

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference

class UserBookingAdapter(var bookings: List<BookingModel>, private val database: DatabaseReference) : RecyclerView.Adapter<UserBookingAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val status: TextView = itemView.findViewById(R.id.status)
        val carDetails: TextView = itemView.findViewById(R.id.CarDetails)
        val pickTextView: TextView = itemView.findViewById(R.id.pickTextView)
        val dropTextView: TextView = itemView.findViewById(R.id.dropTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_bookings, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookings.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = bookings[position]

        if (booking.isConfirmed == true) {
            holder.status.text = "Status: Confirmed"
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark))
        } else {
            holder.status.text = "Status: Pending"
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_blue_light))
        }

        holder.pickTextView.text = "Pickup Details: ${booking.pickupDateTime}"
        holder.dropTextView.text = "Drop Details: ${booking.returnDateTime}"
        holder.priceTextView.text = "Price: ₹${booking.price}"

        val carId = booking.carId.toString()
        Log.d("UserBookingsModel",carId)
        database.child("cars").child(carId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val location = snapshot.child("location").value.toString()
                val carName=snapshot.child("carName").value.toString()
                val manuf=snapshot.child("manufacturer").value.toString()
                Log.d("deatils","${location}")
                Log.d("name","${manuf} ${carName}")

                holder.locationTextView.text = "Location: $location"
                holder.carDetails.text="Car Deatils: ${manuf} ${carName}"
            }
        }.addOnFailureListener {
            Log.d("UserbookingModel","carId not found in db")
        }
        holder.locationTextView.setOnClickListener {
            showLocation(holder.locationTextView.text.toString(),holder.itemView.context)
        }

    }

    private fun showLocation(location: String, context: android.content.Context) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=$location")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        ContextCompat.startActivity(context, mapIntent, null)
    }

}