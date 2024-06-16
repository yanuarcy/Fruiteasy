package com.dicoding.fruiteasy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.model.ReportBugRequest
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ReportBugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_bug)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val backButton = findViewById<ImageView>(R.id.Back_button)
        val emailInput: TextInputEditText = findViewById(R.id.email)
        val messageInput: TextInputEditText = findViewById(R.id.message)
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        val submitButton: Button = findViewById(R.id.submit_button)

        // Set back button click listener
        backButton.setOnClickListener {
            finish()
        }



        submitButton.setOnClickListener {
            val email = emailInput.text.toString()
            val message = messageInput.text.toString()
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val feedback = selectedRadioButton.text.toString()

            val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
            val getFullName = sharedPref.getString("fullName", null)
            val fullName = getFullName.toString()


            if (email.isEmpty() || message.isEmpty() || selectedRadioButtonId == -1) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {

                submitContactUsForm(fullName, email, feedback, message)
                Toast.makeText(this, "My Name Is: $fullName", Toast.LENGTH_SHORT).show()

                // Handle the form submission (e.g., send data to server or save locally)
                Toast.makeText(this, "Feedback submitted: $feedback", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitContactUsForm(fullName: String, email: String, rating: String, message: String) {
        val reportBugRequest = ReportBugRequest(fullName, email, rating, message)
        val apiService = RetrofitClient.instance
        val call = apiService.submitReportBug(reportBugRequest)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ReportBugActivity, "Form submitted successfully.", Toast.LENGTH_SHORT).show()
                    // Optionally, clear the form or navigate to another activity
                    finish()
                } else {
                    Toast.makeText(this@ReportBugActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ReportBugActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}