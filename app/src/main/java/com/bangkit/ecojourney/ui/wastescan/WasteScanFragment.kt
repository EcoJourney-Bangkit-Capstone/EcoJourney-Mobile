package com.bangkit.ecojourney.ui.wastescan

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import com.bangkit.ecojourney.R
import com.bangkit.ecojourney.adapter.ArticleRecommendAdapter
import com.bangkit.ecojourney.adapter.ScanResultAdapter
import com.bangkit.ecojourney.data.ScanResult
import com.bangkit.ecojourney.data.WasteScanned
import com.bangkit.ecojourney.data.response.ArticleItem
import com.bangkit.ecojourney.databinding.FragmentWasteScanBinding
import com.bangkit.ecojourney.ui.ViewModelFactory
import com.bangkit.ecojourney.utils.ObjectDetectorHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.tensorflow.lite.task.vision.detector.Detection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.concurrent.ExecutorService

class WasteScanFragment : Fragment(), ObjectDetectorHelper.DetectorListener {
    private val viewModel: WasteScanViewModel by viewModels<WasteScanViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var binding: FragmentWasteScanBinding

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private lateinit var bitmapBuffer: Bitmap
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    var wasteTypeScanned: MutableList<Detection>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWasteScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.captureImage.setOnClickListener { takePhoto() }

        return root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeObjectDetector()

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.viewFinder.post {
            setUpCamera()
        }
    }

    private fun initializeObjectDetector() {
        try {
            objectDetectorHelper = ObjectDetectorHelper(
                context = requireContext(),
                objectDetectorListener = this
            )
        } catch (e: Exception) {
            Log.e(TAG, "Object detector initialization failed", e)
            Toast.makeText(requireContext(), "Failed to initialize object detector", Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - makes assumption that we're only using the back camera
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview =
            Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        if (!::bitmapBuffer.isInitialized) {
                            // The image rotation and RGB image buffer are initialized only once
                            // the analyzer has started running
                            bitmapBuffer = Bitmap.createBitmap(
                                image.width,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )
                        }

                        detectObjects(image)
                    }
                }

        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .build()

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer, imageCapture)

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun detectObjects(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to the object detector helper for processing and detection
        objectDetectorHelper.detect(bitmapBuffer, imageRotation)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = binding.viewFinder.display.rotation
    }

    // Update UI after objects have been detected. Extracts original image height/width
    // to scale and place bounding boxes properly through OverlayView
    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
        activity?.runOnUiThread {
            // Pass necessary information to OverlayView for drawing on the canvas
            binding.overlay.setResults(
                results ?: LinkedList<Detection>(),
                imageHeight,
                imageWidth
            )

            wasteTypeScanned = results

            // Force a redraw
            binding.overlay.invalidate()
        }
    }

    override fun onError(error: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(ContextCompat.getMainExecutor(requireActivity()), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = imageToBitmap(image)
                val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                val setOfWasteType = HashSet<String>()

                for (detection in wasteTypeScanned!!) {
                    for (category in detection.categories) {
                        setOfWasteType.add(category.label)
                    }
                }

                val listOfWasteScanned = setOfWasteType.map { WasteScanned(it) }

                val wasteScanResult = ScanResult(listOfWasteScanned, dateTime, bitmap)

                showBottomSheet(wasteScanResult)
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

    private fun showBottomSheet(scanResult: ScanResult) {
        val dialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_scan_result)

        dialog.show()
        setDialogBehaviour(dialog)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvScanResults)
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ScanResultAdapter(scanResult.wasteScanned) { position ->
            val wasteScanned = scanResult.wasteScanned[position]
            onItemClicked(wasteScanned)
        }
        recyclerView?.adapter = adapter
    }

    private fun onItemClicked(wasteScanned: WasteScanned) {
        val dialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_recommended_article)

        dialog.findViewById<TextView>(R.id.wasteType)?.text = wasteScanned.wasteType

        dialog.show()
        setDialogBehaviour(dialog)

        viewModel.getArticles()
        viewModel.articles.observe(viewLifecycleOwner) { articles ->
            val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvRecommendedArticles)
            recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
            val adapter = ArticleRecommendAdapter(articles) { position ->
                val article = articles[position]
            }
            recyclerView?.adapter = adapter
        }
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