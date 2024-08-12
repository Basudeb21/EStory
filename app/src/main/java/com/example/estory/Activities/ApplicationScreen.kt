package com.example.estory.Activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.estory.BottomMenuFragments.*
import com.example.estory.BottomMenuFragments.SideNavItems.Profile
import com.example.estory.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class ApplicationScreen : AppCompatActivity() {

    lateinit var navbar: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private fun init() {
        navbar = findViewById(R.id.navbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.side_nav)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_application_screen)
        init()

        replaceFragment(Home())

        navbar.setOnItemSelectedListener { item: MenuItem ->
            if (item.itemId == R.id.home){
                navView.setCheckedItem(R.id.side_home)
            }
            else if (item.itemId == R.id.story){
                navView.setCheckedItem(R.id.side_story)
            }
            else if (item.itemId == R.id.pencil){
                navView.setCheckedItem(R.id.side_upload)
            }
            else if (item.itemId == R.id.notification){
                navView.setCheckedItem(R.id.side_notification)
            }
            else if (item.itemId == R.id.membership){
                navView.setCheckedItem(R.id.side_membership)
            }

            navHandler(item)

        }

        val menuButton: View = findViewById(R.id.menu)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(GravityCompat.START)
            if (item.itemId == R.id.side_story){
                navbar.selectedItemId = R.id.story
            }
            else if (item.itemId == R.id.side_home){
                navbar.selectedItemId = R.id.home
            }
            else if (item.itemId == R.id.side_upload){
                navbar.selectedItemId = R.id.pencil
            }
            else if (item.itemId == R.id.side_notification){
                navbar.selectedItemId = R.id.notification
            }
            else if (item.itemId == R.id.side_membership){
                navbar.selectedItemId = R.id.membership
            }
            else if(item.itemId == R.id.side_profile){
                replaceFragment(Profile())
                navView.setCheckedItem(R.id.side_profile)
            }
            navHandler(item)
            true
        }
    }

    private fun navHandler(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> replaceFragment(Home())
            R.id.story -> replaceFragment(Library())
            R.id.pencil -> replaceFragment(Upload())
            R.id.notification -> replaceFragment(Notification())
            R.id.membership -> replaceFragment(Membership())
            R.id.side_home -> replaceFragment(Home())
            R.id.side_story -> replaceFragment(Library())
            R.id.side_upload -> replaceFragment(Upload())
            R.id.side_notification -> replaceFragment(Notification())
            R.id.side_membership -> replaceFragment(Membership())

        }
        return true
    }



    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
