package com.example.estory.BottomMenuFragments.UploadFragments

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.estory.R
import java.io.File

class StoryOutput : Fragment() {

    private lateinit var noteTitle: TextView
    private lateinit var noteDesc: TextView
    private lateinit var open_folder: ImageButton



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_story_output, container, false)

        noteTitle = view.findViewById(R.id.note_title)
        noteDesc = view.findViewById(R.id.note_desc)

        val fileName = arguments?.getString("FILE_NAME") // Retrieve the file name passed
        loadNoteFromFile(fileName) // Load the content of the selected file

        open_folder.setOnClickListener {
            openFolder()
        }

        return view
    }

    private fun loadNoteFromFile(fileName: String?) {
        fileName?.let {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), it)
            if (file.exists()) {
                try {
                    val content = file.readText()
                    // Log the content for debugging
                    Log.d("StoryOutput", "Content of $fileName: $content")

                    // Set the first TextView to display the file name
                    noteTitle.text = it // Display the file name

                    // Set the second TextView to display the content of the file
                    noteDesc.text = if (content.isNotBlank()) content else "No content available."
                } catch (e: Exception) {
                    Log.e("StoryOutput", "Error reading file: ${e.message}")
                    noteTitle.text = "Error loading file"
                    noteDesc.text = ""
                }
            } else {
                noteTitle.text = "File not found"
                noteDesc.text = ""
            }
        }
    }
    private fun openFolder(){
        val uploadFilesFragment = UploadMenu()
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, uploadFilesFragment)
            .addToBackStack(null)
            .commit()
    }
}
