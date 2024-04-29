package com.example.ca3carrental

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class LoginActivity : AppCompatActivity() {
    private lateinit var signup: Button
    private lateinit var login: Button
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferencesManager:SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        signup = findViewById(R.id.signupbtn)
        login = findViewById(R.id.loginbtn)
        email = findViewById(R.id.email)
        pass = findViewById(R.id.pass)
        sharedPreferencesManager = SharedPreferencesManager(this)
//
//        if (sharedPreferencesManager.isLoggedIn()) {
//            // User is already logged in, navigate to Dashboard
//            startActivity(Intent(this@LoginActivity, Dashboard::class.java))
//            finish()
//        }
        if (sharedPreferencesManager.isLoggedIn()) {
            // If logged in, redirect to Dashboard or Profile activity
            // Example:
            val intent = Intent(this@LoginActivity, Dashboard::class.java)
            startActivity(intent)
            finish()
        }

        signup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        login.setOnClickListener {
            val inputEmail = email.text.toString()
            val inputPassword = pass.text.toString()

            firebaseAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = firebaseAuth.currentUser
                        if (firebaseUser != null) {
                            val userId = firebaseUser.uid
                            if (userId.isNotEmpty()) { // Check if userId is not empty
                                if (inputEmail == "admin@admin.com" && inputPassword == "admin123") {
                                    // If admin credentials, redirect to AdminDashboard
                                    val intent =
                                        Intent(this@LoginActivity, AdminDashboard::class.java)
                                    startActivity(intent)
                                } else {
                                    // For normal users, redirect to Dashboard
                                    val intent = Intent(this@LoginActivity, Dashboard::class.java)
//                                    sharedPreferencesManager.setLoggedIn(true)
                                    val sharedPreferences =
                                        getSharedPreferences("UserProfile", MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("userEmail", inputEmail)
                                    editor.putString("userPassword", inputPassword)
                                    editor.putString("userId", userId) // Store userId
                                    editor.apply()
                                    startActivity(intent)
                                }
                                finish()
                            } else {
                                // Handle case when userId is null or empty
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Failed to get user information",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid credentials",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            //user2@gmail.com && user123
        }
    }
}