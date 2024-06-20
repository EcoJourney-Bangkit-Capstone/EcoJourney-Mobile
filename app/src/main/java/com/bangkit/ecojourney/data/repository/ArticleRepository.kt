package com.bangkit.ecojourney.data.repository

import com.bangkit.ecojourney.data.pref.UserPreference
import com.bangkit.ecojourney.data.response.ArticleResponse
import com.bangkit.ecojourney.data.retrofit.ApiService
import retrofit2.Call

class ArticleRepository(private val apiService: ApiService) {

    fun getAllArticles(): Call<ArticleResponse> {
        return apiService.getAllArticles()
    }

    suspend fun searchArticles(keyword: String): ArticleResponse {
        val request = mapOf(
            "keyword" to keyword
        )
        return apiService.searchArticle(request)
    }


    companion object {
        @Volatile
        private var instance: ArticleRepository? = null
        fun getInstance(
            apiService: ApiService
        ): ArticleRepository =
            instance ?: synchronized(this) {
                instance ?: ArticleRepository(apiService)
            }.also { instance = it }
    }
}