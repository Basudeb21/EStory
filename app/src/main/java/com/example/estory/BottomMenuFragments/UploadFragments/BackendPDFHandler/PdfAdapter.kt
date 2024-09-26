package com.example.estory.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.estory.R

class PdfAdapter(private val pdfUris: List<Uri>, private val onItemClick: (Uri) -> Unit) :
    RecyclerView.Adapter<PdfAdapter.PdfViewHolder>() {

    inner class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pdfName: TextView = itemView.findViewById(R.id.pdf_name)

        fun bind(uri: Uri) {
            pdfName.text = uri.lastPathSegment
            itemView.setOnClickListener { onItemClick(uri) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf, parent, false)
        return PdfViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.bind(pdfUris[position])
    }

    override fun getItemCount() = pdfUris.size
}
