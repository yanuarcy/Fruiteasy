package com.dicoding.fruiteasy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.model.User
import com.dicoding.fruiteasy.ui.profile.ProfileFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Find views by ID
        val usernameEditText: EditText = findViewById(R.id.username)
        val emailEditText: EditText = findViewById(R.id.email)
        val phoneEditText: EditText = findViewById(R.id.phone)
        val passwordEditText: EditText = findViewById(R.id.password)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirm_password)
        val registerButton: Button = findViewById(R.id.sign_in_button)
        val createAccountTextView: TextView = findViewById(R.id.create_account)

        // Set click listener for the register button
//        registerButton.setOnClickListener {
//            val username = usernameEditText.text.toString().trim()
//            val email = emailEditText.text.toString().trim()
//            val phone = phoneEditText.text.toString().trim()
//            val password = passwordEditText.text.toString().trim()
//
//            // Simple validation
//            if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
//            } else {
//                // Store user data locally
//                val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
//                with(sharedPref.edit()) {
//                    putString("username", username)
//                    putString("email", email)
//                    putString("phone", phone)
//                    putString("password", password)
//                    apply()
//                }
//                // Registration successful
//                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
//                // You can start a new activity here or navigate back to the login screen
//                 val intent = Intent(this, ProfileFragment::class.java)
//                 startActivity(intent)
//                finish()  // Close this activity and go back to the previous one
//            }
//        }

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Simple validation
//            if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
//                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
//            } else if (password != confirmPassword) {
//                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
//            } else {
            val user = User(uid = "", id = "", username, email, phone, password, confirmPassword, fullName = "", gender = "", dateOfBirth = "", address = "", cities = "")
            val apiService = RetrofitClient.instance
            val call = apiService.requestVerifyEmail(user)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        val builder = AlertDialog.Builder(this@RegisterActivity)
                        builder.setTitle("Success")
                        builder.setMessage("Registration processed. To continue, please check your email for verification.")

                        // Set the positive button (OK button)
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            finish() // Finish the activity when OK is clicked
                        }

                        // Create and show the AlertDialog
                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        // Handle specific error status codes
                        val errorMessage = when (response.code()) {
                            400 -> {
                                val errorBody = response.errorBody()?.string()
                                if (!errorBody.isNullOrEmpty()) {
                                    // Check the specific error message
                                    when {
                                        errorBody.contains("Passwords do not match") -> "The passwords you entered do not match. Please make sure both passwords are the same."
                                        errorBody.contains("Email already in use") -> "The email address you entered is already in use. Please try a different email address or log in if you already have an account."
                                        errorBody.contains("Please enter all data correctly") -> "It seems like some information is missing or incorrect. Please double-check your entries and try again."
                                        else -> "Failed to send verification email: ${response.message()}"
                                    }
                                } else {
                                    "Failed to send verification email: ${response.message()}"
                                }
                            }
                            else -> "Failed to send verification email: ${response.message()}"
                        }

                        val builder = AlertDialog.Builder(this@RegisterActivity)
                        builder.setTitle("Error")
                        builder.setMessage(errorMessage)

                        // Set the positive button (OK button)
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }

                        // Create and show the AlertDialog
                        val dialog = builder.create()
                        dialog.show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
//            }
        }

        // Set click listener for the create account text view
        createAccountTextView.setOnClickListener {
            // Handle already have an account click
//            Toast.makeText(this, "Already have an account Clicked", Toast.LENGTH_SHORT).show()
            // You can finish this activity or navigate back to the login screen
            finish()  // Close this activity and go back to the previous one
        }
    }
}