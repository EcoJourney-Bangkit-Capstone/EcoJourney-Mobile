package com.bangkit.ecojourney.data

import android.graphics.Bitmap

data class ScanResult(
    val wasteScanned: List<WasteScanned>,
    val dateTime: String,
    val image: Bitmap
)

data class WasteScanned(
    val wasteType: String
)
