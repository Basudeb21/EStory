package com.example.estory.BottomMenuFragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.estory.BottomMenuFragments.UploadFragments.FeaturesHandler
import com.example.estory.R
import com.example.estory.UserDetails.UserData
import com.google.firebase.database.DatabaseReference

class Upload : Fragment() {

    private lateinit var noteTitle: EditText
    private lateinit var noteDesc: EditText
    private lateinit var copyButton: ImageButton
    private lateinit var pdfButton: ImageButton
    private lateinit var folder: ImageButton
    private lateinit var saveLocalButton: ImageButton
    private lateinit var saveDbButton: ImageButton
    private lateinit var database: DatabaseReference

    private val featuresHandler = FeaturesHandler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload, container, false)
        requestPermissions()

        noteTitle = view.findViewById(R.id.note_title)
        noteDesc = view.findViewById(R.id.note_desc)
        copyButton = view.findViewById(R.id.copy)
        pdfButton = view.findViewById(R.id.make_pdf)
        folder = view.findViewById(R.id.open_folder)
        saveLocalButton = view.findViewById(R.id.save_local)
        saveDbButton = view.findViewById(R.id.save_db)


        copyButton.setOnClickListener {
            val titleText = noteTitle.text.toString()
            val descText = noteDesc.text.toString()
            if (titleText.isEmpty() || descText.isEmpty()) {
                Toast.makeText(requireContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                featuresHandler.addToClipboard(requireContext(), titleText, descText)
            }
        }

        pdfButton.setOnClickListener {
            val titleText = noteTitle.text.toString()
            val descText = noteDesc.text.toString()
            if (titleText.isEmpty() || descText.isEmpty()) {
                Toast.makeText(requireContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                featuresHandler.createPDF(requireContext(), titleText, descText)
            }
        }

        folder.setOnClickListener {
            featuresHandler.openFolder(this)
        }

        saveLocalButton.setOnClickListener {
            val titleText = noteTitle.text.toString()
            val descText = noteDesc.text.toString()
            if (titleText.isEmpty() || descText.isEmpty()) {
                Toast.makeText(requireContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                featuresHandler.saveTextFile(requireContext(), titleText, descText)
            }
        }

        saveDbButton.setOnClickListener {
            val titleText = noteTitle.text.toString()
            val descText = noteDesc.text.toString()

            if (titleText.isEmpty() || descText.isEmpty()) {
                Toast.makeText(requireContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                featuresHandler.saveStoryToFirebase(requireContext(), titleText, descText) { success ->
                    if (success) {
                        // Optional: Handle successful save (e.g., reset fields or show success message)
                        noteTitle.text.clear()
                        noteDesc.text.clear()
                    } else {
                        // Optional: Handle failure case
                        Toast.makeText(requireContext(), "Failed to save story to Firebase", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}
