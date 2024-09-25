package com.example.estory.BottomMenuFragments

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
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
import com.example.estory.BottomMenuFragments.UploadFragments.UploadMenu
import com.example.estory.R
import com.example.estory.UserDetails.AuthUser
import com.example.estory.UserDetails.UserData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class Upload : Fragment() {

    private lateinit var noteTitle: EditText
    private lateinit var noteDesc: EditText
    private lateinit var copyButton: ImageButton
    private lateinit var pdfButton: ImageButton
    private lateinit var folder: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_upload, container, false)

        // Request necessary permissions
        requestPermissions()

        // Initialize views
        noteTitle = view.findViewById(R.id.note_title)
        noteDesc = view.findViewById(R.id.note_desc)
        copyButton = view.findViewById(R.id.copy)
        pdfButton = view.findViewById(R.id.make_pdf)
        folder = view.findViewById(R.id.open_folder)


        // Set onClickListeners for the buttons
        copyButton.setOnClickListener {
            val titleText = noteTitle.text.toString()
            val descText = noteDesc.text.toString()
            if (titleText.isEmpty() || descText.isEmpty()){
                Toast.makeText(requireContext(), "Filed not be empty", Toast.LENGTH_SHORT).show()
            }
            else{
                addToClipboard(titleText, descText)
            }
        }

        pdfButton.setOnClickListener {
            val titleText = noteTitle.text.toString()
            val descText = noteDesc.text.toString()
            if (titleText.isEmpty() || descText.isEmpty()){
                Toast.makeText(requireContext(), "Filed not be empty", Toast.LENGTH_SHORT).show()
            }
            else{
                createPDF(titleText, descText)
            }

        }

        folder.setOnClickListener {
            openFolder()
        }
        return view
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    private fun addToClipboard(titleText: String, descText: String) {
        val combinedText = "Story Name: $titleText\n\nWritten By: ${UserData.nickname}\n\nStory: $descText"
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", combinedText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Story copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun openFolder(){
        val uploadFilesFragment = UploadMenu()
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, uploadFilesFragment) // Assuming your container ID is fragment_container
            .addToBackStack(null) // Optional: allows user to go back to the previous fragment
            .commit()

    }

    private fun createPDF(titleText: String, descText: String) {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842

        // Define margins and line height
        val leftMargin = 50f
        val rightMargin = 50f
        val topMargin = 80f
        val bottomMargin = 50f
        val lineHeight = 20f
        val rightPadding = 10f

        // Define paint styles
        val titlePaint = android.graphics.Paint().apply {
            textSize = 16f
            color = android.graphics.Color.BLUE
            isUnderlineText = true
            typeface = android.graphics.Typeface.create(typeface, android.graphics.Typeface.BOLD)
        }

        val authorPaint = android.graphics.Paint().apply {
            textSize = 14f
            color = android.graphics.Color.BLUE
            typeface = android.graphics.Typeface.create(typeface, android.graphics.Typeface.BOLD) // Set to bold, no underline
        }

        val contentPaint = android.graphics.Paint().apply {
            textSize = 12f
            color = android.graphics.Color.BLACK
        }

        val borderPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 2f
            style = android.graphics.Paint.Style.STROKE
        }

        // Fetch current user's UID
        val currentUser = AuthUser().getCurrentUser()
        val u_id = currentUser?.uid ?: "UnknownUID"

        // Define file path and name
        val fileName = "${sanitizeFileName(titleText)}_${UserData.nickname}_${u_id}.pdf"
        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val file = File(directoryPath, fileName)

        // Load watermark bitmap and resize it to 500x500 pixels
        val watermarkBitmap = BitmapFactory.decodeResource(resources, R.drawable.itstack)
        val scaledWatermarkBitmap = Bitmap.createScaledBitmap(watermarkBitmap, 500, 500, true)

        val watermarkPaint = android.graphics.Paint().apply {
            alpha = 80 // Set alpha value (0-255) for transparency (lower value = more transparent)
        }

        var currentPageNumber = 1
        var yPos: Float

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        canvas.drawRect(leftMargin, topMargin - 20f, pageWidth - rightMargin, pageHeight - bottomMargin, borderPaint)

        val watermarkX = (pageWidth - scaledWatermarkBitmap.width) / 2
        val watermarkY = (pageHeight - scaledWatermarkBitmap.height) / 2

        canvas.drawBitmap(scaledWatermarkBitmap, watermarkX.toFloat(), watermarkY.toFloat(), watermarkPaint)

        val title = "Story Name: $titleText"
        canvas.drawText(title, (pageWidth - titlePaint.measureText(title)) / 2, topMargin, titlePaint)
        yPos = topMargin + lineHeight * 2

        val author = "~ By: ${UserData.nickname}"
        canvas.drawText(author, (pageWidth - authorPaint.measureText(author)) / 2, yPos, authorPaint)
        yPos += lineHeight

        val words = descText.split(" ")
        var currentLine = StringBuilder()
        var hasContentOnPage = false

        for (word in words) {
            val testLine = currentLine.toString() + " " + word
            val testLineWidth = contentPaint.measureText(testLine)

            if (testLineWidth > (pageWidth - leftMargin - rightMargin - rightPadding)) {
                if (currentLine.isNotEmpty()) {
                    canvas.drawText(currentLine.toString(), leftMargin + 10, yPos, contentPaint)
                    yPos += lineHeight
                    hasContentOnPage = true
                }

                if (yPos + lineHeight > pageHeight - bottomMargin) {
                    drawPageNumber(canvas, currentPageNumber, pageWidth, bottomMargin, pageHeight)
                    pdfDocument.finishPage(page)

                    currentPageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    canvas.drawRect(leftMargin, topMargin - 20f, pageWidth - rightMargin, pageHeight - bottomMargin, borderPaint)

                    canvas.drawBitmap(scaledWatermarkBitmap, watermarkX.toFloat(), watermarkY.toFloat(), watermarkPaint)

                    yPos = topMargin // Reset Y position for the new page
                }

                currentLine = StringBuilder(word) // Start new line with the current word
            } else {
                currentLine.append(" ").append(word)
            }
        }

        if (currentLine.isNotEmpty()) {
            canvas.drawText(currentLine.toString(), leftMargin + 10, yPos, contentPaint)
            yPos += lineHeight
            hasContentOnPage = true
        }

        if (hasContentOnPage) {
            drawPageNumber(canvas, currentPageNumber, pageWidth, bottomMargin, pageHeight)
            pdfDocument.finishPage(page)
        } else {
            pdfDocument.close() // Close document if no content was drawn
            Toast.makeText(requireContext(), "No content to save in PDF", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(requireContext(), "PDF saved at: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error saving PDF", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close() // Close the document
        }
    }

    private fun drawPageNumber(canvas: android.graphics.Canvas, currentPage: Int, pageWidth: Int, bottomMargin: Float, pageHeight: Int) {
        val pageNumberText = "Page | $currentPage"
        val pageNumberPaint = android.graphics.Paint().apply {
            textSize = 12f
            color = android.graphics.Color.GRAY
        }

        val textWidth = pageNumberPaint.measureText(pageNumberText)
        val xPosition = pageWidth - 100f
        val yPosition = pageHeight - bottomMargin + 20f
        canvas.drawText(pageNumberText, xPosition, yPosition, pageNumberPaint)
    }

    private fun sanitizeFileName(fileName: String): String {
        return fileName.replace("[^a-zA-Z0-9_\\-\\.\\s]".toRegex(), "_")
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}
