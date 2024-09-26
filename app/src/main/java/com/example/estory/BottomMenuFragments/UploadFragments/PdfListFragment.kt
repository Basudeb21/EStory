package com.example.estory

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.estory.Adapters.PdfAdapter
import com.example.estory.R
import com.example.estory.UserDetails.AuthUser
import java.io.File
import java.io.FileOutputStream

class PdfListFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_PDF_PICKER = 100
    }

    private val pdfUris = mutableListOf<Uri>() // Store selected PDF Uris
    private lateinit var recyclerView: RecyclerView
    private lateinit var pdfAdapter: PdfAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pdf_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)

        pdfUris.addAll(loadPdfFiles()) // Load your PDF files into the pdfUris list
        pdfAdapter = PdfAdapter(pdfUris) { pdfUri ->
            // Handle PDF click (open the PDF)
            openPDFViewer(pdfUri)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = pdfAdapter

        return view
    }

    private fun loadPdfFiles(): List<Uri> {
        val pdfFiles = mutableListOf<Uri>() // Change the type to List<Uri>
        val userId = AuthUser().getCurrentUser()?.uid ?: return pdfFiles // Get user ID
        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val pdfDirectory = File(directoryPath)

        if (pdfDirectory.exists() && pdfDirectory.isDirectory) {
            val files = pdfDirectory.listFiles { _, name ->
                name.endsWith(".pdf") && name.contains(userId) // Filter files with user ID
            }
            files?.forEach { file ->
                pdfFiles.add(Uri.fromFile(file)) // Add the Uri of the file
            }
        }
        return pdfFiles
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PDF_PICKER && resultCode == Activity.RESULT_OK) {
            data?.data?.let { pdfUri ->
                pdfUris.add(pdfUri)
                pdfAdapter.notifyDataSetChanged()
                openPDFViewer(pdfUri)
            } ?: run {
                Toast.makeText(requireContext(), "No PDF selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openPDFViewer(pdfUri: Uri) {
        val tempFile = File(requireContext().cacheDir, "estory_story.pdf")

        requireContext().contentResolver.openInputStream(pdfUri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        val fileUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", tempFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No PDF viewer app found", Toast.LENGTH_SHORT).show()
        }
    }
}
