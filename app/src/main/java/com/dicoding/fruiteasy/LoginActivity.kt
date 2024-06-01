package com.dicoding.fruiteasy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Find views by ID
        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val signInButton: Button = findViewById(R.id.sign_in_button)
        val forgotPasswordTextView: TextView = findViewById(R.id.forgot_password)
        val createAccountTextView: TextView = findViewById(R.id.create_account)

        // Set click listener for the sign-in button
        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Simple validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Check stored user credentials
                val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
                val storedEmail = sharedPref.getString("email", null)
                val storedUsername = sharedPref.getString("username", null)
                val storedPassword = sharedPref.getString("password", null)

                if ((email == storedEmail || email == storedUsername) && password == storedPassword) {
                    // Sign-in successful
                    Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show()
                    // Navigate to HomeActivity
                     val homeIntent = Intent(this, MainActivity::class.java)
                     startActivity(homeIntent)
                } else {
                    // Sign-in failed
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set click listener for the forgot password text view
        forgotPasswordTextView.setOnClickListener {
            // Handle forgot password click
            Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show()
            // You can start a new activity or perform some action here
            // val intent = Intent(this, ForgotPasswordActivity::class.java)
            // startActivity(intent)
        }

        // Set click listener for the create account text view
        createAccountTextView.setOnClickListener {
            // Handle create new account click
            Toast.makeText(this, "Create New Account Clicked", Toast.LENGTH_SHORT).show()
            // You can start a new activity here
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}