package com.example.estory.BottomMenuFragments.UploadFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.estory.BottomMenuFragments.UploadFragments.CloudHandler.StoryAdapter
import com.example.estory.BottomMenuFragments.UploadFragments.StoryOutputFirebase
import com.example.estory.Models.Story
import com.example.estory.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseFilesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter
    private var storiesList: ArrayList<Story> = ArrayList()
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_firebase_files, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize the StoryAdapter
        storyAdapter = StoryAdapter(storiesList) { story ->
            openStoryOutput(story)
        }
        recyclerView.adapter = storyAdapter

        // Initialize FirebaseAuth and DatabaseReference
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Load stories from Firebase
        loadStoriesFromFirebase()

        return view
    }

    private fun loadStoriesFromFirebase() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid

            database.child("estory").child("users").child(uid).child("unposted_stories")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            storiesList.clear()
                            for (storySnapshot in snapshot.children) {
                                val title = storySnapshot.child("title").getValue(String::class.java)
                                val description = storySnapshot.child("description").getValue(String::class.java)
                                if (title != null && description != null) {
                                    val story = Story(title, description)
                                    storiesList.add(story)
                                } else {
                                    Log.w("FirebaseFilesFragment", "Missing title or description for story ID: ${storySnapshot.key}")
                                }
                            }
                            storyAdapter.notifyDataSetChanged()  // Notify the adapter of data changes
                        } else {
                            Log.d("FirebaseFilesFragment", "No stories found at unposted_stories level.")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseFilesFragment", "Error loading stories: ${error.message}")  // Log errors
                    }
                })
        } else {
            Log.w("FirebaseFilesFragment", "No current user found.")
        }
    }

    private fun openStoryOutput(story: Story) {
        val fragment = StoryOutputFirebase().apply {
            arguments = Bundle().apply {
                putString("FILE_NAME", story.title)
                putString("NOTE_DESC", story.description)
            }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}
