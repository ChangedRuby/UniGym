package com.example.unigym2.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.unigym2.Fragments.Calendar.CalendarUser
import com.example.unigym2.Fragments.Chat.ChatPersonal
import com.example.unigym2.Fragments.Chat.ChatUser
import com.example.unigym2.Fragments.Home.HomePersonalTrainer
import com.example.unigym2.Fragments.Home.HomeUser
import com.example.unigym2.Fragments.Profile.ProfilePersonal
import com.example.unigym2.Fragments.Profile.ProfileUser
import com.example.unigym2.Fragments.Treinos.TreinosPersonal
import com.example.unigym2.Fragments.Treinos.TreinosUser
import com.example.unigym2.R
import com.example.unigym2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), Communicator{
    var personalMode : Boolean = false
    lateinit var binding: ActivityMainBinding
    lateinit var userId: String
    lateinit var userEmail: String
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("userId").toString()
        userEmail = intent.getStringExtra("userEmail").toString()
        Log.d("MainActivityDebug", userId)

        db.collection("Usuarios").document(userId).get().addOnSuccessListener { document ->
            if(document.get("isPersonal") == true){
                personalMode = true
                replaceFragment(HomePersonalTrainer())
                Log.d("MainActivityDebug", "Personal mode set to true")
            } else{
                replaceFragment(HomeUser())
            }
            Log.d("MainActivityDebug", if(personalMode) "Login as personal" else "Login as user")
        }.addOnFailureListener { exception ->
            personalMode = true
            replaceFragment(HomePersonalTrainer())
            Log.d("MainActivityDebug", "Error getting user data; User not logged in; Login as Personal instead", exception)

        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            if(personalMode){
                when(it.itemId){
                    R.id.home -> replaceFragment(HomePersonalTrainer())
                    R.id.chat -> replaceFragment(ChatPersonal())
                    R.id.profile -> replaceFragment(ProfilePersonal())
                    R.id.calendar -> replaceFragment(CalendarUser())
                    R.id.treinos -> replaceFragment(TreinosPersonal())

                    else -> {
                        Log.d("MainActivityDebug", "Fragment not found")
                    }
                }
            } else{
                when(it.itemId){
                    R.id.home -> replaceFragment(HomeUser())
                    R.id.chat -> replaceFragment(ChatUser())
                    R.id.profile -> replaceFragment(ProfileUser())
                    R.id.calendar -> replaceFragment(CalendarUser())
                    R.id.treinos -> replaceFragment(TreinosUser())

                    else -> {
                        Log.d("MainActivityDebug", "Fragment not found")
                    }
                }
            }

            true
        }

    }

    override fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val supportFragmentManager = supportFragmentManager
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.addToBackStack(supportFragmentManager.findFragmentById(R.id.frame_layout).toString())
        fragmentTransaction.commit()
    }

    override fun getMode(): Boolean {
        return personalMode
    }

    override fun setAuthUser(userId: String) {
        this.userId = userId
    }

    override fun getAuthUser(): String {
        return userId
    }

    override fun setAuthUserEmail(userEmail: String) {
        this.userEmail = userEmail
    }

    override fun getAuthUserEmail(): String {
        return userEmail
    }
}