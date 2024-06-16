package com.dicoding.fruiteasy

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.dicoding.fruiteasy.api.ApiService
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.model.HistoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class HistoryScanningActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var loadingAnimation: LottieAnimationView
    private lateinit var emptyAnimation: LottieAnimationView
    private lateinit var emptyMessage: TextView
    private lateinit var emptyStateContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_scanning)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val backButton = findViewById<ImageView>(R.id.Back_button)
        backButton.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        loadingAnimation = findViewById(R.id.loadingAnimation)
        emptyAnimation = findViewById(R.id.emptyAnimation)
        emptyMessage = findViewById(R.id.emptyMessage)
        emptyStateContainer = findViewById(R.id.emptyStateContainer)

        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val userLocalId = sharedPref.getString("uid", null)
        Log.i("History", "This is UID: $userLocalId")

        if (userLocalId != null) {
            fetchHistoryData(userLocalId)
        } else {
            Log.e("History", "UserLocalId is null")
            showEmptyAnimation()
        }
    }

    private fun fetchHistoryData(userLocalId: String) {
        Log.i("History", "Fetching history data for user: $userLocalId")
        val apiService = RetrofitClient.instance
        val call = apiService.getHistory(userLocalId)

        call.enqueue(object : Callback<List<HistoryResponse>> {
            override fun onResponse(call: Call<List<HistoryResponse>>, response: Response<List<HistoryResponse>>) {
                Log.i("History", "Response received: ${response.code()}")
                if (response.isSuccessful) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val historyData = response.body()
                        Log.i("History", "History data: $historyData")
                        if (!historyData.isNullOrEmpty()) {
                            val items = groupByDate(historyData)
                            displayHistoryData(items)
                        } else {
                            Log.i("History", "History data is empty")
                            showEmptyAnimation()
                        }
                        loadingAnimation.visibility = View.GONE
                    }, 1000)
                } else {
                    Log.e("History", "Response not successful: ${response.message()}")
                    loadingAnimation.visibility = View.GONE
                    showEmptyAnimation()
                }
            }

            override fun onFailure(call: Call<List<HistoryResponse>>, t: Throwable) {
                loadingAnimation.visibility = View.GONE
                showEmptyAnimation()
            }
        })
    }

    private fun parseAndFormatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date!!)
    }

    private fun groupByDate(historyData: List<HistoryResponse>): List<Any> {
        Log.i("History", "Grouping data by date")
        val groupedData = historyData.groupBy {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(it.createdAt)!!
            )
        }

        val items = mutableListOf<Any>()
        for (date in groupedData.keys.sortedDescending()) {
            items.add(DateHeader(date))
            groupedData[date]?.forEach { historyResponse ->
                val fruitData = historyResponse.fruitData
                items.add(
                    HistoryItem(
                        parseAndFormatDate(historyResponse.createdAt),
                        fruitData.nama_latin,
                        fruitData.nama_buah,
                        fruitData.icon?.trim() ?: "",
                        fruitData.id_buah,
                        fruitData.desc_buah,
                        fruitData.vitamin_c,
                        fruitData.vitamin_a,
                        fruitData.zat_besi,
                        fruitData.protein_lemak,
                        fruitData.ketahanan_tubuh,
                        fruitData.penglihatan,
                        fruitData.anemia,
                        fruitData.gizi_buruk,
                        fruitData.kandungan_protein,
                        fruitData.kandungan_lemak,
                        fruitData.kandungan_karbohidrat,
                        fruitData.kandungan_energi,
                        fruitData.kandungan_zat_besi,
                        fruitData.kandungan_vitamin_a,
                        fruitData.kandungan_vitamin_c,
                        fruitData.kandungan_folat,
                        fruitData.kandungan_kalsium,
                        fruitData.kandungan_seng,
                        fruitData.Berat_Buah_PerGizi ?: "",
                        fruitData.nilai_gizi_Kalori,
                        fruitData.funfact,
                        fruitData.faq ?: ""
                    )
                )
            }
        }
        Log.i("History", "Grouped items: $items")
        return items
    }

    private fun displayHistoryData(items: List<Any>) {
        Log.i("History", "Displaying history data")
        adapter = HistoryAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.visibility = View.VISIBLE
        emptyStateContainer.visibility = View.GONE
    }

    private fun showEmptyAnimation() {
        Log.i("History", "Showing empty animation")
        emptyStateContainer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }
}
