package com.bangkit.ecojourney.di

import android.content.Context
import com.bangkit.ecojourney.data.pref.UserPreference
import com.bangkit.ecojourney.data.pref.dataStore
import com.bangkit.ecojourney.data.repository.ArticleRepository
import com.bangkit.ecojourney.data.repository.ScanRepository
import com.bangkit.ecojourney.data.repository.UserRepository
import com.bangkit.ecojourney.data.retrofit.ApiConfig

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideArticleRepository(context: Context): ArticleRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return ArticleRepository.getInstance(pref, apiService)
    }

    fun provideScanRepository(context: Context): ScanRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return ScanRepository.getInstance(pref, apiService)
    }
}