@file:Suppress("DEPRECATION")

package com.dicoding.fruiteasy

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.TextUtils
import com.bumptech.glide.request.transition.Transition
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginLeft
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.dicoding.fruiteasy.databinding.ActivityDetailInformationScannerBinding
import com.dicoding.fruiteasy.ui.scanner.ScannerFragment
import jp.wasabeef.blurry.Blurry
import jp.wasabeef.glide.transformations.BlurTransformation
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class DetailInformationScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailInformationScannerBinding
    private lateinit var bgBlur: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailInformationScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bgBlur = findViewById(R.id.bg_blur)

        // Menunggu layout selesai diatur sebelum mengambil tampilan
        bgBlur.viewTreeObserver.addOnGlobalLayoutListener {
            // Ambil tampilan dari ImageView dan terapkan efek blur
            Blurry.with(this)
                .radius(15) // Atur radius blur
                .sampling(2) // Atur sampling
                .capture(binding.fruitImage)
                .into(bgBlur)
        }




//        // Render drawable shape to a Bitmap
//        val drawable = bgBlur.background
//        val bitmap = drawableToBitmap(drawable, bgBlur.width, bgBlur.height)
////
////        // Use Glide to apply the blur transformation
//        Glide.with(this)
//            .load(BitmapDrawable(resources, bitmap))
//            .apply(RequestOptions.bitmapTransform(BlurTransformation(15, 2))) // Set the blur radius
//            .into(bgBlur)

//        val responseBody = intent.getStringExtra(EXTRA_RESPONSE_BODY)
//
//        // Use cameraXImage and responseBody as needed
//        Log.i(TAG, "Received response body in DetailInformationScannerActivity: $responseBody")
        // Retrieve responseBody from SharedPreferences
        val sharedPreferences = getSharedPreferences("Scanning", Context.MODE_PRIVATE)
        val responseBody = sharedPreferences.getString("historyData", null)

        // Use cameraXImage and responseBody as needed
        Log.i(TAG, "Received response body on Detail: $responseBody")


        // Set data for views
//        binding.fruitImage.setImageResource(R.drawable.apples_bg_layout)
        // URL gambar



        // Muat gambar latar belakang dari drawable dan terapkan blur
//        Glide.with(this)
//            .load(R.drawable.bgblur_layout)
//            .apply(RequestOptions.bitmapTransform(BlurTransformation(15, 2)))
//            .into(bgBlur)

        responseBody?.let {
            try {
                val jsonObject = JSONObject(it)

//                val imageUrl = "https://drive.google.com/uc?export=view&id=1MUd0xbLobXMdWs0TMUGPbrfA17QjLZql"
                val imageUrl = jsonObject.getString("image")

                // Muat gambar menggunakan Glide
                Glide.with(this)
                    .load(imageUrl)
                    .into(binding.fruitImage)

                // Load the image using Glide
                Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(object : CustomTarget<Bitmap>(245, 245) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            // Apply the blur effect
                            Blurry.with(this@DetailInformationScannerActivity)
                                .radius(15)
                                .sampling(2)
                                .from(resource)
                                .into(binding.bgBlur)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // Handle cleanup if necessary
                            binding.bgBlur.setImageDrawable(null)
                        }
                    })

                binding.fruitType.text = jsonObject.getString("nama_buah")
                binding.latinName.text = jsonObject.getString("nama_latin")
                binding.weight.text = jsonObject.getString("Berat Buah PerGizi")
                binding.nutritionValue.text = "Nutrition Value"
                binding.calories.text = jsonObject.getString("kandungan_energi")

                binding.servingSizeTitle.text = "Serving size"
                binding.servingSize.text = jsonObject.getString("Berat Buah PerGizi")
                binding.latinNameTitle.text = "Nama Latin"
                binding.latinNameValue.text = jsonObject.getString("nama_latin")
                binding.localNameTitle.text = "Nama Lokal"
                binding.localNameValue.text = jsonObject.getString("nama_buah")
                binding.mainBenefitTitle.text = "Manfaat Utama"

                binding.readMoreLayout.setOnClickListener {
                    if (binding.readMore.text == "Read More") {
                        binding.mainBenefitValue.maxLines = Integer.MAX_VALUE
                        binding.mainBenefitValue.ellipsize = null
                        binding.readMore.text = "Read Less"
                        binding.chevronIcon.setImageResource(R.drawable.ic_chevron_up_24)
                    } else {
                        binding.mainBenefitValue.maxLines = 3
                        binding.mainBenefitValue.ellipsize = TextUtils.TruncateAt.END
                        binding.readMore.text = "Read More"
                        binding.chevronIcon.setImageResource(R.drawable.ic_chevron_down_24)
                    }
                }

                binding.mainBenefitValue.text = jsonObject.getString("desc_buah")
                binding.nutritionalInfoTitle.text = "Zat Gizi Utama"
                binding.nutritionalPotentialTitle.text = "Potensi untuk Gizi"
                binding.nutritionDetailsTitle.text = "Gizi"

                // Example of setting nutrition details
                binding.nutritionCarbohydratesLabel.text = "Karbohidrat"
                binding.nutritionCalciumLabel.text = "Kalsium"
                binding.nutritionMagnesiumLabel.text = "Magnesium"
                binding.nutritionVitaminCLabel.text = "Vitamin C"
                binding.nutritionFolateLabel.text = "Folat (B9)"
                binding.nutritionFiberLabel.text = "Serat"
                binding.nutritionWaterLabel.text = "Air"

                binding.nutritionCarbohydratesValue.text = jsonObject.getString("kandungan_karbohidrat")
                binding.nutritionCalciumValue.text = jsonObject.getString("kandungan_kalsium")
                binding.nutritionMagnesiumValue.text = jsonObject.getString("kandungan_seng")
                binding.nutritionVitaminCValue.text = jsonObject.getString("kandungan_vitamin_c")
                binding.nutritionFolateValue.text = jsonObject.getString("kandungan_folat")
                binding.nutritionFiberValue.text = "4.37g"  // Assuming fixed value since it's not provided
                binding.nutritionWaterValue.text = "156g"  // Assuming fixed value since it's not provided

                // Setting potential values
                binding.potentialImmunity.text = "Ketahanan Tubuh"
                binding.potentialVision.text = "Penglihatan"
                binding.potentialAnemia.text = "Anemia"
                binding.potentialBadNutrition.text = "Gizi Buruk"
                binding.potentialImmunityValue.text = jsonObject.getString("ketahanan_tubuh")
                binding.potentialVisionValue.text = jsonObject.getString("penglihatan")
                binding.potentialAnemiaValue.text = jsonObject.getString("anemia")
                binding.potentialBadNutritionValue.text = jsonObject.getString("gizi_buruk")

                val funFactsContainer = findViewById<LinearLayout>(R.id.fun_facts_container)
                val funFacts = jsonObject.getString("funfact").trimIndent()

                val funFactsArray = funFacts.split(";")
                for (fact in funFactsArray) {
                    val factRowLayout = LinearLayout(this)
                    factRowLayout.orientation = LinearLayout.HORIZONTAL
                    factRowLayout.setPadding(0, 14, 0, 14)

                    val icon = ImageView(this)
                    icon.setImageResource(R.drawable.icon_fun_fact) // Sesuaikan dengan icon yang Anda miliki
                    icon.layoutParams = LinearLayout.LayoutParams(54.dpToPx(), 54.dpToPx())
//                    icon.setPadding(10, 10, 10, 10)
                    icon.setBackgroundResource(R.drawable.border_funfact)

                    val factTextView = TextView(this)
                    factTextView.text = fact.trim()
                    factTextView.textSize = 18f

                    // Mengatur warna teks
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

            } catch (e: JSONException) {
                Log.e(TAG, "Failed to parse JSON", e)
            }
        }

        // Back button listener
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigateTo", "scannerFragment")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }


    }
}

fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}