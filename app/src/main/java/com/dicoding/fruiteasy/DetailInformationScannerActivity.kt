package com.dicoding.fruiteasy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.fruiteasy.databinding.ActivityDetailInformationScannerBinding
import com.dicoding.fruiteasy.ui.scanner.ScannerFragment
import jp.wasabeef.glide.transformations.BlurTransformation

class DetailInformationScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailInformationScannerBinding
    private lateinit var bgBlur: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailInformationScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bgBlur = findViewById(R.id.bg_blur)

        // Set data for views
//        binding.fruitImage.setImageResource(R.drawable.apples_bg_layout)
        // URL gambar
        val imageUrl = "https://drive.google.com/uc?export=view&id=1MUd0xbLobXMdWs0TMUGPbrfA17QjLZql"

        // Muat gambar menggunakan Glide
        Glide.with(this)
            .load(imageUrl)
            .into(binding.fruitImage)


        // Muat gambar latar belakang dari drawable dan terapkan blur
        Glide.with(this)
            .load(R.drawable.overlay_background)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(15, 2)))
            .into(bgBlur)


        binding.fruitType.text = "Fruit"
        binding.latinName.text = "Malus domestica Borkh"
        binding.weight.text = "182g"
        binding.nutritionValue.text = "Nutrition Value"
        binding.calories.text = "94.6 kcal"

        binding.servingSizeTitle.text = "Serving size"
        binding.servingSize.text = "182g"
        binding.latinNameTitle.text = "Nama Latin"
        binding.latinNameValue.text = "Malus domestica Borkh"
        binding.localNameTitle.text = "Nama Lokal"
        binding.localNameValue.text = "Buah Apel"
        binding.mainBenefitTitle.text = "Manfaat Utama"
        binding.mainBenefitValue.text = "Buah apel memiliki banyak manfaat utama bagi kesehatan. Kaya akan serat dan vitamin C, apel membantu meningkatkan sistem kekebalan tubuh dan pencernaan. Antioksidan dalam apel dapat mengurangi risiko penyakit kronis seperti kanker dan penyakit jantung. Selain itu, apel juga membantu mengontrol berat badan dan menjaga kesehatan gigi. Dengan mengonsumsi apel secara rutin, Anda dapat memperoleh manfaat kesehatan yang optimal."
        binding.nutritionalInfoTitle.text = "Zat Gizi Utama"
        binding.nutritionalPotentialTitle.text = "Potensi untuk Gizi"
        binding.nutritionDetailsTitle.text = "Gizi"

        // Example of setting nutrition details
        binding.nutritionCarbohydrates.text = "Karbohidrat 25.1g"
        binding.nutritionCalcium.text = "Kalsium 10.9mg"
        binding.nutritionMagnesium.text = "Magnesium 9.1mg"
        binding.nutritionVitaminC.text = "Vitamin C 8.37mg"
        binding.nutritionFolate.text = "Folat (B9) 5.4mcg"
        binding.nutritionFiber.text = "Serat 4.37g"
        binding.nutritionWater.text = "Air 156g"

        // Setting potential values
        binding.potentialImmunity.text = "Ketahanan Tubuh"
        binding.potentialVision.text = "Penglihatan"
        binding.potentialAnemia.text = "Anemia"
        binding.potentialBadNutrition.text = "Gizi Buruk"
        binding.potentialImmunityValue.text = "Medium"
        binding.potentialVisionValue.text = "Kurang"
        binding.potentialAnemiaValue.text = "Kurang"
        binding.potentialBadNutritionValue.text = "Kurang"

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