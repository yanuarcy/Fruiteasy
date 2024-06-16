package com.dicoding.fruiteasy

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class HistoryScanningActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
//    private lateinit var emptyMessage: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_scanning)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val emptyMessage : TextView = findViewById(R.id.emptyMessage)

        val items = loadHistoryData()
        if (items.isEmpty()) {
            emptyMessage.visibility = View.VISIBLE
        } else {
            emptyMessage.visibility = View.GONE

            adapter = HistoryAdapter(items)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }

    }

    private fun loadHistoryData(): List<Any> {
        val sharedPreferences = getSharedPreferences("Scanning", Context.MODE_PRIVATE)
        val historyDataString = sharedPreferences.getString("historyData", "{}") ?: "{}"
        val historyData = JSONObject(historyDataString)

        val items = mutableListOf<Any>()

        // Get keys and sort them by date
        val dates = historyData.keys().asSequence().sortedByDescending { dateString ->
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(dateString)
        }

        for (date in dates) {
            items.add(DateHeader(date))
            val historyItems = historyData.getJSONArray(date)
            for (i in 0 until historyItems.length()) {
                val responseBody = historyItems.getString(i)
                items.add(HistoryItem("Malus domestica Borkh", responseBody, R.drawable.icon_history)) // Adjust this line as per your data structure
            }
        }

        return items
    }
}