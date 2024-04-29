package com.example.ca3carrental
import android.content.Context
import android.content.SharedPreferences




    class SharedPreferencesManager(context: Context) {
        private val sharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        fun setLoggedIn(loggedIn: Boolean) {
            sharedPreferences.edit().putBoolean("isLoggedIn", loggedIn).apply()
        }

        fun isLoggedIn(): Boolean {
            return sharedPreferences.getBoolean("isLoggedIn", false)
        }
    }

