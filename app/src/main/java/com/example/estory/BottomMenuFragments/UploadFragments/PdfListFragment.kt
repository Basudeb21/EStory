package com.example.estory

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PdfListFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_PDF_PICKER = 100
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var pdfAdapter: PdfAdapter
    private val pdfUris = mutableListOf<Uri>() // Store selected PDF Uris

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pdf_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)

        // Initialize the adapter with the list of PDF Uris
        pdfAdapter = PdfAdapter(pdfUris) { pdfUri ->
            openPDFViewer(pdfUri) // Use the openPDFViewer method
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = pdfAdapter

        // Allow user to select PDF using an intent
        selectPdf()

        return view
    }

    private fun selectPdf() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, REQUEST_CODE_PDF_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PDF_PICKER && resultCode == Activity.RESULT_OK) {
            data?.data?.let { pdfUri ->
                // Add the selected Uri to the list and notify the adapter
                pdfUris.add(pdfUri)
                pdfAdapter.notifyDataSetChanged() // Update the RecyclerView
                openPDFViewer(pdfUri) // Open the PDF viewer directly with the Uri
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
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) // Optional: prevents showing in the app history
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No PDF viewer app found", Toast.LENGTH_SHORT).show()
        }
    }
}
