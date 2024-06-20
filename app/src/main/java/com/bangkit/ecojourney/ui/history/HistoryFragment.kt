package com.bangkit.ecojourney.ui.history

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.ecojourney.R
import com.bangkit.ecojourney.adapter.ArticleRecommendAdapter
import com.bangkit.ecojourney.adapter.HistoryAdapter
import com.bangkit.ecojourney.adapter.ScanResultAdapter
import com.bangkit.ecojourney.data.ScanResult
import com.bangkit.ecojourney.data.WasteScanned
import com.bangkit.ecojourney.data.response.HistoryItem
import com.bangkit.ecojourney.databinding.FragmentHistoryBinding
import com.bangkit.ecojourney.ui.ViewModelFactory
import com.bangkit.ecojourney.ui.article.DetailArticleFragment
import com.bangkit.ecojourney.ui.wastescan.WasteScanFragment
import com.bangkit.ecojourney.ui.wastescan.WasteScanViewModel
import com.bangkit.ecojourney.utils.DateConverter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HistoryFragment : Fragment() {
    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val scanWasteViewModel by viewModels<WasteScanViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getHistory()
        viewModel.history.observe(viewLifecycleOwner) { history ->
            binding.rvScanHistory.layoutManager = LinearLayoutManager(requireActivity())
            val adapter = HistoryAdapter(history) { position ->
                val scanResult = history[position]
                onItemClicked(scanResult)
            }
            binding.rvScanHistory.adapter = adapter
        }
    }

    private fun onItemClicked(historyItem: HistoryItem) {
        val dialog = BottomSheetDialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_history_detail)

        dialog.findViewById<TextView>(R.id.historyScanDate)?.text = formatDateTime(historyItem.timestamp)

        dialog.findViewById<ImageView>(R.id.scanImage)?.let {
            Glide.with(requireContext())
                .load(historyItem.imageURL)
                .override(Target.SIZE_ORIGINAL)
                .into(it)
        }

        setDialogBehaviour(dialog)
        dialog.show()

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvScanResults)
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ScanResultAdapter(historyItem.type) { position ->
            val wasteScanned = historyItem.type[position]
            Log.d(TAG, "wasteScanned: $wasteScanned")
            onWasteTypeClicked(wasteScanned)
        }
        recyclerView?.adapter = adapter

    }

    private fun formatDateTime(input: String): String? {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        inputFormatter.timeZone = TimeZone.getTimeZone("UTC")

        val date: Date? = inputFormatter.parse(input)
        val outputFormatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH)

        return date?.let { outputFormatter.format(it) }
    }

    private fun setDialogBehaviour(dialog: BottomSheetDialog) {
        dialog.behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            isDraggable = true
        }

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }
    }

    private fun onWasteTypeClicked(wasteScanned: String) {
        val dialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_recommended_article)

        dialog.findViewById<TextView>(R.id.wasteType)?.text = wasteScanned

        scanWasteViewModel.searchArticle(wasteScanned)

        scanWasteViewModel.articles.observe(viewLifecycleOwner) { articles ->
            val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvRecommendedArticles)
            recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
            val adapter = ArticleRecommendAdapter(articles) { it ->
                val navController = findNavController()
                val bundle = Bundle().apply {
                    putString(DetailArticleFragment.EXTRA_TITLE, it.title)
                    putString(DetailArticleFragment.EXTRA_PUBLISHER, it.publisher)
                    putString(DetailArticleFragment.EXTRA_DATE, it.datePublished?.let { it1 ->
                        DateConverter.formatDate(
                            it1
                        )
                    })
                    putString(DetailArticleFragment.EXTRA_CONTENT, it.content)
                    putString(DetailArticleFragment.EXTRA_IMAGE, it.imgUrl)
                }
                Log.d(TAG, "title: ${it.title}, publisher: ${it.publisher}, date: ${it.datePublished}, content: ${it.content}, image: ${it.imgUrl}")
                navController.navigate(R.id.action_navigation_history_to_detailArticleFragment, bundle)
            }
            recyclerView?.adapter = adapter
        }

        setDialogBehaviour(dialog)
        dialog.show()
    }

    companion object {
        private const val TAG = "HistoryFragment"
    }

}