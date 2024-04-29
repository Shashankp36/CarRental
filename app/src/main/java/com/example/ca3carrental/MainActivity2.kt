package com.example.ca3carrental

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.loadUrl("https://echallan.parivahan.gov.in/index/accused-challan")
    }
}