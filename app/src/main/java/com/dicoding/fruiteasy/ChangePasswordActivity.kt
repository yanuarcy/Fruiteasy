package com.dicoding.fruiteasy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.model.ResetPasswordRequest
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.Back_button)
        val currentPassword = findViewById<TextInputEditText>(R.id.current_password)
        val newPassword = findViewById<TextInputEditText>(R.id.new_password)
        val confirmNewPassword = findViewById<TextInputEditText>(R.id.confirm_new_password)
        val submitButton = findViewById<Button>(R.id.btnSubmit)

        // Set back button click listener
        backButton.setOnClickListener {
            finish()
        }

        // Set submit button click listener
//        submitButton.setOnClickListener {
//            val currentPwd = currentPassword.text.toString()
//            val newPwd = newPassword.text.toString()
//            val confirmPwd = confirmNewPassword.text.toString()
//
//            if (validateInputs(currentPwd, newPwd, confirmPwd)) {
//                // Handle password reset logic here
//                if (changePassword(currentPwd, newPwd)) {
//                    Toast.makeText(this, "Password has been reset successfully!", Toast.LENGTH_SHORT).show()
//                    // Navigate to another activity or perform other actions as needed
//                } else {
//                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        submitButton.setOnClickListener {
            val currentPwd = currentPassword.text.toString()
            val newPwd = newPassword.text.toString()
            val confirmPwd = confirmNewPassword.text.toString()

            if (validateInputs(currentPwd, newPwd, confirmPwd)) {
                resetPassword(currentPwd, newPwd, confirmPwd)

            }
        }
    }

    private fun validateInputs(currentPwd: String, newPwd: String, confirmPwd: String): Boolean {
        if (currentPwd.isEmpty()) {
            Toast.makeText(this, "Current password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPwd.isEmpty()) {
            Toast.makeText(this, "New password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (confirmPwd.isEmpty()) {
            Toast.makeText(this, "Confirm new password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPwd != confirmPwd) {
            Toast.makeText(this, "New password and confirm password do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

//    private fun changePassword(currentPwd: String, newPwd: String): Boolean {
//        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
//        val storedPassword = sharedPref.getString("password", null)
//
//        return if (currentPwd == storedPassword) {
//            // Update the password in SharedPreferences
//            with(sharedPref.edit()) {
//                putString("password", newPwd)
//                apply()
//            }
//            true
//        } else {
//            false
//        }
//    }

    private fun resetPassword(currentPwd: String, newPwd: String, confirmPwd: String) {
        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val uidLocal = sharedPref.getString("uid", null)

        if (uidLocal != null) {
            val resetPasswordRequest = ResetPasswordRequest(
                uidLocal = uidLocal,
                currentPassword = currentPwd,
                newPassword = newPwd,
                confirmNewPassword = confirmPwd
            )

            val apiService = RetrofitClient.instance
            val call = apiService.resetPassword(resetPasswordRequest)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ChangePasswordActivity, "Password has been reset successfully!", Toast.LENGTH_SHORT).show()
                        // Navigate to another activity or perform other actions as needed
                        // val profileIntent = Intent(this@ChangePasswordActivity, ProfileFragment::class.java)
                        // startActivity(profileIntent)
                        finish()
                    } else {
                        // Handle specific error status codes
                        val errorMessage = when (response.code()) {
                            400 -> {
                                val errorBody = response.errorBody()?.string()
                                if (!errorBody.isNullOrEmpty()) {
                                    // Check the specific error message
                                    if (errorBody.contains("Current password is incorrect")) {
                                        "Current password is incorrect"
                                    } else if (errorBody.contains("New passwords do not match")) {
                                        "New passwords do not match"
                                    } else if (errorBody.contains("New password must be different from old password")) {
                                        "New password must be different from old password"
                                    } else if (errorBody.contains("Please enter all data correctly")) {
                                        "Please enter all data correctly"
                                    } else {
                                        "Error: ${response.message()}"
                                    }
                                } else {
                                    "Error: ${response.message()}"
                                }
                            }
                            else -> "Error: ${response.message()}"
                        }
                        Toast.makeText(this@ChangePasswordActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ChangePasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "User UID not found", Toast.LENGTH_SHORT).show()
        }
    }
}