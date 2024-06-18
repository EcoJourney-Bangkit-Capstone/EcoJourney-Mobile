package com.bangkit.ecojourney.di

import android.content.Context
import com.bangkit.ecojourney.data.pref.UserPreference
import com.bangkit.ecojourney.data.pref.dataStore
import com.bangkit.ecojourney.data.repository.UserRepository

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}