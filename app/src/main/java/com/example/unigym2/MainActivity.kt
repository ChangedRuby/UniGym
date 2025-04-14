package com.example.unigym2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.unigym2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var personalMode = false

        if(intent.getStringExtra("personalMode").equals("true")){
            personalMode = true
            replaceFragment(HomePersonalTrainer())
            Log.d("MainActivityDebug", "Login as personal")
        } else{
            replaceFragment(HomeUser())
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            if(personalMode){
                when(it.itemId){
                    R.id.home -> replaceFragment(HomePersonalTrainer())
                    R.id.chat -> replaceFragment(ChatUser())
                    R.id.profile -> replaceFragment(ProfileUser())
                    R.id.calendar -> replaceFragment(CalendarUser())
                    R.id.treinos -> replaceFragment(TreinosPersonalTrainer())

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

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}