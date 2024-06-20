package com.bangkit.ecojourney.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.ecojourney.data.ScanResult
import com.bangkit.ecojourney.data.WasteScanned
import com.bangkit.ecojourney.databinding.ScanResultListItemBinding

class ScanResultAdapter(
    private val data: List<String>,
    private val onClickListener: (Int) -> Unit) : RecyclerView.Adapter<ScanResultAdapter.ScanResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanResultViewHolder {
        val binding = ScanResultListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScanResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScanResultViewHolder, position: Int) {
        val scanResult = data[position]
        holder.binding.wasteType.text = scanResult

        // Set click listener on the ViewHolder's root view
        holder.binding.viewArticlesBtn.setOnClickListener {
            onClickListener(position)  // Call the provided listener with position
        }
    }

    override fun getItemCount() = data.size



    class ScanResultViewHolder(val binding: ScanResultListItemBinding) : RecyclerView.ViewHolder(binding.root)
}