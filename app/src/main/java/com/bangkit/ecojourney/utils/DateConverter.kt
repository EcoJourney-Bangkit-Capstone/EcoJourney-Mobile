package com.bangkit.ecojourney.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateConverter {
    companion object {
        fun longToDatetimeString(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }

        fun formatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)
            val date = inputFormat.parse(dateString)
            return outputFormat.format(date!!)
        }
    }
}