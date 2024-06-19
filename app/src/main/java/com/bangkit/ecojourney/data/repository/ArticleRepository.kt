package com.bangkit.ecojourney.data.repository

import android.util.Log
import com.bangkit.ecojourney.data.pref.UserModel
import com.bangkit.ecojourney.data.pref.UserPreference
import com.bangkit.ecojourney.data.response.LoginResponse
import com.bangkit.ecojourney.data.response.RegisterResponse
import com.bangkit.ecojourney.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ArticleRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
): Repository {

    fun getAllArticles() = apiService.getAllArticles()

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
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