package com.bangkit.ecojourney.data.repository

import android.util.Log
import com.bangkit.ecojourney.data.pref.UserModel
import com.bangkit.ecojourney.data.pref.UserPreference
import com.bangkit.ecojourney.data.response.LoginResponse
import com.bangkit.ecojourney.data.response.RegisterResponse
import com.bangkit.ecojourney.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
): Repository {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        val response = apiService.register(name, email, password)
        if (response.error) {
            Log.d("REGISTER ERROR", response.message)
        }
        return response
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)

        if (!response.error) {
            val user = UserModel(
                email = email,
                token = response.data.token)
            userPreference.saveSession(user)
            Log.d("LOGIN SAVE SESS", userPreference.getSession().first().toString())
        }
        return response
    }

//    suspend fun register(name: String, email: String, password: String): RegisterResponse {
//        return apiService.register(name, email, password)
//    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}