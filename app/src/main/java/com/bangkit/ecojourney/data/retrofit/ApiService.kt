package com.bangkit.ecojourney.data.retrofit

import com.bangkit.ecojourney.data.repository.ArticleRepository
import com.bangkit.ecojourney.data.response.ArticleResponse
import com.bangkit.ecojourney.data.response.LoginResponse
import com.bangkit.ecojourney.data.response.LogoutResponse
import com.bangkit.ecojourney.data.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("username") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("auth/logout")
    suspend fun logout(
        @Field("email") email: String
    ): LogoutResponse

    @GET("api/article")
    fun getAllArticles(): Call<ArticleResponse>
}