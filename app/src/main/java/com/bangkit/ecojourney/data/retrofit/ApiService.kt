package com.bangkit.ecojourney.data.retrofit

import com.bangkit.ecojourney.data.repository.ArticleRepository
import com.bangkit.ecojourney.data.response.ArticleResponse
import com.bangkit.ecojourney.data.response.HistoryResponse
import com.bangkit.ecojourney.data.response.LoginResponse
import com.bangkit.ecojourney.data.response.LogoutResponse
import com.bangkit.ecojourney.data.response.RegisterResponse
import com.bangkit.ecojourney.data.response.ScanResponse
import com.bangkit.ecojourney.data.response.SelfResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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

    @GET("api/articles")
    suspend fun getAllArticles(
        @Header("Authorization") token: String
    ): ArticleResponse

    @POST("api/articles/search")
    suspend fun searchArticle(
        @Body requestBody: Map<String, String>
    ): ArticleResponse

    @Multipart
    @POST("api/waste-recognition")
    suspend fun postScan(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part vararg types: MultipartBody.Part
    ): ScanResponse

    @GET("api/user/self")
    suspend fun getSelfInfo(
        @Header("Authorization") token: String
    ): SelfResponse

    @GET("api/waste-recognition/history")
    suspend fun getHistory(
        @Header("Authorization") token: String
    ): HistoryResponse


}