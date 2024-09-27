package com.example.estory.BottomMenuFragments.UploadFragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.estory.R
import java.io.File

class StoryOutput : Fragment() {

    private lateinit var noteTitle: TextView
    private lateinit var noteDesc: TextView
    private lateinit var openFolder: ImageButton
    private lateinit var updateStory: ImageButton
    private val featuresHandler = FeaturesHandler()
    private lateinit var copyButton:ImageButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_story_output, container, false)

        noteTitle = view.findViewById(R.id.note_title)
        noteDesc = view.findViewById(R.id.note_desc)
        openFolder = view.findViewById(R.id.open_folder)
        updateStory = view.findViewById(R.id.update_story)
        copyButton =  view.findViewById(R.id.copy_story)

        openFolder.setOnClickListener {
            featuresHandler.openFolder(this)
        }
        updateStory.setOnClickListener {
            setupUpdateButton()
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

        val fileName = arguments?.getString("FILE_NAME")
        loadNoteFromFile(fileName)

        return view
    }
    private fun setupUpdateButton() {

            val title = noteTitle.text.toString()
            val content = noteDesc.text.toString()

            // Navigate to UpdateTxt Fragment
            val updateFragment = UpdateTxt().apply {
                arguments = Bundle().apply {
                    putString("FILE_NAME", arguments?.getString("FILE_NAME"))
                    putString("NOTE_TITLE", title)
                    putString("NOTE_DESC", content)
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, updateFragment)
                .addToBackStack(null)
                .commit()
    }


    private fun loadNoteFromFile(fileName: String?) {
        fileName?.let {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), it)
            if (file.exists()) {
                val content = file.readText()
                // Display the file name in noteTitle and content in noteDesc
                noteTitle.text = it
                noteDesc.text = if (content.isNotBlank()) content else "No content available."
            } else {
                noteTitle.text = "File not found"
                noteDesc.text = ""
            }
        }
    }
}
