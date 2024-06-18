package com.bangkit.ecojourney.data.response

import com.google.gson.annotations.SerializedName

data class ArticleResponse(

	@field:SerializedName("data")
	val listArticle: List<ArticleItem>,

	@field:SerializedName("total_count")
	val totalCount: Int,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class ArticleItem(

	@field:SerializedName("date_published")
	val datePublished: String,

	@field:SerializedName("img_url")
	val imgUrl: String,

	@field:SerializedName("publisher")
	val publisher: String,

	@field:SerializedName("topic")
	val topic: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("content")
	val content: String
)
