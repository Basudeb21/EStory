package com.example.estory.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.example.estory.LoginFragments.LoginFragment
import com.example.estory.R
import com.example.estory.Activities.ApplicationScreen
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Enable edge-to-edge mode
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Check if the user is already logged in
        if (FirebaseAuth.getInstance().currentUser != null) {
            // User is logged in, redirect to ApplicationScreen
            val intent = Intent(this, ApplicationScreen::class.java)
            startActivity(intent)
            finish() // Close LoginActivity
            return
        }

        // Load the LoginFragment if the user is not logged in
        loadFragment(LoginFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
