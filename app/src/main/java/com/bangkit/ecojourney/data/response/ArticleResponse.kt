package com.bangkit.ecojourney.data.response

import com.google.gson.annotations.SerializedName

data class ArticleResponse(

	@field:SerializedName("details")
	val details: Details?,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class ArticlesItem(

	@field:SerializedName("date_published")
	val datePublished: String,

	@field:SerializedName("img_url")
	val imgUrl: String,

	@field:SerializedName("publisher")
	val publisher: String,

	@field:SerializedName("topic")
	val topic: Any,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("content")
	val content: String
)

data class Details(

	@field:SerializedName("articles")
	val articles: List<ArticlesItem>,

	@field:SerializedName("Total_count")
	val totalCount: Int
)