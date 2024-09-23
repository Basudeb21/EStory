package com.example.estory.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthUser {
     fun getCurrentUser(): FirebaseUser? {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return currentUser
        } else {
            return null
        }
    }
}