package com.team48.applikasjon.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.team48.applikasjon.R

/* Klasse for opprettelse av SplashScreen ved appens oppstart */
class SplashActivity : AppCompatActivity() {

    // View for splashscreen
    lateinit var lottieAnimationView : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Fjerner actionbar fra splashscreen og initialiserer view
        supportActionBar?.hide()
        lottieAnimationView = findViewById(R.id.animatedLogo)

        // Oppsett av hvor lenge splash screen skal kjøres
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // Vi skal aldri returnere til den, så gjør kall på finish
            finish()
        }, 3500)
    }
}