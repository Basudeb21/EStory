package com.example.estory.SideNavItems.SubItems

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.example.estory.Activities.ApplicationScreen // Import the ApplicationScreen
import com.example.estory.R

class EditProfile : Fragment() {

    private lateinit var languageSpinner: Spinner
    private lateinit var submit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Find views
        languageSpinner = view.findViewById(R.id.language_spinner)
        submit = view.findViewById(R.id.submit_button) // Ensure this matches your submit button ID

        // Set up the spinner with language options
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_options, // Ensure you have this array in your strings.xml
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Handle submit button click
        submit.setOnClickListener {
            // Navigate to ApplicationScreen activity
            val intent = Intent(activity, ApplicationScreen::class.java)
            startActivity(intent)
        }

        return view
    }
}
