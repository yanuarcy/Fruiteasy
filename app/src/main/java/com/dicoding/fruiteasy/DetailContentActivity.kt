package com.dicoding.fruiteasy

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.fruiteasy.databinding.ActivityDetailContentBinding

@Suppress("DEPRECATION")
class DetailContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val image = intent.getStringExtra("image")
        val name = intent.getStringExtra("name")
        val benefit = intent.getStringExtra("benefit")
        val ingredients = intent.getStringExtra("ingredients")
        val steps = intent.getStringExtra("steps")

        binding.BackButton.setOnClickListener {
            finish()
        }

        Glide.with(this).load(image).into(binding.imageContent)
        binding.tvContentTitle.text = name
        binding.tvContentBenefit.text = benefit
        binding.tvContentIngredients.text = formatAsList(ingredients)
        binding.tvContentSteps.text = formatAsList(steps)
    }

    private fun formatAsList(text: String?): String {
        if (text.isNullOrEmpty()) return ""

        // Split text by <br> and remove empty items
        val items = text.split("<br>").filter { it.isNotBlank() }

        // Prepare formatted text with numbering
        val formattedText = StringBuilder()
        for ((index, item) in items.withIndex()) {
            formattedText.append("${index + 1}. $item\n")
        }

        return formattedText.toString().trim()
    }
}
