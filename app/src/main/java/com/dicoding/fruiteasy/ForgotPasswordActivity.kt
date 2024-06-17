package com.dicoding.fruiteasy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.model.RequestResetPasswordLink
import com.google.android.material.textfield.TextInputEditText

@Suppress("DEPRECATION")
class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val backButton = findViewById<ImageView>(R.id.backButton)
        val emailInput = findViewById< TextInputEditText>(R.id.email)
        val submitButton = findViewById<Button>(R.id.submitButton)

        backButton.setOnClickListener {
            finish()
        }

        submitButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isNotEmpty()) {
                val requestResetPasswordLink = RequestResetPasswordLink(email)
                val apiService = RetrofitClient.instance
                val call = apiService.requestPasswordLink(requestResetPasswordLink)

                call.enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ForgotPasswordActivity, "Password reset link has been sent to $email", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            // Handle specific error status codes
                            val errorMessage = when (response.code()) {
                                404 -> {
                                    val errorBody = response.errorBody()?.string()
                                    if (!errorBody.isNullOrEmpty()) {
                                        // Check the specific error message
                                        when {
                                            errorBody.contains("User not found") -> "We couldn't find an account with that email address. Please double-check and try again."
                                            else -> "Failed to send reset link: ${response.message()}"
                                        }
                                    } else {
                                        "Failed to send reset link: ${response.message()}"
                                    }
                                }
                                else -> "Failed to send reset link: ${response.message()}"
                            }

                            val builder = AlertDialog.Builder(this@ForgotPasswordActivity)
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

                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        Toast.makeText(this@ForgotPasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("ForgotPasswordActivity", "Request failed", t)
                    }
                })
            } else {
                Toast.makeText(this@ForgotPasswordActivity, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}