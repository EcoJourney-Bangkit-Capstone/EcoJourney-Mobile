package com.bangkit.ecojourney.data.repository

import com.bangkit.ecojourney.data.pref.UserPreference
import com.bangkit.ecojourney.data.response.ArticleResponse
import com.bangkit.ecojourney.data.retrofit.ApiService

class ArticleRepository(private val apiService: ApiService) {

    suspend fun getArticles(): ArticleResponse {
        return apiService.getArticle()
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