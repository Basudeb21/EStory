package com.example.estory.BottomMenuFragments.UploadFragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.estory.R
import java.io.File

class LocalTextFilesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TextFileAdapter
    private var textFiles: List<String> = listOf() // This should be your list of text files

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_local_text_files, container, false)

        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter with a click listener
        adapter = TextFileAdapter(textFiles) { fileName ->
            openStoryOutput(fileName) // Open the StoryOutput fragment
        }
        recyclerView.adapter = adapter

        // Load text files after initializing the adapter
        loadTextFiles()

        return view
    }

    private fun loadTextFiles() {
        // Define the directory path
        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val directory = File(directoryPath)

        // Check if the directory exists
        if (directory.exists()) {
            // Get a list of text files in the directory
            val files = directory.listFiles { file -> file.isFile && file.name.endsWith(".txt") }
            if (files != null && files.isNotEmpty()) {
                textFiles = files.map { it.name } // Map to a list of file names
            } else {
                textFiles = listOf() // No files found
            }
        } else {
            Toast.makeText(requireContext(), "Directory does not exist", Toast.LENGTH_SHORT).show()
            textFiles = listOf() // Set to empty list if directory doesn't exist
        }

        // Update the adapter with the loaded files
        adapter.updateFiles(textFiles)
    }

    private fun openStoryOutput(fileName: String) {
        // Create a new instance of StoryOutput and pass the file name
        val bundle = Bundle().apply {
            putString("FILE_NAME", fileName)
        }
        val storyOutputFragment = StoryOutput().apply {
            arguments = bundle
        }

        // Navigate to the StoryOutput fragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, storyOutputFragment)
            .addToBackStack(null)
            .commit()
    }
}
