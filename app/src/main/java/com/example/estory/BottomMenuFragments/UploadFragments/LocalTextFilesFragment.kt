package com.example.estory.BottomMenuFragments.UploadFragments

import TextFileAdapter
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
    private var textFiles: List<String> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_local_text_files, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TextFileAdapter(textFiles, { fileName ->
            openStoryOutput(fileName)
        }, { fileName, action ->
            handleMenuAction(fileName, action)
        })
        recyclerView.adapter = adapter

        loadTextFiles()

        return view
    }

    private fun loadTextFiles() {

        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val directory = File(directoryPath)

        if (directory.exists()) {
            val files = directory.listFiles { file -> file.isFile && file.name.endsWith(".txt") }
            textFiles = files?.map { it.name } ?: listOf()
        } else {
            Toast.makeText(requireContext(), "Directory does not exist", Toast.LENGTH_SHORT).show()
            textFiles = listOf()
        }

        adapter.updateFiles(textFiles)
    }

    private fun openStoryOutput(fileName: String) {
        val bundle = Bundle().apply {
            putString("FILE_NAME", fileName)
        }
        val storyOutputFragment = StoryOutput().apply {
            arguments = bundle
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, storyOutputFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun handleMenuAction(fileName: String, action: String) {
        when (action) {
            "open" -> openStoryOutput(fileName)
            "update" -> updateFile(fileName)
            "delete" -> deleteFile(fileName)
        }
    }

    private fun updateFile(fileName: String) {
        // Implement your logic to update the file
        Toast.makeText(requireContext(), "Updating $fileName", Toast.LENGTH_SHORT).show()
    }

    private fun deleteFile(fileName: String) {
        // Implement your logic to delete the file
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName)
        if (file.exists() && file.delete()) {
            Toast.makeText(requireContext(), "$fileName deleted", Toast.LENGTH_SHORT).show()
            loadTextFiles() // Reload files after deletion
        } else {
            Toast.makeText(requireContext(), "Failed to delete $fileName", Toast.LENGTH_SHORT).show()
        }
    }
}