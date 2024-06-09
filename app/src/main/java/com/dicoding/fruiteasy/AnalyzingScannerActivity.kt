package com.dicoding.fruiteasy

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.InputStream

@Suppress("DEPRECATION")
class AnalyzingScannerActivity : AppCompatActivity() {

    private lateinit var capturedImageViewBg: ImageView
    private lateinit var capturedImageView: ImageView
    private lateinit var analyzingImage: TextView
    private lateinit var detectingLeaves: TextView
    private lateinit var identifyingPlant: TextView

    private lateinit var loadingAnimation1: LottieAnimationView
    private lateinit var loadingAnimation2: LottieAnimationView
    private lateinit var loadingAnimation3: LottieAnimationView
    private lateinit var scannerLine: LottieAnimationView

    private val handler = Handler(Looper.getMainLooper())
    private var currentStep = 0

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyzing_scanner)

        supportActionBar?.hide()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        capturedImageViewBg = findViewById(R.id.captured_image)
        capturedImageView = findViewById(R.id.scanner_overlay)
        analyzingImage = findViewById(R.id.analyzing_image)
        detectingLeaves = findViewById(R.id.detecting_leaves)
        identifyingPlant = findViewById(R.id.identifying_plant)
        scannerLine = findViewById(R.id.scanner_line)

        loadingAnimation1 = findViewById(R.id.loading_1)
        loadingAnimation2 = findViewById(R.id.loading_2)
        loadingAnimation3 = findViewById(R.id.loading_3)

        // Load the captured image from the previous activity
        currentImageUri = intent.getStringExtra(EXTRA_CAMERAX_IMAGE)?.toUri()
        currentImageUri?.let { uri ->
            val bitmap = loadBitmapFromUri(uri)
            bitmap?.let {
                val rotatedBitmap = rotateBitmapIfRequired(it, uri)

                // Wait until the ImageView is measured to get its width
                capturedImageView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        capturedImageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                        capturedImageViewBg.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        val scaledBitmap = scaleBitmapToWidth(rotatedBitmap, capturedImageView.width)
                        capturedImageView.setImageBitmap(scaledBitmap)
                        capturedImageViewBg.setImageBitmap(scaledBitmap)
                        Glide.with(this@AnalyzingScannerActivity)
                            .load(rotatedBitmap)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(15,2)))
                            .into(capturedImageViewBg)
                        startScannerAnimation()
                    }
                })
            }
        }

        startLoadingAnimation()
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun rotateBitmapIfRequired(bitmap: Bitmap, uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri) ?: return bitmap
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
        inputStream.close()
        return rotatedBitmap
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun scaleBitmapToWidth(bitmap: Bitmap, targetWidth: Int): Bitmap {
        val aspectRatio = bitmap.height.toFloat() / bitmap.width.toFloat()
        val targetHeight = (targetWidth * aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun startScannerAnimation() {
        // Start the Lottie animation for the scanner line
        scannerLine.playAnimation()
    }

    private fun startLoadingAnimation() {
        val steps = listOf(analyzingImage, detectingLeaves, identifyingPlant)
        val animations = listOf(loadingAnimation1, loadingAnimation2, loadingAnimation3)
        val runnable = object : Runnable {
            override fun run() {
                if (currentStep < steps.size) {
                    animations[currentStep].visibility = View.VISIBLE
                    handler.postDelayed({
                        animations[currentStep].visibility = View.INVISIBLE
                        steps[currentStep].setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0)
                        currentStep++
                        handler.post(this)
                    }, 2000)
                } else {
                    // All steps are completed, start the new activity
                    val intent = Intent(this@AnalyzingScannerActivity, DetailInformationScannerActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
    }
}