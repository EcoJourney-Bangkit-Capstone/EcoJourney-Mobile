package com.bangkit.ecojourney.data.response

import com.google.gson.annotations.SerializedName

data class ArticleResponse(

	@field:SerializedName("details")
	val details: Details? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ArticlesItem(

	@field:SerializedName("date_published")
	val datePublished: String? = null,

	@field:SerializedName("img_url")
	val imgUrl: String? = null,

	@field:SerializedName("publisher")
	val publisher: String? = null,

	@field:SerializedName("topic")
	val topic: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)

data class Details(

	@field:SerializedName("total_count")
	val totalCount: Int? = null,

	@field:SerializedName("articles")
	val articles: List<ArticlesItem?>? = null
)
