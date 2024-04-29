package com.example.ca3carrental

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


class FragBookings : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var bookingList: MutableList<BookingModel>
    private lateinit var userBookingAdapter: UserBookingAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_frag_bookings, container, false)
        database = FirebaseDatabase.getInstance().reference

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        bookingList= mutableListOf()
        userBookingAdapter = UserBookingAdapter(bookingList,database)
        recyclerView.adapter = userBookingAdapter
        sharedPreferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "").toString()
        fetchUserBookings(userId)
        return view
    }

    private fun fetchUserBookings(userId:String) {
        val query: Query = database.child("bookings").orderByChild("userId").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                if (dataSnapshot.exists()) {
                    for (appointmentSnapshot in dataSnapshot.children) {
                        val bookings = appointmentSnapshot.getValue(BookingModel::class.java)
                        bookings?.let {
                            bookingList.add(it)
                        }
                    }
                    userBookingAdapter.bookings = bookingList
                    userBookingAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }


}