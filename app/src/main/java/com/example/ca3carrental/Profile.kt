package com.example.ca3carrental

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User

class Profile : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String
    private lateinit var ti1: TextInputLayout
    private lateinit var ti2: TextInputLayout
    private lateinit var ti3: TextInputLayout
    private lateinit var nameTV: TextView
    private lateinit var emailTV: TextView
    private lateinit var logout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ti1 = findViewById(R.id.full_name_profile) as TextInputLayout
        ti2 = findViewById(R.id.email) as TextInputLayout
        ti3 = findViewById(R.id.phoneNumber1) as TextInputLayout
        nameTV = findViewById(R.id.fullname_field)
        emailTV = findViewById(R.id.username_field)

        sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "").toString()

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Fetch user details
        fetchUserDetails(userId)
        logout=findViewById(R.id.logout)
        logout.setOnClickListener {
            // Clear the logged-in state
            val sharedPreferencesManager = SharedPreferencesManager(this)
            sharedPreferencesManager.setLoggedIn(false)

            // Navigate back to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUserDetails(userId: String) {
        database.child("users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        user?.let {
                            // Update UI with user details
                            nameTV.text = it.name
                            emailTV.text = it.email
                            ti1.editText?.setText(it.name)
                            ti2.editText?.setText(it.email)
                            ti3.editText?.setText(it.phoneNumber)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                }
            })
    }
    data class User(
        val name: String = "",
        val email: String = "",
        val phoneNumber: String = ""
    )
}