package com.example.estory.Activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.estory.BottomMenuFragments.*
import com.example.estory.Fetures.SearchFragment
import com.example.estory.SideNavItems.ContactUsFragment
import com.example.estory.SideNavItems.Profile
import com.example.estory.R
import com.example.estory.UserDetails.UserData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
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

        u_name.text = UserData.name
        u_nname.text = UserData.nickname
        u_mail.text = UserData.email

        replaceFragment(Home())

        findViewById<CircleImageView>(R.id.search).setOnClickListener {
            replaceFragment(SearchFragment())
        }
        findViewById<CircleImageView>(R.id.profile_pic).setOnClickListener {
            replaceFragment(Profile())
        }

        navbar.setOnItemSelectedListener { item ->
            navHandler(item)
        }

        findViewById<View>(R.id.menu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        navView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(GravityCompat.START)
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
            R.id.side_profile -> {
                replaceFragment(Profile())
                navView.setCheckedItem(R.id.side_profile)
            }
            R.id.side_contacts -> {
                replaceFragment(ContactUsFragment())
                navView.setCheckedItem(R.id.side_contacts)
            }
            R.id.side_logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null) // Optionally add to back stack
        fragmentTransaction.commit()
    }
}
