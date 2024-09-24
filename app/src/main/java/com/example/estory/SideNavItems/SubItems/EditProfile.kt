package com.example.estory.SideNavItems.SubItems

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
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
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.estory.Activities.ApplicationScreen
import com.example.estory.R
import com.example.estory.UserDetails.AuthUser
import com.example.estory.UserDetails.UserData // Import UserData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class EditProfile : Fragment() {

    private lateinit var languageSpinner: Spinner
    private lateinit var submit: Button
    private lateinit var u_name: EditText
    private lateinit var u_nname: EditText
    private lateinit var phno: EditText
    private lateinit var profileImage: CircleImageView
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var takePictureFromGallery: FloatingActionButton
    private lateinit var loadingDialog: AlertDialog
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Initialize Firebase database and storage references
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference
        databaseRef = FirebaseDatabase.getInstance().getReference("estory/users/${FirebaseAuth.getInstance().currentUser?.uid}/profile/user_profile_pic")

        // Find views
        languageSpinner = view.findViewById(R.id.language_spinner)
        submit = view.findViewById(R.id.submit_button)
        u_name = view.findViewById(R.id.name_input)
        u_nname = view.findViewById(R.id.nick_name)
        phno = view.findViewById(R.id.phone_input)
        profileImage = view.findViewById(R.id.profile_image)
        takePictureFromGallery = view.findViewById(R.id.edit_profile_image)
        loadProfilePicture()

        // Set up the spinner with language options
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Click listener to select an image from the gallery
        takePictureFromGallery.setOnClickListener {
            selectImageFromGallery()
        }

        submit.setOnClickListener {
            addProfileDetails()
        }

        return view
    }

    private fun loadProfilePicture() {
        databaseRef.get().addOnSuccessListener { snapshot ->
            val imageUrl = snapshot.value.toString() // Get the URL as a String

            // Check if the Fragment is still attached
            if (isAdded) {
                // If imageUrl is empty, use the default avatar drawable
                if (imageUrl.isEmpty()) {
                    profileImage.setImageResource(R.drawable.avatar) // Replace with your drawable resource
                } else {
                    Glide.with(this) // Using the Fragment context
                        .load(imageUrl)
                        .into(profileImage)
                }
            }
        }.addOnFailureListener { exception ->
            // Handle the error if necessary
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == -1) {
                val data = result.data
                imageUri = data?.data ?: Uri.EMPTY
                profileImage.setImageURI(imageUri)
                Toast.makeText(requireContext(), "Image Selected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun addProfileDetails() {
        val name = u_name.text.toString().trim()
        val n_name = u_nname.text.toString().trim()
        val phone = phno.text.toString().trim()
        val lang = languageSpinner.selectedItem.toString()

        if (name.isEmpty() || n_name.isEmpty() || phone.isEmpty() || lang.isEmpty()) {
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

        userId?.let { id ->
            showLoadingDialog() // Show loading dialog before starting upload
            database.child("estory").child("users").child(id).child("profile").setValue(userProfile)
                .addOnSuccessListener {
                    updateUserData(name, n_name, phone, lang) // Update UserData after profile details added

                    // Check if an image has been selected; if not, fetch the old image URL
                    if (this::imageUri.isInitialized && imageUri != Uri.EMPTY) {
                        uploadProfilePic(id)
                    } else {
                        // No new image selected, get the old image URL and update UserData
                        loadOldProfileImageUrl(id)
                    }
                }
                .addOnFailureListener {
                    hideLoadingDialog() // Hide loading dialog on failure
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateUserData(name: String, n_name: String, phone: String, lang: String) {
        UserData.name = name
        UserData.nickname = n_name
        UserData.phoneNumber = phone
        UserData.language = lang
        // Optionally, you can also update other fields in UserData if necessary
    }

    private fun loadOldProfileImageUrl(userId: String) {
        database.child("estory").child("users").child(userId).child("profile").child("user_profile_pic").get()
            .addOnSuccessListener { snapshot ->
                val oldImageUrl = snapshot.value.toString()
                UserData.profileImageUrl = oldImageUrl // Update UserData with old image URL
                hideLoadingDialog() // Hide loading dialog
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, ApplicationScreen::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                hideLoadingDialog() // Hide loading dialog on failure
                Toast.makeText(requireContext(), "Failed to retrieve old profile picture", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadProfilePic(userId: String) {
        val profilePicRef = storage.child("estory/users/$userId/profile/user_profile_pic.jpg")

        profilePicRef.putFile(imageUri)
            .addOnSuccessListener {
                profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                    database.child("estory").child("users").child(userId).child("profile")
                        .child("user_profile_pic").setValue(uri.toString())
                        .addOnSuccessListener {
                            UserData.profileImageUrl = uri.toString() // Update UserData with new image URL
                            hideLoadingDialog() // Hide loading dialog after successful upload
                            Toast.makeText(requireContext(), "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, ApplicationScreen::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            hideLoadingDialog() // Hide loading dialog on failure
                            Toast.makeText(requireContext(), "Failed to upload profile picture", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                hideLoadingDialog() // Hide loading dialog on failure
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoadingDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)
        builder.setView(dialogView)
        builder.setCancelable(false) // Prevent dismissing the dialog on touch outside
        loadingDialog = builder.create()
        loadingDialog.show()
    }

    private fun hideLoadingDialog() {
        if (this::loadingDialog.isInitialized && loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }
}
