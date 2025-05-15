package com.example.unigym2.Activities

import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser

interface Communicator {
    fun replaceFragment(fragment: Fragment)
    fun getMode(): Boolean
    fun setAuthUser(userId: String)
    fun getAuthUser(): String
    fun setAuthUserEmail(userEmail: String)
    fun getAuthUserEmail(): String
    fun setAuthUserName(userName: String)
    fun getAuthUserName(): String
}