package com.bangkit.ecojourney.data.repository

import com.bangkit.ecojourney.data.pref.UserPreference
import com.bangkit.ecojourney.data.response.ArticleResponse
import com.bangkit.ecojourney.data.retrofit.ApiService

class ArticleRepository(private val apiService: ApiService) {

    fun getAllArticles() = apiService.getAllArticles()

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