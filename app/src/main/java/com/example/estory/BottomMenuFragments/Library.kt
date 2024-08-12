package com.example.estory.BottomMenuFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.estory.BottomMenuFragments.HomeFragments.CatagoriesFragment
import com.example.estory.backend.FragmentAdaper
import com.example.estory.BottomMenuFragments.HomeFragments.RecomandedFragment
import com.example.estory.BottomMenuFragments.LibraryFragments.AllContents
import com.example.estory.BottomMenuFragments.LibraryFragments.Downloads
import com.example.estory.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Library : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: FragmentAdaper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        tabLayout = view.findViewById(R.id.library_tab)
        viewPager = view.findViewById(R.id.tab_page)

        adapter = FragmentAdaper(childFragmentManager, lifecycle)
        adapter.addFragment(AllContents(), "Online stories")
        adapter.addFragment(Downloads(), "Downloads")

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
        return view
    }
}