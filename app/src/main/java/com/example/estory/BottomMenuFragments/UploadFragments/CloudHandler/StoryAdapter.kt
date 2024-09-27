package com.example.estory.BottomMenuFragments.UploadFragments.CloudHandler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.estory.Models.Story
import com.example.estory.R

class StoryAdapter(
    private val storyList: List<Story>,
    private val itemClick: (Story) -> Unit
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.file_name)
        private val menuButton: ImageButton = itemView.findViewById(R.id.text_menu)

        init {
            itemView.setOnClickListener {
                itemClick(storyList[adapterPosition])
            }
            menuButton.setOnClickListener {
                // Add logic for menu button if needed
            }
        }

        fun bind(story: Story) {
            titleView.text = story.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.txt_file_firebase, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(storyList[position])
    }

    override fun getItemCount(): Int = storyList.size
}
