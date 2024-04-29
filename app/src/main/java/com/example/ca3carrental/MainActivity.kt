package com.example.ca3carrental
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.ca3carrental.Dashboard
import com.example.ca3carrental.R
import com.example.ca3carrental.SharedPreferencesManager
import com.example.ca3carrental.WelcomeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)
    }
}