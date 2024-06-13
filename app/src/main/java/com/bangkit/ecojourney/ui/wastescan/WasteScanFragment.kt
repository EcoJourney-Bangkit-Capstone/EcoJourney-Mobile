package com.bangkit.ecojourney.ui.wastescan

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.bangkit.ecojourney.R
import com.bangkit.ecojourney.adapter.ScanResultAdapter
import com.bangkit.ecojourney.data.ScanResult
import com.bangkit.ecojourney.databinding.FragmentWasteScanBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class WasteScanFragment : Fragment() {
    private lateinit var binding: FragmentWasteScanBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWasteScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.captureImage.setOnClickListener { takePhoto() }
        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()
        return root
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

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
                    requireActivity(),
                    "Failed to show camera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(ContextCompat.getMainExecutor(requireActivity()), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = imageToBitmap(image)
                showBottomSheet(bitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(
                    requireActivity(),
                    "Failed to take photo.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
            }
        })
    }

    private fun imageToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val rotationDegrees = image.imageInfo.rotationDegrees
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // Rotate the bitmap if necessary
        return when (rotationDegrees) {
            90 -> rotateBitmap(bitmap, 90f)
            180 -> rotateBitmap(bitmap, 180f)
            270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun showBottomSheet(bitmap: Bitmap) {
        val dialog = BottomSheetDialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_scan_result)

        dialog.show()
        setDialogBehaviour(dialog)

        // TODO: DONT FORGET TO DELETE THIS WHEN REAL IMPLEMENTATION IS DONE
        val dummyScanResults = listOf(
            ScanResult("Product 1", 2),
            ScanResult("Product 2", 2),
            ScanResult("Product 3", 2),
            ScanResult("Product 4", 2),
        )

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvScanResults)
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ScanResultAdapter(dummyScanResults) { position ->
            val scanResult = dummyScanResults[position]
            onItemClicked(scanResult)
        }
        recyclerView?.adapter = adapter
    }

    private fun onItemClicked(scanResult: ScanResult) {
        val dialog = BottomSheetDialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_recommended_article)

        dialog.findViewById<TextView>(R.id.wasteType)?.text = scanResult.title

        dialog.show()
        setDialogBehaviour(dialog)

    }

    private fun setDialogBehaviour(dialog: BottomSheetDialog) {
        dialog.behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            isDraggable = true
        }

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraActivity"
    }
}