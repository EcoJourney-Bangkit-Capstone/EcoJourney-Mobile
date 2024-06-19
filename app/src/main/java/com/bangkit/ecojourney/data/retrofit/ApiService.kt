package com.bangkit.ecojourney.data.retrofit

import com.bangkit.ecojourney.data.response.LoginResponse
import com.bangkit.ecojourney.data.response.LogoutResponse
import com.bangkit.ecojourney.data.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(
        @Body requestBody: Map<String, String>
    ): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(
        @Body requestBody: Map<String, String>
    ): LoginResponse

    @POST("auth/logout")
    suspend fun logout(
        @Body requestBody: Map<String, String>
    ): LogoutResponse
}