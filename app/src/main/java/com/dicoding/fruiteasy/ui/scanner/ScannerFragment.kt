@file:Suppress("DEPRECATION")

package com.dicoding.fruiteasy.ui.scanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dicoding.fruiteasy.databinding.FragmentScannerBinding

@Suppress("DEPRECATION")
class ScannerFragment : Fragment(), SurfaceHolder.Callback {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder
    private var camera: Camera? = null
    private lateinit var identifyButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var galleryButton: ImageView
    private val scannerViewModel: ScannerViewModel by activityViewModels()

    private val REQUEST_CAMERA_PERMISSION = 100
    private val REQUEST_GALLERY_IMAGE = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        surfaceView = binding.cameraPreview
        identifyButton = binding.identifyButton
        backButton = binding.backButton
        galleryButton = binding.galleryButton

        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)

        identifyButton.setOnClickListener {
            // Handle Identify button click
            Toast.makeText(requireContext(), "Identify clicked", Toast.LENGTH_SHORT).show()
        }

        backButton.setOnClickListener {
            // Handle Back button click
            requireActivity().onBackPressed()
        }

        galleryButton.setOnClickListener {
            // Handle Gallery button click
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_GALLERY_IMAGE)
        }

//        scannerViewModel.setBottomNavVisible(false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scannerViewModel.hideBottomNav()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Handle surface changes if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopCamera()
    }

    private fun startCamera() {
        camera = Camera.open()
        camera?.apply {
            setPreviewDisplay(surfaceHolder)
            setCameraDisplayOrientation()
            startPreview()
        }
    }

    private fun stopCamera() {
        camera?.apply {
            stopPreview()
            release()
        }
        camera = null
    }

    private fun setCameraDisplayOrientation() {
        val rotation = requireActivity().windowManager.defaultDisplay.rotation
        val degrees = when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }

        val info = Camera.CameraInfo()
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info)

        val result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            val tempResult = (info.orientation + degrees) % 360
            result = (360 - tempResult) % 360  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }

        camera?.setDisplayOrientation(result)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            // Handle the selected image from the gallery
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scannerViewModel.showBottomNav()
    }
}
