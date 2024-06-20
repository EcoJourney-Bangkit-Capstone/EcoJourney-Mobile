package com.bangkit.ecojourney.ui.wastescan

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
import com.bangkit.ecojourney.databinding.FragmentWasteScanBinding
import com.bangkit.ecojourney.ui.ViewModelFactory
import com.bangkit.ecojourney.utils.ObjectDetectorHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.tensorflow.lite.task.vision.detector.Detection
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.LinkedList
import java.util.UUID
import java.util.concurrent.ExecutorService
import androidx.camera.core.ImageProxy
import androidx.camera.core.ImageProxy.PlaneProxy
import androidx.navigation.fragment.findNavController
import com.bangkit.ecojourney.ui.article.ArticleFragment
import com.bangkit.ecojourney.ui.article.DetailArticleFragment
import com.bangkit.ecojourney.utils.DateConverter
import com.bangkit.ecojourney.utils.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class WasteScanFragment : Fragment(), ObjectDetectorHelper.DetectorListener {
    private val viewModel: WasteScanViewModel by viewModels<WasteScanViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var binding: FragmentWasteScanBinding

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var bitmapBuffer: Bitmap
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    var wasteTypeScanned: MutableList<Detection>? = null
    var wasteTypeClassified: List<Classifications>? = null

    private var currentImageUri: Uri? = null
    private lateinit var surfaceTexture: SurfaceTexture
    private lateinit var imageProxy: ImageProxy

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWasteScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.captureImage.setOnClickListener { takePhoto() }

        binding.galleryImage.setOnClickListener { startGallery() }

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

    override fun onResume() {
        super.onResume()
        initializeObjectDetector()
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
                val setOfWasteType = HashSet<String>()

                wasteTypeScanned?.let { detections ->
                    for (detection in detections) {
                        for (category in detection.categories) {
                            setOfWasteType.add(category.label)
                        }
                    }

                    val listOfWasteScanned = setOfWasteType.map { it }

                    if (listOfWasteScanned.isNotEmpty()) {
                        val imageFile = bitmapToFile(bitmap)
                        viewModel.postScan(imageFile, listOfWasteScanned)
                        viewModel.scanResponse.observe(viewLifecycleOwner) { scanResponse ->
                            Log.d("SCAN RESPONSE", scanResponse.toString())
                        }
                    }

                    showBottomSheet(listOfWasteScanned)
                } ?: run {
                    Log.e(TAG, "wasteTypeScanned is null")
                    Toast.makeText(requireContext(), "No waste type detected", Toast.LENGTH_SHORT).show()
                }

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


    private fun bitmapToFile(bitmap: Bitmap): File {
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}..jpg")
        val stream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,stream)
        stream.flush()
        stream.close()
        return file
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

    private fun showBottomSheet(listOfWasteScanned: List<String>) {
        val dialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_scan_result)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvScanResults)
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ScanResultAdapter(listOfWasteScanned) { position ->
            val wasteScanned = listOfWasteScanned[position]
            onItemClicked(wasteScanned)
        }
        recyclerView?.adapter = adapter

        setDialogBehaviour(dialog)
        dialog.show()
    }

    private fun onItemClicked(wasteScanned: String) {
        val dialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_recommended_article)

        dialog.findViewById<TextView>(R.id.wasteType)?.text = wasteScanned

        viewModel.searchArticle(wasteScanned)

        viewModel.articles.observe(viewLifecycleOwner) { response ->
            if (!response.error) {
                val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvRecommendedArticles)
                recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
                val adapter = response.details?.let {
                    ArticleRecommendAdapter(it.articles) { it ->
                        val navController = findNavController()
                        val bundle = Bundle().apply {
                            putString(DetailArticleFragment.EXTRA_TITLE, it.title)
                            putString(DetailArticleFragment.EXTRA_PUBLISHER, it.publisher)
                            putString(
                                DetailArticleFragment.EXTRA_DATE,
                                it.datePublished?.let { it1 ->
                                    DateConverter.formatDate(
                                        it1
                                    )
                                })
                            putString(DetailArticleFragment.EXTRA_CONTENT, it.content)
                            putString(DetailArticleFragment.EXTRA_IMAGE, it.imgUrl)
                        }
                        Log.d(
                            TAG,
                            "title: ${it.title}, publisher: ${it.publisher}, date: ${it.datePublished}, content: ${it.content}, image: ${it.imgUrl}"
                        )
                        navController.navigate(
                            R.id.action_navigation_scan_to_detailArticleFragment,
                            bundle
                        )
                    }
                }
                recyclerView?.adapter = adapter
            } else {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Oops!")
                    setMessage(response.message)
                    setPositiveButton("Back", null)
                    create()
                    show()
                }
            }
        }

        setDialogBehaviour(dialog)
        dialog.show()
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

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            classifyImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun classifyImage() {
        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    wasteTypeClassified = results
                }

                override fun onError(error: String) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        currentImageUri?.let { imageClassifierHelper.classifyStaticImage(it) }

        val contentResolver = requireContext().contentResolver
        val inputStream = currentImageUri?.let { contentResolver.openInputStream(it) }
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val setOfWasteType = HashSet<String>()

        for (detection in wasteTypeClassified!!) {
            for (category in detection.categories) {
                setOfWasteType.add(category.label.trimEnd())
            }
        }

        val listOfWasteScanned = setOfWasteType.map { it }

        if (listOfWasteScanned.isNotEmpty()) {
            val image = bitmapToFile(bitmap)
            viewModel.postScan(image, listOfWasteScanned)
            viewModel.scanResponse.observe(viewLifecycleOwner) { scanResponse ->
                Log.d("SCAN RESPONSE", scanResponse.toString())
            }
        }

        showBottomSheet(listOfWasteScanned)
    }


    companion object {
        private const val TAG = "CameraActivity"
    }
}