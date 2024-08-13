package com.example.estory.SideNavItems

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.estory.R

class ContactUsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)

        val callME: CardView = view.findViewById(R.id.visit_call)
        callME.setOnClickListener {
            val phoneNumber = "tel:9832961542"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(phoneNumber)
            startActivity(intent)
        }

        val visitFB: CardView = view.findViewById(R.id.visit_fb)
        visitFB.setOnClickListener {
            val url = "https://www.facebook.com/Niladittya21"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val visitEmail: CardView = view.findViewById(R.id.vist_mail)
        visitEmail.setOnClickListener {
            val email = "bpaulc21@gmail.com"
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$email")
            startActivity(intent)
        }

        val visitGit: CardView = view.findViewById(R.id.visit_github)
        visitGit.setOnClickListener {
            val url = "https://github.com/Basudeb21"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val vistLinkedin: CardView = view.findViewById(R.id.visit_linkedin)
        vistLinkedin.setOnClickListener {
            val url = "https://www.linkedin.com/in/basudeb21/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        val visitWP: CardView = view.findViewById(R.id.vist_whatsapp)
        visitWP.setOnClickListener {
            val url = "https://wa.me/9832961542"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        return view
    }
}
