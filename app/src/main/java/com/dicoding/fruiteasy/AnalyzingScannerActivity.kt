package com.dicoding.fruiteasy

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.model.LoginRequest
import com.dicoding.fruiteasy.model.Predict
import com.dicoding.fruiteasy.model.PredictHistoryRequest
import com.google.gson.JsonParser
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class AnalyzingScannerActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
        const val EXTRA_RESPONSE_BODY = "extra_response_body"
    }

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
    private var uploadDuration: Long = 0L

    private var currentImageUri: Uri? = null
    private val uploadCompletion = CompletableDeferred<Unit>()

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

//        val responseBody = intent.getStringExtra(EXTRA_RESPONSE_BODY)
//        Log.i(TAG, "Received response body on Analyzing: $responseBody")

        // Retrieve responseBody from SharedPreferences
//        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
//        val responseBody = sharedPreferences.getString("responseBody", null)
//
//        // Use cameraXImage and responseBody as needed
//        Log.i(TAG, "Received response body on Analyzing: $responseBody")

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
            uploadImage(uriToFile(uri, this))
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
                    }, 8500)
                } else {
                    // All steps are completed, start the new activity

//                    val intent = Intent(this@AnalyzingScannerActivity, DetailInformationScannerActivity::class.java)
////                    intent.putExtra(DetailInformationScannerActivity.EXTRA_RESPONSE_BODY, responseBody)
//                    startActivity(intent)

                    lifecycleScope.launch {
                        // Wait for the upload to complete before moving to the next activity
                        uploadCompletion.await()
                        // All steps are completed, start the new activity
                        val intent = Intent(this@AnalyzingScannerActivity, DetailInformationScannerActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
        handler.post(runnable)
    }

    private fun uploadImage(photoFile: File) {
        lifecycleScope.launch {
            try {
                val startTime = System.currentTimeMillis()
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), photoFile)
                val body = MultipartBody.Part.createFormData("image", photoFile.name, requestFile)

                val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
                val getUserLocalId = sharedPref.getString("uid", null)
                val userLocalid = getUserLocalId.toString()
//                val userLocalIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(),
//                    userLocalId.toString()
//                )

//                val userLocalIdBody = Predict(userLocalId.toString())

                Log.i("UploadImage", "This is UID from Login: $userLocalid")
                Log.i("UploadImage", "This is UID with Predict Class: $userLocalid")

                Log.i("UploadImage", "This is value from body Image: $body")


                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.uploadImage(body).execute()
                }

                val endTime = System.currentTimeMillis() // End time measurement
                uploadDuration = endTime - startTime // Calculate duration
                Log.i(TAG, "Upload duration: $uploadDuration ms")

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.i(TAG, "Upload successful: $responseBody")
                    Toast.makeText(this@AnalyzingScannerActivity, "Upload successful", Toast.LENGTH_LONG).show()
                    // Store responseBody in SharedPreferences
                    // Get current date
//                    val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

                    // Get existing data from SharedPreferences
                    val sharedPreferences = getSharedPreferences("Scanning", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("historyData", responseBody).apply()
//
                    // Extract fruitName from responseBody
                    val jsonParser = JsonParser()
                    val jsonObject = jsonParser.parse(responseBody).asJsonObject
                    val fruitName = jsonObject.get("nama_buah").asString

                    Log.i("UploadHistory", "This Is Fruit Name from Data Object: $fruitName")

                    // Prepare data for /Predict/history API call
//                    val predictHistoryData = PredictHistoryRequest(
//                        userLocalId = userLocalId ?: "",
//                        fruitName = fruitName
//                    )

                    val predictHistoryData = PredictHistoryRequest(userLocalid, fruitName)

                    // Post to /Predict/history
                    val historyResponse = withContext(Dispatchers.IO) {
                        RetrofitClient.instance.postPredictHistory(predictHistoryData).execute()
                    }

                    if (historyResponse.isSuccessful) {
                        Log.i(TAG, "Predict history post successful")
                    } else {
                        Log.e(TAG, "Predict history post failed: ${historyResponse.message()}")
                    }

                    // Signal the completion of the upload process
                    uploadCompletion.complete(Unit)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@AnalyzingScannerActivity, "Upload failed", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Upload failed: ${response.message()} - $errorBody")
                    // Signal the completion to avoid waiting indefinitely
                    uploadCompletion.complete(Unit)
                }
            } catch (e: Exception) {
                Toast.makeText(this@AnalyzingScannerActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "uploadImage: ${e.message}", e)
                // Signal the completion to avoid waiting indefinitely
                uploadCompletion.complete(Unit)
            }
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val tempFile = createCustomTempFile(context)
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }


}