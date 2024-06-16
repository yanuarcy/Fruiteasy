package com.dicoding.fruiteasy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.util.Log
import android.view.WindowManager

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        Log.i("Credentials", "Credentials : ${sharedPref.all}")


        Handler(Looper.getMainLooper()).postDelayed({
            if (isLoggedIn) {
                val mainActivity = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(mainActivity)
            } else {
                val onBoarding = Intent(this@SplashActivity, OnBoardingActivity::class.java)
                startActivity(onBoarding)
            }
            finish()
        },4500)
    }
}