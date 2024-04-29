package com.example.ca3carrental

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference

class BookingsAdapter  ( var bookings: List<BookingModel>, private val database: DatabaseReference) : RecyclerView.Adapter<BookingsAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookingIdTextView: TextView = itemView.findViewById(R.id.bookingIdTextView)
        val cardetail:TextView=itemView.findViewById(R.id.carDetails)
        val clientNameTextView: TextView = itemView.findViewById(R.id.clientNameTextView)
        val pickTextView: TextView = itemView.findViewById(R.id.pickTextView)
        val dropTextView: TextView = itemView.findViewById(R.id.dropTextView)
        val clientContactTextView: TextView = itemView.findViewById(R.id.clientContactTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val confirmButton: Button = itemView.findViewById(R.id.confirmButton)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.bookingitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingsAdapter.ViewHolder, position: Int) {
        val bookings = bookings[position]
        holder.bookingIdTextView.text = "Id: ${bookings.appointmentId}"
        holder.pickTextView.text = "pickupDate: ${bookings.pickupDateTime}"
        holder.dropTextView.text = "returnDate: ${bookings.returnDateTime}"
        holder.priceTextView.text = "price: ₹${bookings.price}"

        Log.d("clientID",bookings.userId.toString())

        database.child("cars").child(bookings.carId.toString()).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val location = snapshot.child("location").value.toString()
                val carName=snapshot.child("carName").value.toString()
                val manuf=snapshot.child("manufacturer").value.toString()

                holder.locationTextView.text = "Location: $location"
                holder.cardetail.text="Car Deatils: ${manuf} ${carName}"
            }
        }.addOnFailureListener {

        }

        database.child("users").child(bookings.userId.toString()).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val clientName = snapshot.child("name").value.toString()
                val clientContact = snapshot.child("phoneNumber").value.toString()

                holder.clientNameTextView.text = "Client Name: $clientName"
                holder.clientContactTextView.text = "Contact Info: $clientContact"
            }
        }.addOnFailureListener {

        }



        if (bookings.isConfirmed == true) {
            holder.confirmButton.text = "Booking Confirmed"
            holder.confirmButton.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.gray14))
            holder.confirmButton.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white)) // Set text color to white
            holder.confirmButton.isEnabled = false // Disable the button
        } else {
            holder.confirmButton.text = "Confirm Appointment"
            holder.confirmButton.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
            holder.confirmButton.isEnabled = true // Enable the button
        }
        // Handle confirm button click
        holder.confirmButton.setOnClickListener {
            val bookingId = bookings.appointmentId
            if (bookingId != null) {
                confirmAppointment(holder.itemView.context,holder,bookingId)
            }
        }
    }
    private fun confirmAppointment(context: Context, holder: ViewHolder, appointmentId: String) {
        // Update appointment status in the database to confirmed
        database.child("bookings").child(appointmentId).child("confirmed").setValue(true)
            .addOnSuccessListener {

                holder.confirmButton.text = "Booking Confirmed"
                holder.confirmButton.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.gray14))
                holder.confirmButton.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white)) // Set text color to white
                holder.confirmButton.isEnabled = false // Disable the button
                Toast.makeText(context, "Appointment confirmed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Appointment confirmation failed", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int {
        return bookings.size
    }
}