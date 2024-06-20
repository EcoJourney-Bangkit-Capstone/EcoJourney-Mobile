package com.bangkit.ecojourney.data.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("details")
	val details: HistoryDetails?,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class HistoryDetails(

	@field:SerializedName("total_item")
	val totalItem: Int,

	@field:SerializedName("history")
	val history: List<HistoryItem>
)

data class HistoryItem(

	@field:SerializedName("total_type")
	val totalType: Int,

	@field:SerializedName("historyId")
	val historyId: String,

	@field:SerializedName("imageURL")
	val imageURL: String,

	@field:SerializedName("type")
	val type: List<String>,

	@field:SerializedName("timestamp")
	val timestamp: String
)
