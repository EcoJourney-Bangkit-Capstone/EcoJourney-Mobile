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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.ecojourney.R
import com.bangkit.ecojourney.adapter.HistoryAdapter
import com.bangkit.ecojourney.adapter.ScanResultAdapter
import com.bangkit.ecojourney.data.ScanResult
import com.bangkit.ecojourney.data.WasteScanned
import com.bangkit.ecojourney.databinding.FragmentHistoryBinding
import com.bangkit.ecojourney.ui.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment() {
    private val viewModel by viewModels<HistoryViewModel> {
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

        val dummyDataList = listOf(
            ScanResult(
                listOf(WasteScanned("Plastic bottle")),
                "2024-06-18 15:00:00",
                // Replace with your actual Bitmap image
                Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            ),
            ScanResult(
                listOf(WasteScanned("Paper cup"), WasteScanned("Food scrap")),
                "2024-06-17 10:30:00",
                // Replace with your actual Bitmap image
                Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            ),
            ScanResult(
                listOf(WasteScanned("Metal can")),
                "2024-06-16 09:00:00",
                // Replace with your actual Bitmap image
                Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            ),
            ScanResult(
                listOf(WasteScanned("Cardboard box")),
                "2024-06-15 14:15:00",
                // Replace with your actual Bitmap image
                Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            ),
            ScanResult(
                listOf(WasteScanned("Glass bottle")),
                "2024-06-14 11:40:00",
                // Replace with your actual Bitmap image
                Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            )
        )

        binding.rvScanHistory.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = HistoryAdapter(dummyDataList) { position ->
            val scanResult = dummyDataList[position]
            onItemClicked(scanResult)
        }
        binding.rvScanHistory.adapter = adapter
    }

    private fun onItemClicked(scanResult: ScanResult) {
        val dialog = BottomSheetDialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_history_detail)
        //dialog.findViewById<ImageView>(R.id.scanImage)?.setImageBitmap(scanResult.image)

        val formatter =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val date = formatter.parse(scanResult.dateTime)
        val formatter2 = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US)
        val formattedDate = formatter2.format(date)

        dialog.findViewById<TextView>(R.id.historyScanDate)?.text = formattedDate

        setDialogBehaviour(dialog)
        dialog.show()

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvScanResults)
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ScanResultAdapter(scanResult.wasteScanned) { position ->
            val wasteScanned = scanResult.wasteScanned[position]
            onWasteTypeClicked(wasteScanned)
        }
        recyclerView?.adapter = adapter

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

    private fun onWasteTypeClicked(wasteScanned: WasteScanned) {
        val dialog = BottomSheetDialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_recommended_article)

        dialog.findViewById<TextView>(R.id.wasteType)?.text = wasteScanned.wasteType

        dialog.show()
        setDialogBehaviour(dialog)

    }

}