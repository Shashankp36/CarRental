package com.example.ca3carrental

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class ViewBookings : AppCompatActivity() {
    private lateinit var bookingsRecyclerView:RecyclerView
    private lateinit var bookingsAdapter: BookingsAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_bookings)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView)
        bookingsRecyclerView.layoutManager = LinearLayoutManager(this)
        bookingsAdapter = BookingsAdapter(emptyList(), database)
        bookingsRecyclerView.adapter = bookingsAdapter
        fetchAllAppointments()
    }

    private fun fetchAllAppointments() {
        val query: Query = database.child("bookings")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val bookingsList = mutableListOf<BookingModel>()

                if (dataSnapshot.exists()) {
                    for (appointmentSnapshot in dataSnapshot.children) {
                        val bookings = appointmentSnapshot.getValue(BookingModel::class.java)
                        bookings?.let {
                            bookingsList.add(it)
                        }
                    }
                    bookingsAdapter.bookings = bookingsList
                    bookingsAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

}