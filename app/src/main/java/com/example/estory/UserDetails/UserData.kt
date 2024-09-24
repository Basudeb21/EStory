package com.example.estory.UserDetails

import com.google.firebase.database.FirebaseDatabase

class UserData {

    companion object {
        var email: String? = null
        var name: String? = null
        var phoneNumber: String? = null
        var nickname: String? = null
        var language: String? = null
        var profileImageUrl: String? = null // This is used to store the profile image URL

        // Clear all user details
        fun clear() {
            email = null
            name = null
            phoneNumber = null
            nickname = null
            language = null
            profileImageUrl = null
        }

    }
}
