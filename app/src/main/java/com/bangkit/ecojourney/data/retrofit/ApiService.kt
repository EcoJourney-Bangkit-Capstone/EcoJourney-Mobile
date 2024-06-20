package com.bangkit.ecojourney.data.retrofit

import com.bangkit.ecojourney.data.response.ArticleResponse
import com.bangkit.ecojourney.data.response.LoginResponse
import com.bangkit.ecojourney.data.response.LogoutResponse
import com.bangkit.ecojourney.data.response.RegisterResponse
import com.bangkit.ecojourney.data.response.ScanResponse
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

    @GET("api/article")
    suspend fun getArticle(

    ): ArticleResponse

    @Multipart
    @POST("api/waste-recognition")
    suspend fun postScan(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("type") type: RequestBody
    ): ScanResponse

}
