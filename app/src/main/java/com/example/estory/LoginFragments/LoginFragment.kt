package com.example.estory.LoginFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.estory.Activities.ApplicationScreen
import com.example.estory.R
import com.example.estory.UserDetails.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginFragment : Fragment() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signup: TextView
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        loginButton = view.findViewById(R.id.create_account_btn)

        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            // User is already logged in, fetch details and go to ApplicationScreen
            val userId = auth.currentUser?.uid
            userId?.let { fetchDetails(it) }
        }

        // Handle button clicks
        loginButton.setOnClickListener {
            loginUser()
        }

        signup = view.findViewById(R.id.sign_me_up)
        signup.setOnClickListener {
            // Navigate to SignupFragment
            val signupFragment = SignupFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, signupFragment) // Replace with your container ID
                .addToBackStack(null) // Add to back stack if you want to navigate back
                .commit()
        }

        return view
    }

    // Login user with email and password
    private fun loginUser() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        // Check if the email and password fields are not empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Sign in the user with email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        fetchDetails(it) // Fetch details now
                    }
                    Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchDetails(uid: String) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("estory").child("users").child(uid).child("profile").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("user_name").getValue(String::class.java)
                    val phone = snapshot.child("user_phno").getValue(String::class.java)
                    val nickname = snapshot.child("user_nick_name").getValue(String::class.java)
                    val language = snapshot.child("user_language").getValue(String::class.java)
                    val email = auth.currentUser?.email // Assuming you want to store the email from FirebaseAuth

                    // Store data in UserData object
                    UserData.name = name
                    UserData.phoneNumber = phone
                    UserData.nickname = nickname
                    UserData.email = email
                    UserData.language = language

                    // Redirect to ApplicationScreen
                    val intent = Intent(activity, ApplicationScreen::class.java)
                    startActivity(intent)
                    activity?.finish() // Close LoginFragment
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
    }
}
