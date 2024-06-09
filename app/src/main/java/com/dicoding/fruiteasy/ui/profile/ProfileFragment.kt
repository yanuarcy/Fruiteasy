package com.dicoding.fruiteasy.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.R
import com.dicoding.fruiteasy.ChangePasswordActivity
import com.dicoding.fruiteasy.ContactUsActivity
import com.dicoding.fruiteasy.HistoryScanningActivity
import com.dicoding.fruiteasy.LoginActivity
import com.dicoding.fruiteasy.MyProfileActivity
import com.dicoding.fruiteasy.ReportBugActivity
import com.dicoding.fruiteasy.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view: View = binding.root

        // Load profile image with Glide
//        Glide.with(this)
//            .load(R.drawable.profile_img)
//            .circleCrop()
//            .into(binding.profilePicture)

        // Setup click listeners for the LinearLayouts
        binding.myProfile.setOnClickListener {
            // Handle My Profile click
            Toast.makeText(requireContext(), "My Profile clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), MyProfileActivity::class.java)
            startActivity(intent)
        }

        binding.changePassword.setOnClickListener {
            // Handle Change Password click
            Toast.makeText(requireContext(), "Change Password clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.historyScanning.setOnClickListener {
            // Handle History Scanning click
            Toast.makeText(requireContext(), "History Scanning clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), HistoryScanningActivity::class.java)
            startActivity(intent)
        }

        binding.reportBug.setOnClickListener {
            // Handle Report Bug click
            Toast.makeText(requireContext(), "Report Bug clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), ReportBugActivity::class.java)
            startActivity(intent)
        }

        binding.contactUs.setOnClickListener {
            // Handle Contact Us click
            Toast.makeText(requireContext(), "Contact Us clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), ContactUsActivity::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener {
            // Handle Log Out click
            Toast.makeText(requireContext(), "Log Out clicked", Toast.LENGTH_SHORT).show()
            // Perform logout operation
            performLogout()
        }

        return view
    }
    override fun onResume() {
        super.onResume()
        // Load data from SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "Fruiteasy")
        val userId = sharedPref.getString("id", "ID: 1204210134")
        val userEmail = sharedPref.getString("email", "fruiteasy1@gmail.com")

        // Set data to TextViews
        binding.username.text = username
        binding.userId.text = "ID: $userId"
        binding.userEmail.text = userEmail
    }

    private fun performLogout() {
        // Logic to clear user session or token
        // Example:
        // SessionManager.logoutUser()

        // Navigate to login screen
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}