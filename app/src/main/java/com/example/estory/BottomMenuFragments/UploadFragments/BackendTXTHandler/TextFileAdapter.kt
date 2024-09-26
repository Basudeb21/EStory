package com.example.estory.BottomMenuFragments.UploadFragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.estory.R

class TextFileAdapter(private var files: List<String>, private val clickListener: (String) -> Unit) :
    RecyclerView.Adapter<TextFileAdapter.FileViewHolder>() {

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_text, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.textView.text = files[position]
        holder.itemView.setOnClickListener {
            clickListener(files[position]) // Handle item clicks
        }
    }

    override fun getItemCount() = files.size

    fun updateFiles(newFiles: List<String>) {
        files = newFiles
        notifyDataSetChanged() // Refresh the RecyclerView
    }
}
