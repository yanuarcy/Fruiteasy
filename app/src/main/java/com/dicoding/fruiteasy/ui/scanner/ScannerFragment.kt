package com.dicoding.fruiteasy.ui.scanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.fruiteasy.AnalyzingScannerActivity
import com.dicoding.fruiteasy.AnalyzingScannerActivity.Companion.CAMERAX_RESULT
import com.dicoding.fruiteasy.api.RetrofitClient
import com.dicoding.fruiteasy.createCustomTempFile
import com.dicoding.fruiteasy.databinding.FragmentScannerBinding
import com.dicoding.fruiteasy.getImageUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var currentImageUri: Uri? = null
    private val scannerViewModel: ScannerViewModel by activityViewModels()


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.backButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.identifyButton.setOnClickListener { takePhoto() }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scannerViewModel.hideBottomNav()
    }


    override fun onResume() {
        super.onResume()
//        hideSystemUI()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(requireContext())

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    currentImageUri = savedUri
                    val msg = "Photo saved"

                    Toast.makeText(requireContext(), "$msg $savedUri", Toast.LENGTH_LONG).show()
                    // Call function to upload the image
//                    uploadImage(photoFile)
                    val intent = Intent(requireContext(), AnalyzingScannerActivity::class.java)
                    intent.putExtra(AnalyzingScannerActivity.EXTRA_CAMERAX_IMAGE, output.savedUri.toString())
                    startActivity(intent)
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onError: ${exc.message}")
                }
            }
        )
    }

    private fun uploadImage(photoFile: File) {
        lifecycleScope.launch {
            try {
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), photoFile)
                val body = MultipartBody.Part.createFormData("file", photoFile.name, requestFile)

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.uploadImage(body).execute()
                }

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.i(TAG, "Upload successful: $responseBody")
                    Toast.makeText(requireContext(), "Upload successful: $responseBody", Toast.LENGTH_LONG).show()
                    // Store responseBody in SharedPreferences
                    val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("responseBody", responseBody).apply()

                    val intent = Intent(requireContext(), AnalyzingScannerActivity::class.java)
                    intent.putExtra(AnalyzingScannerActivity.EXTRA_CAMERAX_IMAGE, currentImageUri.toString())
                    startActivity(intent)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(requireContext(), "Upload failed: ${response.message()}", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Upload failed: ${response.message()} - $errorBody")
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "uploadImage: ${e.message}", e)
            }
        }
    }

//    private fun hideSystemUI() {
//        @Suppress("DEPRECATION")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }
//        supportActionBar?.hide()
//    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(requireContext()) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
//            val file = uriToFile(uri, requireContext())
//            uploadImage(file)
            Log.i("Photo Picker", "Media selected : $currentImageUri")
            val intent = Intent(requireContext(), AnalyzingScannerActivity::class.java)
            intent.putExtra(AnalyzingScannerActivity.EXTRA_CAMERAX_IMAGE, currentImageUri.toString())
            startActivity(intent)
        } else {
            Log.d("Photo Picker", "No media selected")
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

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.scannerOverlay.setImageURI(it)
        }
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
        scannerViewModel.hideBottomNav()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
        scannerViewModel.showBottomNav()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
        private const val TAG = "ScannerFragment"
    }
}


