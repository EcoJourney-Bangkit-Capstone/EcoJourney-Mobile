package com.bangkit.ecojourney.data.repository

import android.util.Log
import com.bangkit.ecojourney.data.pref.UserModel
import com.bangkit.ecojourney.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository private constructor(
    private val userPreference: UserPreference,
//    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(email: String, password: String): String {
        val user = UserModel(
            email = email,
            token = "dummy_token")
        userPreference.saveSession(user)
        Log.d("LOGIN SAVE SESS", userPreference.getSession().first().toString())
        return "Login : ${email}, $password"
    }

//    suspend fun register(name: String, email: String, password: String): RegisterResponse {
//        return apiService.register(name, email, password)
//    }

//    suspend fun login(email: String, password: String): LoginResponse {
//        val response = apiService.login(email, password)
//        if (!response.error) {
//            val user = UserModel(
//                email = email,
//                token = response.loginResult.token)
//            userPreference.saveSession(user)
//            Log.d("LOGIN SAVE SESS", userPreference.getSession().first().toString())
//        }
//        return response
//    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
//            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)//, apiService)
            }.also { instance = it }
    }
}