package com.example.estory.BottomMenuFragments.UploadFragments

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.estory.R
import com.example.estory.UserDetails.AuthUser
import com.example.estory.UserDetails.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FeaturesHandler {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("estory")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun openFolder(fragment: Fragment) {
        val uploadFilesFragment = UploadMenu()
        fragment.parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, uploadFilesFragment) // Assuming frame_layout is the container ID
            .addToBackStack(null)
            .commit()
    }

    fun addToClipboard(context: Context, titleText: String, descText: String) {
        val combinedText = "Story Name: $titleText\n\nWritten By: ${UserData.nickname}\n\nStory: $descText"
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", combinedText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Story copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun saveTextFile(context: Context, titleText: String, descText: String) {
        // Define the file name and file path
        val fileName = "${sanitizeFileName(titleText)}_${UserData.nickname}.txt"
        val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val file = File(directoryPath, fileName)

        // Check if file already exists
        if (file.exists()) {
            Toast.makeText(context, "File already exists: $fileName", Toast.LENGTH_LONG).show()
            return
        }

        try {
            // Write the text to the file
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(descText.toByteArray())
            fileOutputStream.close()

            Toast.makeText(context, "Text saved locally as: $fileName", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving text file", Toast.LENGTH_LONG).show()
        }
    }

    fun createPDF(context: Context, titleText: String, descText: String) {
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
            typeface = android.graphics.Typeface.create(typeface, android.graphics.Typeface.BOLD)
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

        // Load watermark bitmap and resize it
        val watermarkBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.itstack)
        val scaledWatermarkBitmap = Bitmap.createScaledBitmap(watermarkBitmap, 500, 500, true)

        val watermarkPaint = android.graphics.Paint().apply {
            alpha = 80 // Set alpha value (0-255) for transparency
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
            Toast.makeText(context, "No content to save in PDF", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF saved at: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving PDF", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close() // Close the document
        }
    }

    private fun drawPageNumber(canvas: android.graphics.Canvas, currentPage: Int, pageWidth: Int, bottomMargin: Float, pageHeight: Int) {
        val pageNumber = "Page $currentPage"
        canvas.drawText(pageNumber, pageWidth - 100f, pageHeight - bottomMargin / 2, android.graphics.Paint().apply {
            textSize = 12f
            color = android.graphics.Color.BLACK
        })
    }

    private fun sanitizeFileName(fileName: String): String {
        return fileName.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
    }


    fun saveStoryToFirebase(context: Context, title: String, description: String, onComplete: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid

            // Reference to the user's unposted stories
            val storiesRef = database.child("users").child(uid).child("unposted_stories")

            // Query to check if the story with the same title already exists
            val query = storiesRef.orderByChild("title").equalTo(title)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Check if any matching story is found
                    if (snapshot.exists()) {
                        // Story with the same title found, show a message to the user
                        Toast.makeText(context, "A story with the same title already exists!", Toast.LENGTH_LONG).show()
                        onComplete(false)
                    } else {
                        // No matching story found, save the new story
                        val storyData = mapOf(
                            "title" to title,
                            "description" to description
                        )

                        storiesRef.push().setValue(storyData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Story saved to Firebase", Toast.LENGTH_SHORT).show()
                                onComplete(true)
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to save story", Toast.LENGTH_SHORT).show()
                                onComplete(false)
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error checking for existing story: ${error.message}", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
            })
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            onComplete(false)
        }
    }


    // Retrieve the list of unposted stories from Firebase for the current user
    fun getUnpostedStories(onDataReceived: (List<Map<String, String>>) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val storiesRef = database.child("users").child(uid).child("unposted_stories")

            storiesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val storyList = mutableListOf<Map<String, String>>()
                    for (storySnapshot in snapshot.children) {
                        val storyMap = storySnapshot.value as Map<String, String>
                        storyList.add(storyMap)
                    }
                    onDataReceived(storyList)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    // Delete a specific story by its key
    fun deleteUnpostedStory(storyKey: String, onComplete: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val storyRef = database.child("users").child(uid).child("unposted_stories").child(storyKey)

            storyRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
        } else {
            onComplete(false)
        }
    }

    // Update a story's title and description
    fun updateUnpostedStory(storyKey: String, newTitle: String, newDescription: String, onComplete: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val storyRef = database.child("users").child(uid).child("unposted_stories").child(storyKey)

            val updatedData = mapOf(
                "title" to newTitle,
                "description" to newDescription
            )

            storyRef.updateChildren(updatedData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
        } else {
            onComplete(false)
        }
    }

}
