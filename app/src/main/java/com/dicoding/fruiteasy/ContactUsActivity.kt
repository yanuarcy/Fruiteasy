package com.dicoding.fruiteasy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import com.dicoding.fruiteasy.api.ApiService
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.model.ContactUsRequest
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ContactUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val backButton: ImageView = findViewById(R.id.backButton)
        val nameInput: TextInputEditText = findViewById(R.id.name)
        val emailInput: TextInputEditText = findViewById(R.id.email)
        val phoneInput: TextInputEditText = findViewById(R.id.phone)
        val messageInput: TextInputEditText = findViewById(R.id.message)
        val agreeTerms: CheckBox = findViewById(R.id.agreeTerms)
        val contactUsButton: Button = findViewById(R.id.contactUsButton)

        backButton.setOnClickListener {
            finish()
        }

        contactUsButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val message = messageInput.text.toString().trim()
            val termsAccepted = agreeTerms.isChecked

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || message.isEmpty() || !termsAccepted) {
                Toast.makeText(this, "Please fill all fields and accept the terms.", Toast.LENGTH_SHORT).show()
            } else {
                // Handle form submission
                submitContactUsForm(name, email, phone, message)
            }
        }
    }

    private fun submitContactUsForm(name: String, email: String, phone: String, message: String) {
        val contactUsRequest = ContactUsRequest(name, email, phone, message)
        val apiService = RetrofitClient.instance
        val call = apiService.submitContactUs(contactUsRequest)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ContactUsActivity, "Form submitted successfully.", Toast.LENGTH_SHORT).show()
                    // Optionally, clear the form or navigate to another activity
                    finish()
                } else {
                    Toast.makeText(this@ContactUsActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ContactUsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}