package com.example.estory.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.estory.BottomMenuFragments.*
import com.example.estory.Fetures.SearchFragment
import com.example.estory.SideNavItems.ContactUsFragment
import com.example.estory.SideNavItems.Profile
import com.example.estory.R
import com.example.estory.UserDetails.UserData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class ApplicationScreen : AppCompatActivity() {

    private lateinit var navbar: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var u_name: TextView
    private lateinit var u_mail: TextView
    private lateinit var u_nname: TextView

    private fun init() {
        navbar = findViewById(R.id.navbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.side_nav)
        val headerView = navView.getHeaderView(0)
        u_name = headerView.findViewById(R.id.user_name)
        u_mail = headerView.findViewById(R.id.user_mail)
        u_nname = headerView.findViewById(R.id.user_nick_name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_screen)
        init()

        // Set user details
        u_name.text = UserData.name ?: "Default Name"
        u_nname.text = UserData.nickname ?: "Default Nickname"
        u_mail.text = UserData.email ?: "Default Email"

        replaceFragment(Home())

        val search_btn = findViewById<CircleImageView>(R.id.search)
        search_btn.setOnClickListener{
            replaceFragment(SearchFragment())
        }
        val profile_btn = findViewById<CircleImageView>(R.id.profile_pic)
        profile_btn.setOnClickListener{
            replaceFragment(Profile())
        }


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
            else if(item.itemId == R.id.side_contacts){
                replaceFragment(ContactUsFragment())
                navView.setCheckedItem(R.id.side_profile)
            }
            else if (item.itemId == R.id.side_logout) {
                showLogoutConfirmationDialog()
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
        Log.d("ApplicationScreen", "Replacing fragment with ${fragment::class.java.simpleName}")

        // Check if the fragment is already the current one
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
        if (currentFragment?.javaClass == fragment.javaClass) {
            Log.d("ApplicationScreen", "Fragment already loaded: ${fragment::class.java.simpleName}")
            return
        }

        // Clear the back stack
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        // Replace the fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit the app?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                finish() // Exit the app
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() } // Do nothing
            .show()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                logoutUser()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() } // Do nothing
            .show()
    }
    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut() // Sign out from Firebase
        UserData.clear() // Clear user data
        startActivity(Intent(this, LoginActivity::class.java)) // Redirect to LoginActivity
        finish() // Close the ApplicationScreen
    }


    override fun onBackPressed() {
        super.onBackPressed()
        showExitConfirmationDialog() // Show exit confirmation dialog
    }
}
