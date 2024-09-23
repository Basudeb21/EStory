package com.example.estory.LoginFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.estory.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.estory.SideNavItems.Profile // Import your Profile fragment
import com.example.estory.SideNavItems.SubItems.EditProfile

class SignupFragment : Fragment() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var login: TextView
    private lateinit var actionButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        actionButton = view.findViewById(R.id.create_account_btn)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Handle button clicks
        actionButton.setOnClickListener {
            createAccount()
        }

        login = view.findViewById(R.id.log_me_in)
        login.setOnClickListener {
            // Navigate to LoginFragment
            val loginFragment = LoginFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, loginFragment) // Replace with your container ID
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    // Create account with email and password
    private fun createAccount() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        // Check if the email and password fields are not empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Account creation successful
                    val user = auth.currentUser
                    val userId = user?.uid

                    // Save user details to the database under estory/users
                    val userData = mapOf("email" to email)
                    database.child("estory").child("users").child(userId!!).setValue(userData)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Account created successfully", Toast.LENGTH_SHORT).show()
                            // Redirect to ProfileFragment after sign-up
                            val profileFragment = EditProfile()
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, profileFragment) // Replace with your container ID
                                .commit()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Account creation failed
                    Toast.makeText(activity, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
