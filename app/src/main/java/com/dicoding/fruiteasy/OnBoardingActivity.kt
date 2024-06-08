package com.dicoding.fruiteasy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class OnBoardingActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        val loginButton: Button = findViewById(R.id.Login)
        val registerButton: Button = findViewById(R.id.Register)

        loginButton.setOnClickListener(this)
        registerButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.Login -> {
                val loginIntent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
                startActivity(loginIntent)
            }

            R.id.Register -> {
                val registerIntent = Intent(this@OnBoardingActivity, RegisterActivity::class.java)
                startActivity(registerIntent)
            }
        }
    }
}