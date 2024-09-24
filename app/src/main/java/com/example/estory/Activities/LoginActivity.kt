package com.example.estory.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.example.estory.LoginFragments.LoginFragment
import com.example.estory.R
import com.example.estory.UserDetails.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Enable edge-to-edge mode
        WindowCompat.setDecorFitsSystemWindows(window, false)

        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progress_bar)
        loadingLayout = findViewById(R.id.loading_layout)

        // Check if the user is already logged in
        if (auth.currentUser != null) {
            showLoading(true) // Show loading spinner
            // User is signed in, fetch details
            fetchUserDetails(auth.currentUser!!.uid)
        } else {
            // No user is signed in, show login fragment
            loadFragment(LoginFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun fetchUserDetails(uid: String) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("estory").child("users").child(uid).child("profile").get()
            .addOnSuccessListener { snapshot ->
                showLoading(false) // Hide loading spinner
                if (snapshot.exists()) {
                    val name = snapshot.child("user_name").getValue(String::class.java)
                    val phone = snapshot.child("user_phno").getValue(String::class.java)
                    val nickname = snapshot.child("user_nick_name").getValue(String::class.java)
                    val language = snapshot.child("user_language").getValue(String::class.java)
                    val email = auth.currentUser?.email

                    // Store data in UserData object
                    UserData.name = name
                    UserData.phoneNumber = phone
                    UserData.nickname = nickname
                    UserData.email = email
                    UserData.language = language

                    // Redirect to ApplicationScreen
                    startActivity(Intent(this, ApplicationScreen::class.java))
                    finish()
                } else {
                    loadFragment(LoginFragment()) // If no user details, show login fragment
                }
            }
            .addOnFailureListener {
                showLoading(false) // Hide loading spinner
                loadFragment(LoginFragment()) // Show login fragment on failure
            }
    }

    private fun showLoading(isLoading: Boolean) {
        loadingLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
