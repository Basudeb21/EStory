package com.example.estory.BottomMenuFragments.UploadFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.estory.R

class StoryOutputFirebase : Fragment() {

    private lateinit var noteTitle: TextView
    private lateinit var noteDesc: TextView
    private lateinit var openFolder: ImageButton
    private lateinit var updateStory: ImageButton
    private lateinit var copyButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_story_output, container, false)

        noteTitle = view.findViewById(R.id.note_title)
        noteDesc = view.findViewById(R.id.note_desc)
        openFolder = view.findViewById(R.id.open_folder)
        updateStory = view.findViewById(R.id.update_story)
        copyButton = view.findViewById(R.id.copy_story)

        // Retrieve the title and description from arguments
        val title = arguments?.getString("FILE_NAME") ?: "No Title"
        val description = arguments?.getString("NOTE_DESC") ?: "No Description"

        noteTitle.text = title
        noteDesc.text = description

        openFolder.setOnClickListener {
            // Handle opening folder logic here
        }

        updateStory.setOnClickListener {
            // Handle updating story logic here
        }

        copyButton.setOnClickListener {
            // Handle copying story logic here
        }

        return view
    }
}
