package com.bangkit.ecojourney.data.repository

import android.util.Log
import com.bangkit.ecojourney.data.pref.UserModel
import com.bangkit.ecojourney.data.pref.UserPreference
import com.bangkit.ecojourney.data.response.LoginResponse
import com.bangkit.ecojourney.data.response.RegisterResponse
import com.bangkit.ecojourney.data.response.SelfResponse
import com.bangkit.ecojourney.data.retrofit.ApiService
import com.bangkit.ecojourney.ui.onboarding.LoginActivity
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
        val request = mapOf(
            "username" to name,
            "email" to email,
            "password" to password
        )
        val response = apiService.register(request)
        Log.d("REGISTER RESPONSE", "Login Response: $response")
        if (response.error) {
            Log.d("REGISTER ERROR", response.message)
        }
        return response
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val request = mapOf(
            "email" to email,
            "password" to password
        )
        val response = apiService.login(request)

        if (!response.error) {
            val user = response.data?.let {
                UserModel(
                    email = email,
                    token = it.token)
            }
            if (user != null) {
                userPreference.saveSession(user)
            }
            Log.d("LOGIN SAVE SESS", userPreference.getSession().first().toString())
        }
        return response
    }

    suspend fun getSelfInfo(): SelfResponse = apiService.getSelfInfo("Bearer ${userPreference.getSession().first().token}")

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