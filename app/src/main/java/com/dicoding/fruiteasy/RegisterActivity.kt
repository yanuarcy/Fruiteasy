package com.dicoding.fruiteasy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Find views by ID
        val usernameEditText: EditText = findViewById(R.id.username)
        val emailEditText: EditText = findViewById(R.id.email)
        val phoneEditText: EditText = findViewById(R.id.phone)
        val passwordEditText: EditText = findViewById(R.id.password)
        val registerButton: Button = findViewById(R.id.sign_in_button)
        val createAccountTextView: TextView = findViewById(R.id.create_account)

        // Set click listener for the register button
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Simple validation
            if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                // Store user data locally
                val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("username", username)
                    putString("email", email)
                    putString("phone", phone)
                    putString("password", password)
                    apply()
                }
                // Registration successful
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                // You can start a new activity here or navigate back to the login screen
                // val intent = Intent(this, LoginActivity::class.java)
                // startActivity(intent)
                finish()  // Close this activity and go back to the previous one
            }
        }

        // Set click listener for the create account text view
        createAccountTextView.setOnClickListener {
            // Handle already have an account click
            Toast.makeText(this, "Already have an account Clicked", Toast.LENGTH_SHORT).show()
            // You can finish this activity or navigate back to the login screen
            finish()  // Close this activity and go back to the previous one
        }
    }
}