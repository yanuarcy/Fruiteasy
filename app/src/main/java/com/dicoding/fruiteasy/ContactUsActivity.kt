package com.dicoding.fruiteasy

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.databinding.ActivityContactUsBinding
import com.dicoding.fruiteasy.model.ContactUsRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ContactUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val storedEmail = sharedPref.getString("email", null)
        val storedPhone = sharedPref.getString("phone", null)

        binding.email.setText(storedEmail)
        binding.phone.setText(storedPhone)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.contactUsButton.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val phone = binding.phone.text.toString().trim()
            val message = binding.message.text.toString().trim()
            val termsAccepted = binding.agreeTerms.isChecked

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