package com.dicoding.fruiteasy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.model.LoginRequest
import com.dicoding.fruiteasy.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
//        signInButton.setOnClickListener {
//            val email = emailEditText.text.toString().trim()
//            val password = passwordEditText.text.toString().trim()
//
//            // Simple validation
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT)
//                    .show()
//            } else {
//                // Check stored user credentials
//                val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
//                val storedEmail = sharedPref.getString("email", null)
//                val storedUsername = sharedPref.getString("username", null)
//                val storedPassword = sharedPref.getString("password", null)
//
//                if ((email == storedEmail || email == storedUsername) && password == storedPassword) {
//                    // Sign-in successful
//                    Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show()
//                    // Navigate to HomeActivity
//                     val homeIntent = Intent(this, MainActivity::class.java)
//                     startActivity(homeIntent)
//                } else {
//                    // Sign-in failed
//                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        signInButton.setOnClickListener {
            val emailOrUsername = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Simple validation
            if (emailOrUsername.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                val loginRequest = LoginRequest(emailOrUsername, password)
                val apiService = RetrofitClient.instance
                val call = apiService.loginUser(loginRequest)
                call.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse != null && loginResponse.message == "Login Success") {
                                Toast.makeText(this@LoginActivity, "Sign In Successful", Toast.LENGTH_SHORT).show()
                                // Store user data locally if needed
                                val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putString("uid", loginResponse.user.uid)
                                    putString("id", loginResponse.user.id)
                                    putString("username", loginResponse.user.username)
                                    putString("email", loginResponse.user.email)
                                    putString("phone", loginResponse.user.phone)
                                    putString("password", loginResponse.user.password)
                                    putString("fullName", loginResponse.user.fullName)
                                    putString("gender", loginResponse.user.gender)
                                    putString("dateOfBirth", loginResponse.user.dateOfBirth)
                                    putString("address", loginResponse.user.address)
                                    putString("cities", loginResponse.user.cities)
                                    apply()
                                }
//                                Log.d("LoginActivity", "User ID: ${loginResponse.user}")
                                // Retrieve and log SharedPreferences data
                                val storedUid = sharedPref.getString("uid", null)
                                val storedId = sharedPref.getString("id", null)
                                val storedUsername = sharedPref.getString("username", null)
                                val storedEmail = sharedPref.getString("email", null)
                                val storedPhone = sharedPref.getString("phone", null)
                                val storedPassword = sharedPref.getString("password", null)
                                val storedFullname = sharedPref.getString("fullName", null)
                                val storedGender = sharedPref.getString("gender", null)
                                val storedDateOfBirth = sharedPref.getString("dateOfBirth", null)
                                val storedAddress = sharedPref.getString("address", null)
                                val storedCities = sharedPref.getString("cities", null)


                                Log.d("LoginActivity", "Stored UID: $storedUid")
                                Log.d("LoginActivity", "Stored UID: $storedId")
                                Log.d("LoginActivity", "Stored Username: $storedUsername")
                                Log.d("LoginActivity", "Stored Email: $storedEmail")
                                Log.d("LoginActivity", "Stored Phone: $storedPhone")
                                Log.d("LoginActivity", "Stored Password: $storedPassword")
                                Log.d("LoginActivity", "Stored FullName: $storedFullname")
                                Log.d("LoginActivity", "Stored Gender: $storedGender")
                                Log.d("LoginActivity", "Stored DateOfBirth: $storedDateOfBirth")
                                Log.d("LoginActivity", "Stored Address: $storedAddress")
                                Log.d("LoginActivity", "Stored Cities: $storedCities")


                                // Navigate to HomeActivity
                                val homeIntent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(homeIntent)
                                finish() // Close this activity
                            } else {
                                Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Login Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        // Set click listener for the forgot password text view
        forgotPasswordTextView.setOnClickListener {
            // Handle forgot password click
            Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show()
            // You can start a new activity or perform some action here
             val intent = Intent(this, ForgotPasswordActivity::class.java)
             startActivity(intent)
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