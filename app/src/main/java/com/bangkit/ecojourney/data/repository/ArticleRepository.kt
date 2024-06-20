package com.bangkit.ecojourney.data.repository

import com.bangkit.ecojourney.data.pref.UserPreference
import com.bangkit.ecojourney.data.response.ArticleResponse
import com.bangkit.ecojourney.data.retrofit.ApiService
import kotlinx.coroutines.flow.first
import retrofit2.Call

class ArticleRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun getAllArticles(): Call<ArticleResponse> {
        return apiService.getAllArticles("Bearer ${userPreference.getSession().first().token}")
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
            userPreference: UserPreference,
            apiService: ApiService
        ): ArticleRepository =
            instance ?: synchronized(this) {
                instance ?: ArticleRepository(userPreference, apiService)
            }.also { instance = it }
    }
}