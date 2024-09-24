package com.example.estory.SideNavItems.SubItems

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.estory.Activities.ApplicationScreen // Import the ApplicationScreen
import com.example.estory.R
import com.example.estory.UserDetails.AuthUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfile : Fragment() {

    private lateinit var languageSpinner: Spinner
    private lateinit var submit: Button
    private lateinit var u_name: EditText
    private lateinit var u_nname: EditText
    private lateinit var phno: EditText
    private lateinit var database: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Find views
        languageSpinner = view.findViewById(R.id.language_spinner)
        submit = view.findViewById(R.id.submit_button)
        u_name = view.findViewById(R.id.name_input)
        u_nname = view.findViewById(R.id.nick_name)
        phno = view.findViewById(R.id.phone_input)
        database = FirebaseDatabase.getInstance().reference

        // Set up the spinner with language options
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        submit.setOnClickListener {
            addProfileDetails()
        }

        return view
    }

    private fun addProfileDetails(){
        val name = u_name.text.toString().trim()
        val n_name = u_nname.text.toString().trim()
        val phone = phno.text.toString().trim()
        val lang = languageSpinner.selectedItem.toString()

        if (name.isEmpty() || n_name.isEmpty() || phone.isEmpty() || lang.isEmpty()){
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userProfile = mapOf(
            "user_name" to name,
            "user_nick_name" to n_name,
            "user_phno" to phone,
            "user_language" to lang,
        )

        val authUser = AuthUser()
        val userId = authUser.getCurrentUser()?.uid

        userId?.let {
            database.child("estory").child("users").child(it).child("profile").setValue(userProfile)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, ApplicationScreen::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }

        }

    }
}
