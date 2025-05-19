package com.example.unigym2.Activities

import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

interface Communicator {
    fun replaceFragment(fragment: Fragment)
    fun getMode(): Boolean
    fun setAuthUser(userId: String)
    fun getAuthUser(): String
    fun setAuthUserEmail(userEmail: String)
    fun getAuthUserEmail(): String
    fun setAuthUserName(userName: String)
    fun getAuthUserName(): String
    fun getAuthInstance(): FirebaseAuth
}