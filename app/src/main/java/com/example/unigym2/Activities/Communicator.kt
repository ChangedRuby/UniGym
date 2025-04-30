package com.example.unigym2.Activities

import androidx.fragment.app.Fragment

interface Communicator {
    fun replaceFragment(fragment: Fragment)
    fun getPersonalMode(): Boolean
}