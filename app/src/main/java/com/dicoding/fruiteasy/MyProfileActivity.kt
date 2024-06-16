package com.dicoding.fruiteasy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.fruiteasy.api.ApiService
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.databinding.ActivityMyprofileBinding
import com.dicoding.fruiteasy.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyprofileBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPref", Context.MODE_PRIVATE)

        // Back button listener
        binding.backButton.setOnClickListener {
            finish()
        }

        // Load saved data
        loadData()

        // Set up click listeners for edit actions
        binding.editPhoneNumber.setOnClickListener {
            toggleEditMode(binding.phoneNumber, binding.PhoneNumberInput, binding.editPhoneNumber, "phone")
        }

        binding.editEmail.setOnClickListener {
            toggleEditMode(binding.email, binding.emailInput, binding.editEmail, "email")
        }

        binding.editBioContent.setOnClickListener {
            toggleEditMode(binding.fullName, binding.fullNameInput, binding.editBioContent, "fullName")
            toggleEditMode(binding.gender, binding.genderInput, binding.editBioContent, "gender")
            toggleEditMode(binding.birthDate, binding.birthDateInput, binding.editBioContent, "dateOfBirth")
            toggleEditMode(binding.address, binding.addressInput, binding.editBioContent, "address")
            toggleEditMode(binding.city, binding.cityInput, binding.editBioContent, "cities")
        }
    }

    private fun loadData() {
        binding.phoneNumber.text = sharedPreferences.getString("phone", "082257508081")
        binding.PhoneNumberInput.setText(sharedPreferences.getString("phone", "082257508081"))

        binding.email.text = sharedPreferences.getString("email", "yanuarcahyo567@gmail.com")
        binding.emailInput.setText(sharedPreferences.getString("email", "yanuarcahyo567@gmail.com"))

        binding.id.text = sharedPreferences.getString("id", "1204210134")

        binding.fullName.text = sharedPreferences.getString("fullName", "Yanuar")
        binding.fullNameInput.setText(sharedPreferences.getString("fullName", "Yanuar"))

        binding.gender.text = sharedPreferences.getString("gender", "Pria")
        binding.genderInput.setText(sharedPreferences.getString("gender", "Pria"))

        binding.birthDate.text = sharedPreferences.getString("dateOfBirth", "17 Agustus 1945")
        binding.birthDateInput.setText(sharedPreferences.getString("dateOfBirth", "17 Agustus 1945"))

        binding.address.text = sharedPreferences.getString("address", "Manukan Kulon")
        binding.addressInput.setText(sharedPreferences.getString("address", "Manukan Kulon"))

        binding.city.text = sharedPreferences.getString("cities", "Surabaya")
        binding.cityInput.setText(sharedPreferences.getString("cities", "Surabaya"))
    }

    private fun toggleEditMode(textView: TextView, editText: EditText, button: TextView, key: String) {
        if (textView.visibility == View.VISIBLE) {
            // Switch to edit mode
            textView.visibility = View.GONE
            editText.visibility = View.VISIBLE
            button.text = "SIMPAN"
        } else {
            // Save changes and switch back to view mode
            val newValue = editText.text.toString()
            textView.text = newValue
            textView.visibility = View.VISIBLE
            editText.visibility = View.GONE
            button.text = "UBAH"

            // Save the new value in SharedPreferences
            with(sharedPreferences.edit()) {
                putString(key, newValue)
                apply()
            }

            // Update profile with new values
            updateProfile()
        }
    }

    private fun updateProfile() {
        val user = User(
            uid = sharedPreferences.getString("uid", "") ?: "",
            id = sharedPreferences.getString("id", "") ?: "",
            username = sharedPreferences.getString("username", "") ?: "",
            email = sharedPreferences.getString("email", "") ?: "",
            phone = sharedPreferences.getString("phone", "") ?: "",
            password = sharedPreferences.getString("password", "") ?: "",
            confirmPassword = sharedPreferences.getString("confirmPassword", "") ?: "",
            fullName = sharedPreferences.getString("fullName", "") ?: "",
            gender = sharedPreferences.getString("gender", "") ?: "",
            dateOfBirth = sharedPreferences.getString("dateOfBirth", "") ?: "",
            address = sharedPreferences.getString("address", "") ?: "",
            cities = sharedPreferences.getString("cities", "") ?: ""
        )

        val apiService = RetrofitClient.instance
        val call = apiService.editProfile(user)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MyProfileActivity, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MyProfileActivity, "Profile Update Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.d("MyProfile", "Error : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MyProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
