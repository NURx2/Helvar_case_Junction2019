package com.nevmem.helvarapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    companion object {
        const val defaultDelay = 100L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        window.statusBarColor = resources.getColor(R.color.white)

        Handler(Looper.getMainLooper()).postDelayed({
            goToPswd()
        }, defaultDelay)
    }

    private fun goToPswd() {
        startActivity(
            Intent(this, PswdActivity::class.java),
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
}