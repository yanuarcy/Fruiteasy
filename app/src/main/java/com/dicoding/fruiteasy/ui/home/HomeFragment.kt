package com.dicoding.fruiteasy.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.dicoding.fruiteasy.DetailContentActivity
import com.dicoding.fruiteasy.MyProfileActivity
import com.dicoding.fruiteasy.R
import com.dicoding.fruiteasy.api.ApiService
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.databinding.FragmentHomeBinding
import com.dicoding.fruiteasy.model.FruitContent
import com.dicoding.fruiteasy.model.FruitSeason
import com.dicoding.fruiteasy.model.HistoryResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val targetSteps = 40
    private lateinit var loadingAnimation: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Lottie Animation
        loadingAnimation = binding.root.findViewById(R.id.loadingAnimation)

        // Set up click listener for the profile icon
        binding.profileIcon.setOnClickListener {
            val intent = Intent(activity, MyProfileActivity::class.java)
            startActivity(intent)
        }

        val sharedPref = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "")

        binding.tvGreeting.text = "$username,"

        // Set current month
        val currentMonth = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())
//        val currentMonthNumber = Calendar.getInstance().get(Calendar.MONTH) + 1
        binding.tvMusimBuah.text = currentMonth

        // Fetch data content from the API
        // Populate data from SharedPreferences
//        val savedData = populateDataFromPreferences()
//        if (savedData != null) {
//            populateDataFromPreferences()
//        } else {
//            // Fetch data from the API
//            fetchData()
//        }

//        fetchData()
        // Populate data from SharedPreferences
        populateDataFromPreferences()
        // Fetch data season from the API
//        fetchFruitSeasonData(currentMonth)

        // Fetch history data fruit season
//        val userLocalId = sharedPref.getString("uid", "") ?: ""
//        Log.i("Home", "This is my Local UID: $userLocalId")
//        if (userLocalId.isNotEmpty()) {
//            fetchHistoryData(userLocalId)
//        }

        return root
    }

    private fun loadDataFromPreferences(): List<FruitContent>? {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = sharedPreferences.getString("fruitContentData", null)
        val type = object : TypeToken<List<FruitContent>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun onResume() {
        super.onResume()

        val sharedPref = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val userLocalId = sharedPref.getString("uid", "") ?: ""
        Log.i("Home", "This is my Local UID: $userLocalId")


        if (userLocalId.isNotEmpty()) {
            fetchHistoryData(userLocalId)
        }

        fetchData()
        populateDataFromPreferences()


        val savedStepsCount = sharedPref.getInt("stepsCount", 0)
        Log.i("Home", "Loading steps count from SharedPreferences: $savedStepsCount")
        // Update UI with the steps count from SharedPreferences
        updateSteps(savedStepsCount)
    }

    private fun fetchHistoryData(userLocalId: String) {
        // Show loading animations
        binding.loading1.visibility = View.VISIBLE
        binding.loading2.visibility = View.VISIBLE
        binding.tvSteps.visibility = View.GONE
        binding.progressPercentage.visibility = View.GONE
        binding.progressBar.progress = 0

        val apiService = RetrofitClient.instance
        val call = apiService.getHistory(userLocalId)

        call.enqueue(object : Callback<List<HistoryResponse>> {
            override fun onResponse(call: Call<List<HistoryResponse>>, response: Response<List<HistoryResponse>>) {
                if (response.isSuccessful) {
                    val historyData = response.body()
                    if (!historyData.isNullOrEmpty()) {
                        val stepsCount = getStepsForCurrentMonth(historyData)
                        Log.i("Home", "This is total my history: $stepsCount")
                        saveStepsCount(stepsCount) // Save to SharedPreferences after fetching API
                        // Update UI with the steps count from SharedPreferences
//                        updateSteps(stepsCount)
                    } else {
                        updateSteps(0)
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch history data: ${response.message()}", Toast.LENGTH_SHORT).show()
                }

                // Hide loading animations after API call completes
                binding.loading1.visibility = View.GONE
                binding.loading2.visibility = View.GONE
                binding.tvSteps.visibility = View.VISIBLE
                binding.progressPercentage.visibility = View.VISIBLE
            }

            override fun onFailure(call: Call<List<HistoryResponse>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                // Hide loading animations on failure
                binding.loading1.visibility = View.GONE
                binding.loading2.visibility = View.GONE
            }
        })
    }

    private fun saveStepsCount(stepsCount: Int) {
        val sharedPref = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("stepsCount", stepsCount)
            apply()
        }
    }

    private fun getStepsForCurrentMonth(historyData: List<HistoryResponse>): Int {
        val currentMonth = SimpleDateFormat("MM", Locale.getDefault()).format(Date())
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        return historyData.filter {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(it.createdAt)
            val month = SimpleDateFormat("MM", Locale.getDefault()).format(date)
            val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
            month == currentMonth && year == currentYear
        }.size
    }

    private fun updateSteps(steps: Int) {
        binding.tvSteps.text = steps.toString()
        binding.tvTargetSteps.text = "/ $targetSteps scan"

        val progress = (steps.toFloat() / targetSteps * 100).toInt()
        binding.progressBar.progress = progress
        binding.progressPercentage.text = "$progress%"

        if (steps >= targetSteps) {
            binding.tvTitleSteps.text = "Selamat anda telah mencapai target untuk scan buah di bulan ini"
        }
    }

    private fun fetchData() {
        val apiService = RetrofitClient.instance
        val call = apiService.getFruitContentByCurrentMonth()

        call.enqueue(object : Callback<List<FruitContent>> {
            override fun onResponse(call: Call<List<FruitContent>>, response: Response<List<FruitContent>>) {
                if (response.isSuccessful) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val fruitContentList = response.body()
                        Log.i("DataContent", "This is Data Content Olahan Buah Before save to Preferences : $fruitContentList")
                        if (!fruitContentList.isNullOrEmpty()) {
                            // Save data to SharedPreferences
                            saveDataToPreferences(fruitContentList)
//                            populateDataFromPreferences()
                        } else {
                            showEmptyAnimation()
                        }
                        loadingAnimation.visibility = View.GONE
                    }, 1000)
                } else {
                    loadingAnimation.visibility = View.GONE
                    Toast.makeText(context, "Failed to fetch data: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FruitContent>>, t: Throwable) {
                // Hide loading animation
                loadingAnimation.visibility = View.GONE
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveDataToPreferences(data: List<FruitContent>) {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(data)
        editor.putString("fruitContentData", json)
        editor.apply()
    }

    private fun populateDataFromPreferences() {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = sharedPreferences.getString("fruitContentData", null)
        val type = object : TypeToken<List<FruitContent>>() {}.type
        val fruitContentList: List<FruitContent>? = gson.fromJson(json, type)

        if (fruitContentList != null) {
            Log.i("ContentData", "data Olahan Buah yang ditampilkan $fruitContentList")
            populateData(fruitContentList)
        } else {
            Toast.makeText(context, "No data found in preferences", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateData(data: List<FruitContent>) {
        val container = binding.horizontalScrollViewContainer
//        container.removeAllViews()

        data.forEach { item ->
            val view = LayoutInflater.from(context).inflate(R.layout.item_fruit_content, container, false)

            val imageContent = view.findViewById<ImageView>(R.id.imageContent)
            val tvContentTitle = view.findViewById<TextView>(R.id.tvContentTitle)
            val tvContentDesc = view.findViewById<TextView>(R.id.tvContentDesc)

            Glide.with(this).load(item.image).into(imageContent)
            tvContentTitle.text = item.name
            tvContentDesc.text = item.benefit

            view.setOnClickListener {
                val intent = Intent(activity, DetailContentActivity::class.java).apply {
                    putExtra("image", item.image)
                    putExtra("name", item.name)
                    putExtra("benefit", item.benefit)
                    putExtra("ingredients", item.ingredients)
                    putExtra("steps", item.steps)
                }
                startActivity(intent)
            }

            container.addView(view)
        }
    }


//    private fun fetchFruitSeasonData(month: Int) {
//        val apiService = RetrofitClient.instance
//        val call = apiService.getFruitSeasonByMonth(month)
//
//        call.enqueue(object : Callback<List<FruitSeason>> {
//            override fun onResponse(call: Call<List<FruitSeason>>, response: Response<List<FruitSeason>>) {
//                if (response.isSuccessful) {
//                    val fruitSeasonList = response.body()
//                    if (!fruitSeasonList.isNullOrEmpty()) {
//                        populateFruitSeasonData(fruitSeasonList)
//                    } else {
//                        showEmptyAnimation()
//                    }
//                } else {
//                    Toast.makeText(context, "Failed to fetch data: ${response.message()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<List<FruitSeason>>, t: Throwable) {
//                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

//    private fun populateFruitSeasonData(data: List<FruitSeason>) {
//        val container = binding.horizontalScrollViewContainerSeason
//        container.removeAllViews()
//
//        // Split the data into chunks of 6 for 2 rows of 3 items each
//        val chunkedData = data.chunked(6)
//        chunkedData.forEach { chunk ->
//            val view = LayoutInflater.from(context).inflate(R.layout.item_fruit_season, container, false)
//
//            // Set up the first row
//            chunk.getOrNull(0)?.let { item ->
//                val fruitCard = view.findViewById<LinearLayout>(R.id.fruitCard1)
//                setFruitSeasonData(fruitCard, item)
//            }
//            chunk.getOrNull(1)?.let { item ->
//                val fruitCard = view.findViewById<LinearLayout>(R.id.fruitCard2)
//                setFruitSeasonData(fruitCard, item)
//            }
//            chunk.getOrNull(2)?.let { item ->
//                val fruitCard = view.findViewById<LinearLayout>(R.id.fruitCard3)
//                setFruitSeasonData(fruitCard, item)
//            }
//
//            // Set up the second row
//            chunk.getOrNull(3)?.let { item ->
//                val fruitCard = view.findViewById<LinearLayout>(R.id.fruitCard4)
//                setFruitSeasonData(fruitCard, item)
//            }
//            chunk.getOrNull(4)?.let { item ->
//                val fruitCard = view.findViewById<LinearLayout>(R.id.fruitCard5)
//                setFruitSeasonData(fruitCard, item)
//            }
//            chunk.getOrNull(5)?.let { item ->
//                val fruitCard = view.findViewById<LinearLayout>(R.id.fruitCard6)
//                setFruitSeasonData(fruitCard, item)
//            }
//
//            container.addView(view)
//        }
//    }

//    private fun setFruitSeasonData(fruitCard: LinearLayout, item: FruitSeason) {
//        val imageView = fruitCard.findViewById<ImageView>(R.id.fruit1)
//        val textView = fruitCard.findViewById<TextView>(R.id.tvFruit1)
//
//        if (item.Icon.isNotEmpty()) {
//            Glide.with(this).load(item.Icon).into(imageView)
//        } else {
//            imageView.setImageResource(R.drawable.icon_anggur) // Use a default image if none provided
//        }
//        textView.text = item.Buah
//    }

    private fun showEmptyAnimation() {
        // Logic to show empty animation
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
