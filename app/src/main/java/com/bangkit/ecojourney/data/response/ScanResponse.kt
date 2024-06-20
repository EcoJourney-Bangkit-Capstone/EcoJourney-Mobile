package com.bangkit.ecojourney.data.response

import com.google.gson.annotations.SerializedName

data class ScanResponse(

	@field:SerializedName("details")
	val details: ScanDetails?,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class ScanDetails(

	@field:SerializedName("total_type")
	val totalType: Int,

	@field:SerializedName("historyId")
	val historyId: String,

	@field:SerializedName("imageURL")
	val imageURL: String,

	@field:SerializedName("type")
	val type: List<String>
)
