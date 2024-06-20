package com.bangkit.ecojourney.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.ecojourney.data.pref.UserPreference
import com.bangkit.ecojourney.data.response.HistoryResponse
import com.bangkit.ecojourney.data.response.ScanResponse
import com.bangkit.ecojourney.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ScanRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun postScan(image: File, types: List<String>): ScanResponse {
        val requestFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", image.name, requestFile)

        val typeParts = types.map { type ->
            MultipartBody.Part.createFormData("type", type)
        }.toTypedArray()

        val response = withContext(Dispatchers.IO) {
            apiService.postScan("Bearer ${userPreference.getSession().first().token}", imagePart, *typeParts)
        }

        return response
    }

    suspend fun getHistory(): HistoryResponse {
        return apiService.getHistory("Bearer ${userPreference.getSession().first().token}")
    }

    companion object {
        @Volatile
        private var instance: ScanRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): ScanRepository =
            instance ?: synchronized(this) {
                instance ?: ScanRepository(userPreference, apiService)
            }.also { instance = it }
    }
}