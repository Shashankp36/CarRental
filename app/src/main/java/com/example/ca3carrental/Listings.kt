package com.example.ca3carrental

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Listings : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var carAdapter: CarAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var carList: MutableList<Car>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listings)

        recyclerView = findViewById(R.id.recyclerViewListings)
        recyclerView.layoutManager = LinearLayoutManager(this)
        carList = mutableListOf()
        carAdapter = CarAdapter(this, carList)

        recyclerView.adapter = carAdapter

        databaseRef = FirebaseDatabase.getInstance().getReference("cars")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    carList.clear()
                    for (carSnapshot in snapshot.children) {
                        val car = carSnapshot.getValue(Car::class.java)
                        car?.let { carList.add(it) }
                    }
                    carAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}