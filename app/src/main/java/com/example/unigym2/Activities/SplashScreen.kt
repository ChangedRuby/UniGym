package com.example.unigym2.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.unigym2.ui.theme.UniGym2Theme

@ExperimentalMaterial3Api
class SplashScreen : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            true
        }

        var intent = Intent(this, LoginMenu::class.java)
        startActivity(intent)



    }
}