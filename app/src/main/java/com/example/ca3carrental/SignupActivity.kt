package com.example.ca3carrental

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class SignupActivity : AppCompatActivity() {
    private lateinit var nameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passET: EditText
    private lateinit var phET: EditText
    private lateinit var signupBtn: Button
    private lateinit var loginBtn: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        nameET = findViewById(R.id.nameET)
        emailET = findViewById(R.id.emailET)
        passET = findViewById(R.id.passET)
        phET = findViewById(R.id.phET)
        signupBtn = findViewById(R.id.signup_btn)
        loginBtn = findViewById(R.id.loginbtn)

        loginBtn.setOnClickListener {
            val intent1 = Intent(this, LoginActivity::class.java)
            startActivity(intent1)
            finish()
        }

        signupBtn.setOnClickListener {
            val name = nameET.text.toString()
            val email = emailET.text.toString()
            val password = passET.text.toString()
            val phoneNumber = phET.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && phoneNumber.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = firebaseAuth.currentUser
                            val user = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "phoneNumber" to phoneNumber
                            )
                            if (firebaseUser != null) {
                                // Store user data in Firestore
                                firestore.collection("users").document(firebaseUser.uid)
                                    .set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@SignupActivity,
                                            "Successfully Registered",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this@SignupActivity,
                                            "Failed to register: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                // Store user data in Realtime Database
                                databaseReference.child("users").child(firebaseUser.uid)
                                    .setValue(user)
                                    .addOnSuccessListener {
                                        // Optional: Handle success if needed
                                    }
                                    .addOnFailureListener { e ->
                                        // Optional: Handle failure if needed
                                    }

                                val intent = Intent(
                                    this@SignupActivity,
                                    LoginActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val exception = task.exception
                            if (exception is FirebaseAuthException) {
                                val errorMessage = exception.message
                                Toast.makeText(
                                    this@SignupActivity,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_LONG).show()
            }
        }
    }
}