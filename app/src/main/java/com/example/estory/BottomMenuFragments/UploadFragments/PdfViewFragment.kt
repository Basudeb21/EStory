package com.example.estory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment

class PdfViewFragment : Fragment() {

    companion object {
        const val KEY_URL = "key_url"
        private const val PDF_VIEWER_URL = "https://docs.google.com/gview?embedded=true&url="

        fun newInstance(url: String): PdfViewFragment {
            val fragment = PdfViewFragment()
            val args = Bundle()
            args.putString(KEY_URL, url)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private var url: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pdf_view, container, false)
        webView = view.findViewById(R.id.webView)
        progressBar = view.findViewById(R.id.progressBar)

        // Enable JavaScript and DOM storage for the WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                progressBar.visibility = View.VISIBLE  // Show the progress bar
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE  // Hide the progress bar
            }
        }

        // Fetch the URL from arguments
        url = arguments?.getString(KEY_URL)

        // Validate the URL
        if (url.isNullOrEmpty()) {
            Toast.makeText(context, "No URL provided!", Toast.LENGTH_SHORT).show()
            return view  // Return early or handle as necessary
        }

        // Load the PDF using Google Docs Viewer
        webView.loadUrl("$PDF_VIEWER_URL$url")

        return view
    }
}
