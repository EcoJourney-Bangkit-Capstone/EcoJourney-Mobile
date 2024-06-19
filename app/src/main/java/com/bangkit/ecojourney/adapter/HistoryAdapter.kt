package com.bangkit.ecojourney.adapter

import android.provider.Settings.System.DATE_FORMAT
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.ecojourney.data.ScanResult
import com.bangkit.ecojourney.databinding.ScanHistoryListItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val data: List<ScanResult>,
    private val onClickListener: (Int) -> Unit) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ScanHistoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val scanResult = data[position]
        holder.binding.apply {
            val formatter =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val date = formatter.parse(scanResult.dateTime)
            val formatter2 = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US)
            val formattedDate = formatter2.format(date)

            scanDate.text = formattedDate
            wasteCount.text = scanResult.wasteScanned.size.toString()
            scanImage.setImageBitmap(scanResult.image)

            root.setOnClickListener {
                onClickListener(position)
            }
        }

    }

    override fun getItemCount() = data.size



    class HistoryViewHolder(val binding: ScanHistoryListItemBinding) : RecyclerView.ViewHolder(binding.root)
}