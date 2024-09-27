package com.example.estory.BottomMenuFragments.UploadFragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.estory.R
import java.io.File
import java.io.FileWriter

class UpdateTxt : Fragment() {

    private lateinit var noteTitle: TextView
    private lateinit var noteDesc: EditText
    private lateinit var saveButton: ImageButton
    private lateinit var folder: ImageButton
    private lateinit var copyButton:ImageButton
    private val featuresHandler = FeaturesHandler()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_txt, container, false)

        noteTitle = view.findViewById(R.id.note_title)
        noteDesc = view.findViewById(R.id.note_desc)
        saveButton = view.findViewById(R.id.save_story)
        copyButton =  view.findViewById(R.id.copy_story)
        folder = view.findViewById(R.id.open_folder)



        // Check permissions
        checkPermissions()

        // Get the title and description from the arguments
        noteTitle.text = arguments?.getString("NOTE_TITLE")?.let { it.toEditable() }
        noteDesc.text = arguments?.getString("NOTE_DESC")?.let { it.toEditable() }

        saveButton.setOnClickListener {
            saveUpdatedNote()
        }
        folder.setOnClickListener {
            featuresHandler.openFolder(this)
        }
        copyButton.setOnClickListener {
            val titleText = noteTitle.text.toString()
            val descText = noteDesc.text.toString()
            if (titleText.isEmpty() || descText.isEmpty()) {
                Toast.makeText(requireContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                featuresHandler.addToClipboard(requireContext(), titleText, descText)
            }
        }

        return view
    }

    private fun saveUpdatedNote() {
        val title = noteTitle.text.toString()
        val content = noteDesc.text.toString()
        val fileName = arguments?.getString("FILE_NAME")

        title?.let {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), it)

            if (file.exists()) {
                FileWriter(file).use { writer ->
                    writer.write(content) // Write updated content
                }

                // Show a toast message after saving
                Toast.makeText(requireContext(), "Story updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "File not found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }
}

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
