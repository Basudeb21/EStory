package com.example.estory.SideNavItems

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.estory.R
import androidx.fragment.app.FragmentTransaction // Import FragmentTransaction
import com.bumptech.glide.Glide
import com.example.estory.SideNavItems.SubItems.EditProfile
import com.example.estory.UserDetails.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

class Profile : Fragment() {

    private lateinit var uname: TextView
    private lateinit var editProfile: ImageButton
    private lateinit var umail: TextView
    private lateinit var profilePic: CircleImageView // Declare CircleImageView
    private lateinit var databaseRef: DatabaseReference // Database reference



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        editProfile = view.findViewById(R.id.edit_profile)
        umail = view.findViewById(R.id.user_profile_mail)
        uname = view.findViewById(R.id.user_profile_name)
        profilePic = view.findViewById(R.id.profile_pic_profile) // Initialize CircleImageView


        editProfile.setOnClickListener {
            val editUserFragment = EditProfile()

            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, editUserFragment) // R.id.fragment_container is the ID of your container layout
                .addToBackStack(null) // Optionally add to backstack so the user can navigate back
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN) // Optional animation
                .commit()
        }

        val email = UserData.email
        val name = formatUserName(UserData.name.toString())
        umail.setText(email)
        uname.setText(name)

        // Initialize Firebase Database reference for profile picture
        databaseRef = FirebaseDatabase.getInstance().getReference("estory/users/${FirebaseAuth.getInstance().currentUser?.uid}/profile/user_profile_pic")

        // Load the profile picture
        loadProfilePicture()



        return view
    }

    private fun loadProfilePicture() {
        databaseRef.get().addOnSuccessListener { snapshot ->
            val imageUrl = snapshot.value.toString() // Get the URL as a String

            // Check if the Fragment is still attached
            if (isAdded) {
                Glide.with(this) // Using the Fragment context
                    .load(imageUrl)
                    .into(profilePic)
            }
        }.addOnFailureListener { exception ->
            // Handle the error if necessary
        }
    }
    private fun formatUserName(name: String): String {
        return if (name.length > 15) {
            if (name.length > 10) {
                name.substring(0, 10) + "..."
            } else {
                name
            }
        } else {
            name
        }
    }

}
