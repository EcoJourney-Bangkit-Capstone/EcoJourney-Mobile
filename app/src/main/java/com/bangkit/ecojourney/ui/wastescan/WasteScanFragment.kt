package com.bangkit.ecojourney.ui.wastescan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import com.bangkit.ecojourney.R
import com.bangkit.ecojourney.adapter.ScanResultAdapter
import com.bangkit.ecojourney.data.ScanResult
import com.bangkit.ecojourney.databinding.FragmentWasteScanBinding
import com.bangkit.ecojourney.utils.ObjectDetectorHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.NumberFormat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.util.concurrent.ExecutorService

class WasteScanFragment : Fragment() {
    private lateinit var binding: FragmentWasteScanBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWasteScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.captureImage.setOnClickListener { takePhoto() }
        cameraExecutor = Executors.newSingleThreadExecutor()

        hideSystemUI()

        return root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        startCamera()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun startCamera() {
        objectDetectorHelper = ObjectDetectorHelper(
            context = context,
            detectorListener = object : ObjectDetectorHelper.DetectorListener {
                override fun onError(error: String) {
                    activity?.runOnUiThread {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(
                    results: MutableList<Detection>?,
                    inferenceTime: Long,
                    imageHeight: Int,
                    imageWidth: Int
                ) {
                    activity?.runOnUiThread {
                        results?.let {
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                println(it)
                                binding.overlay.setResults(
                                    results, imageHeight, imageWidth
                                )

                                val builder = StringBuilder()
                                for (result in results) {
                                    val displayResult =
                                        "${result.categories[0].label} " + NumberFormat.getPercentInstance()
                                            .format(result.categories[0].score).trim()
                                    builder.append("$displayResult \n")
                                }

                                binding.tvResult.text = builder.toString()
                                binding.tvInferenceTime.text = "$inferenceTime ms"
                            } else {
                                binding.overlay.clear()
                                binding.tvResult.text = ""
                                binding.tvInferenceTime.text = ""
                            }
                        }

                        // Force a redraw
                        binding.overlay.invalidate()
                    }
                }
            }
        )

        val cameraProviderFuture = context?.let { ProcessCameraProvider.getInstance(it) }

        cameraProviderFuture?.addListener({
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
            val imageAnalyzer = ImageAnalysis.Builder().setResolutionSelector(resolutionSelector)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build()
            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                objectDetectorHelper.detectObject(image)
            }

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val metrics = activity?.windowManager?.currentWindowMetrics?.bounds
            val screenAspectRatio = metrics?.let { aspectRatio(it.width(), metrics.height()) }
            val rotation = binding.viewFinder.display.rotation

            val preview = screenAspectRatio?.let {
                Preview.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .setTargetRotation(rotation)
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    requireActivity(), "Failed to start camera.", Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - 4.0 / 3.0) <= abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
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
        dialog.findViewById<ImageView>(R.id.scanImage)?.setImageBitmap(bitmap)

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

    private fun hideSystemUI() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        activity?.actionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraActivity"
    }
}