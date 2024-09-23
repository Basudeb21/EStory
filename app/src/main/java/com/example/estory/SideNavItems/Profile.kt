package com.example.estory.SideNavItems

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.estory.R
import androidx.fragment.app.FragmentTransaction // Import FragmentTransaction
import com.example.estory.SideNavItems.SubItems.EditProfile

class Profile : Fragment() {

    private lateinit var editProfile: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize the ImageButton
        editProfile = view.findViewById(R.id.edit_profile) // Use the correct ID from your XML

        // Set the click listener to navigate to EditUser fragment
        editProfile.setOnClickListener {
            // Create an instance of the EditUser fragment
            val editUserFragment = EditProfile()

            // Replace the current fragment with EditUser fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, editUserFragment) // R.id.fragment_container is the ID of your container layout
                .addToBackStack(null) // Optionally add to backstack so the user can navigate back
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN) // Optional animation
                .commit()
        }

        return view
    }
}
