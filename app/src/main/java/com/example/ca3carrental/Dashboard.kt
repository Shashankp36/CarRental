package com.example.ca3carrental

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class Dashboard : AppCompatActivity() {
    lateinit var  bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    replaceFragment(FragHome())
                    true
                }
                R.id.services -> {
                    replaceFragment(FragBookings())
                    true
                }
                R.id.nav_settings -> {
                    replaceFragment(FragOthers())
                    true
                }
                else -> false
            }
        }

        // Set the default fragment to display
        replaceFragment(FragHome())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}