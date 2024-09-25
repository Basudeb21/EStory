package com.example.estory.BottomMenuFragments.UploadFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentTransaction
import com.example.estory.PdfListFragment
import com.example.estory.R

class UploadMenu : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_menu, container, false)

        // Find the LinearLayout for local_pdf
        val localPdfButton: LinearLayout = view.findViewById(R.id.local_pdf)

        // Set an OnClickListener to navigate to PdfListFragment
        localPdfButton.setOnClickListener {
            // Use the supportFragmentManager to begin a transaction
            val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout, PdfListFragment())
            fragmentTransaction.addToBackStack(null) // Optional: add to back stack for navigation
            fragmentTransaction.commit() // Commit the transaction
        }

        return view
    }
}
