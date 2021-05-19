package com.team48.applikasjon.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.team48.applikasjon.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*
This class creates the handler for the splash activity (defines the intent that will make it
appear before main activity)
 */

class SplashActivity : AppCompatActivity() {

    lateinit var lottieAnimationView : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()


        //added the variable for animation-view, in case of later use
        lottieAnimationView = findViewById(R.id.animatedLogo)

        //Uncertain for the use-case of this (not in use)
        //lottieAnimationView.animate().translationY(1400F).setDuration(1000).setStartDelay(4000)

       // CoroutineScope(Dispatchers.IO).launch {  }


        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            //You are not supposed to go back to the splash activity, therefore:
            finish()
            //shows splash for 3,5 seconds
        }, 3500)
    }
}