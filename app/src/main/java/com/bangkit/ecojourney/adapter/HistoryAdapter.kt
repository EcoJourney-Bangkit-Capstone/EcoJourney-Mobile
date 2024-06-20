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
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.ecojourney.data.response.HistoryItem
import com.bangkit.ecojourney.databinding.ScanHistoryListItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HistoryAdapter(
    private val data: List<ScanResult>,

class HistoryAdapter(
    private val data: List<HistoryItem>,
    private val onClickListener: (Int) -> Unit) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ScanHistoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val scanResult = data[position]
        holder.binding.apply {
            scanDate.text = scanResult.timestamp
            wasteCount.text = scanResult.totalType.toString()

            val formattedDate = formatDateTime(scanResult.timestamp)
            scanDate.text = formattedDate

            Glide.with(root)
                .load(scanResult.imageURL)
                .override(Target.SIZE_ORIGINAL)
                .into(scanImage)

            root.setOnClickListener {
                onClickListener(position)
            }
        }

    }

    override fun getItemCount() = data.size

    private fun formatDateTime(input: String): String? {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        inputFormatter.timeZone = TimeZone.getTimeZone("UTC")

        val date: Date? = inputFormatter.parse(input)
        val outputFormatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH)

        return date?.let { outputFormatter.format(it) }
    }

    class HistoryViewHolder(val binding: ScanHistoryListItemBinding) : RecyclerView.ViewHolder(binding.root)
}