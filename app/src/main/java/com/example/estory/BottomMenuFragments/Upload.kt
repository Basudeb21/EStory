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

        // Set onClickListeners for the buttons
        copyButton.setOnClickListener {
            val titleText = noteTitle.text.toString()
            val descText = noteDesc.text.toString()
            addToClipboard(titleText, descText)
        }

        pdfButton.setOnClickListener {
            val titleText = noteTitle.text.toString()
            val descText = noteDesc.text.toString()
            createPDF(titleText, descText)
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

        // Create a Paint object for the watermark with transparency
        val watermarkPaint = android.graphics.Paint().apply {
            alpha = 80 // Set alpha value (0-255) for transparency (lower value = more transparent)
        }

        // Variables for pagination
        var currentPageNumber = 1
        var yPos: Float

        // Create the first page
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Draw page border
        canvas.drawRect(leftMargin, topMargin - 20f, pageWidth - rightMargin, pageHeight - bottomMargin, borderPaint)

        // Center the watermark
        val watermarkX = (pageWidth - scaledWatermarkBitmap.width) / 2
        val watermarkY = (pageHeight - scaledWatermarkBitmap.height) / 2

        // Draw watermark with transparency
        canvas.drawBitmap(scaledWatermarkBitmap, watermarkX.toFloat(), watermarkY.toFloat(), watermarkPaint)

        // Draw the title
        val title = "Story Name: $titleText"
        canvas.drawText(title, (pageWidth - titlePaint.measureText(title)) / 2, topMargin, titlePaint)
        yPos = topMargin + lineHeight * 2

        // Draw author name
        val author = "~ By: ${UserData.nickname}"
        canvas.drawText(author, (pageWidth - authorPaint.measureText(author)) / 2, yPos, authorPaint)
        yPos += lineHeight

        // Wrap the content line to fit within the page width
        val words = descText.split(" ")
        var currentLine = StringBuilder()
        var hasContentOnPage = false

        for (word in words) {
            val testLine = currentLine.toString() + " " + word
            val testLineWidth = contentPaint.measureText(testLine)

            // If the line is too long, draw the current line and start a new one
            if (testLineWidth > (pageWidth - leftMargin - rightMargin - rightPadding)) {
                if (currentLine.isNotEmpty()) {
                    canvas.drawText(currentLine.toString(), leftMargin + 10, yPos, contentPaint)
                    yPos += lineHeight
                    hasContentOnPage = true
                }

                // Check if we need to create a new page
                if (yPos + lineHeight > pageHeight - bottomMargin) {
                    drawPageNumber(canvas, currentPageNumber, pageWidth, bottomMargin, pageHeight)
                    pdfDocument.finishPage(page)

                    // Create a new page
                    currentPageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    canvas.drawRect(leftMargin, topMargin - 20f, pageWidth - rightMargin, pageHeight - bottomMargin, borderPaint)

                    // Draw watermark with transparency
                    canvas.drawBitmap(scaledWatermarkBitmap, watermarkX.toFloat(), watermarkY.toFloat(), watermarkPaint)

                    yPos = topMargin // Reset Y position for the new page
                }

                currentLine = StringBuilder(word) // Start new line with the current word
            } else {
                currentLine.append(" ").append(word)
            }
        }

        // Draw any remaining text in the current line
        if (currentLine.isNotEmpty()) {
            canvas.drawText(currentLine.toString(), leftMargin + 10, yPos, contentPaint)
            yPos += lineHeight
            hasContentOnPage = true
        }

        // Finish the last page only if there was content drawn
        if (hasContentOnPage) {
            drawPageNumber(canvas, currentPageNumber, pageWidth, bottomMargin, pageHeight)
            pdfDocument.finishPage(page)
        } else {
            pdfDocument.close() // Close document if no content was drawn
            Toast.makeText(requireContext(), "No content to save in PDF", Toast.LENGTH_SHORT).show()
            return
        }

        // Write the document content to the file
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

    // Function to draw page number below the bottom border
    private fun drawPageNumber(canvas: android.graphics.Canvas, currentPage: Int, pageWidth: Int, bottomMargin: Float, pageHeight: Int) {
        val pageNumberText = "Page | $currentPage"
        val pageNumberPaint = android.graphics.Paint().apply {
            textSize = 12f
            color = android.graphics.Color.GRAY
        }

        // Calculate the width of the page number text
        val textWidth = pageNumberPaint.measureText(pageNumberText)

        // Adjust the X position to leave 100 pixels from the right margin
        val xPosition = pageWidth - 100f // 100 pixels from the right edge

        // Set the Y position below the bottom border
        val yPosition = pageHeight - bottomMargin + 20f // Move down by 20 pixels from the bottom margin

        canvas.drawText(pageNumberText, xPosition, yPosition, pageNumberPaint)
    }

    // Function to sanitize file name
    private fun sanitizeFileName(fileName: String): String {
        return fileName.replace("[^a-zA-Z0-9_\\-\\.\\s]".toRegex(), "_")
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}
