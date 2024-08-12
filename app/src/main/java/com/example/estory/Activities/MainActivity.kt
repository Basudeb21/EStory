package com.example.estory.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import com.example.estory.R
import com.example.estory.backend.Information

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var info = Information()
        Handler().postDelayed({
            startActivity(Intent(this, ApplicationScreen::class.java))
            finish()
        },info.SPLASH_TIME_OUT)
    }
}