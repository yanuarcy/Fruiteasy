package com.dicoding.fruiteasy

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.dicoding.fruiteasy.databinding.ActivityHistoryScanningDetailBinding

@Suppress("DEPRECATION")
class HistoryScanningDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryScanningDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryScanningDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val backButton = findViewById<ImageView>(R.id.Back_button)
        backButton.setOnClickListener {
            finish()
        }

        val historyItem = intent.getParcelableExtra<HistoryItem>("HISTORY_ITEM")

        if (historyItem != null) {
            // Display data in your views using binding
            binding.tvScientificName.text = historyItem.title
            binding.tvLocalName.text = historyItem.subtitle
            binding.tvDate.text = historyItem.tglScan // Jika Anda memiliki data tanggal, sesuaikan di sini

            Glide.with(this).load(historyItem.imageUrl).into(binding.imageIcon)

            binding.servingSize.text = historyItem.beratBuahPerGizi
            binding.tvBenefits.text = historyItem.descBuah

            binding.readMoreLayout.setOnClickListener {
                if (binding.readMore.text == "Read More") {
                    binding.tvBenefits.maxLines = Integer.MAX_VALUE
                    binding.tvBenefits.ellipsize = null
                    binding.readMore.text = "Read Less"
                    binding.chevronIcon.setImageResource(R.drawable.ic_chevron_up_24)
                } else {
                    binding.tvBenefits.maxLines = 3
                    binding.tvBenefits.ellipsize = TextUtils.TruncateAt.END
                    binding.readMore.text = "Read More"
                    binding.chevronIcon.setImageResource(R.drawable.ic_chevron_down_24)
                }
            }

            // Main Nutrients
            binding.nutrientVitaminCValue.text = historyItem.vitaminC
            binding.nutrientVitaminAValue.text = historyItem.vitaminA
            binding.nutrientIronValue.text = historyItem.zatBesi
            binding.nutrientProteinValue.text = historyItem.proteinLemak

            // Potential Benefits
            binding.potentialImmunityValue.text = historyItem.ketahananTubuh
            binding.potentialVisionValue.text = historyItem.penglihatan
            binding.potentialAnemiaValue.text = historyItem.anemia
            binding.potentialBadNutritionValue.text = historyItem.giziBuruk

            // Additional Nutrients
            binding.nutritionCarbohydratesValue.text = historyItem.kandunganKarbohidrat
            binding.nutritionCalciumValue.text = historyItem.kandunganKalsium
            binding.nutritionMagnesiumValue.text = historyItem.kandunganEnergi // Sesuaikan dengan data yang benar
            binding.nutritionVitaminCValue.text = historyItem.kandunganVitaminC
            binding.nutritionFolateValue.text = historyItem.kandunganFolat
            binding.nutritionFiberValue.text = historyItem.nilaiGiziKalori // Sesuaikan dengan data yang benar
            binding.nutritionWaterValue.text = historyItem.kandunganSeng // Sesuaikan dengan data yang benar

            // Fun Facts
            binding.funFactsHeader.text = "Fun Facts"
            displayFunFacts(historyItem.funfact)
        }
    }

    private fun displayFunFacts(funFacts: String) {
        val funFactsContainer = binding.funFactsContainer
        val funFactsArray = funFacts.split(";")
        for (fact in funFactsArray) {
            val factRowLayout = LinearLayout(this)
            factRowLayout.orientation = LinearLayout.HORIZONTAL
            factRowLayout.setPadding(0, 14, 0, 14)

            val icon = ImageView(this)
            icon.setImageResource(R.drawable.icon_fun_fact) // Sesuaikan dengan icon yang Anda miliki
            icon.layoutParams = LinearLayout.LayoutParams(54.dpToPx(), 54.dpToPx())
            icon.setBackgroundResource(R.drawable.border_funfact)

            val factTextView = TextView(this)
            factTextView.text = fact.trim()
            factTextView.textSize = 18f
            factTextView.setTextColor(ContextCompat.getColor(this, R.color.text_funfact))
            val typeface = ResourcesCompat.getFont(this, R.font.poppins_regular)
            factTextView.typeface = typeface

            // Menambahkan margin left pada TextView
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(16.dpToPx(), 0, 0, 0) // 16dp margin left, bisa disesuaikan
            factTextView.layoutParams = params

            factRowLayout.addView(icon)
            factRowLayout.addView(factTextView)

            funFactsContainer.addView(factRowLayout)
        }
    }

    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}
