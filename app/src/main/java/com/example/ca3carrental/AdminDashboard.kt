package com.example.ca3carrental
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminDashboard : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        val fab: FloatingActionButton = findViewById(R.id.fab_add_car)
        val btnViewListings = findViewById<Button>(R.id.btn_view_listings)
        val btnEditListings = findViewById<Button>(R.id.btn_edit_listings)
        val btnViewRequests = findViewById<Button>(R.id.btn_view_requests)
        val btnLogout=findViewById<Button>(R.id.logoutbt)
        val btnViewConfirmedBookings = findViewById<Button>(R.id.btn_view_confirmed_bookings)
        fab.setOnClickListener {
            val intent = Intent(this, AddCars::class.java)
            startActivity(intent)
        }
        btnViewListings.setOnClickListener {
            val intent=Intent(this,Listings::class.java)
            startActivity(intent)
        }
        btnViewRequests.setOnClickListener {
            val intent=Intent(this,ViewBookings::class.java)
            startActivity(intent)
        }
        btnLogout.setOnClickListener {
            val sharedPreferencesManager = SharedPreferencesManager(this)
            sharedPreferencesManager.setLoggedIn(false)

            // Navigate back to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}