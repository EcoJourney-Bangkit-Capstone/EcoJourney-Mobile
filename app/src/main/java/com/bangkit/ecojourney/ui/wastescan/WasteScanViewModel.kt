package com.bangkit.ecojourney.ui.wastescan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecojourney.BuildConfig
import com.bangkit.ecojourney.data.repository.ArticleRepository
import com.bangkit.ecojourney.data.repository.ScanRepository
import com.bangkit.ecojourney.data.response.ArticleResponse
import com.bangkit.ecojourney.data.response.ArticlesItem
import com.bangkit.ecojourney.data.response.ScanDetails
import com.bangkit.ecojourney.data.response.ScanResponse
import com.bangkit.ecojourney.ui.article.ArticleViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class WasteScanViewModel(private val articleRepository: ArticleRepository, private val scanRepository: ScanRepository): ViewModel() {
    private val _articles = MutableLiveData<List<ArticlesItem>>()
    val articles: LiveData<List<ArticlesItem>> = _articles

    private val _scanResponse = MutableLiveData<ScanDetails?>()
    val scanResponse: LiveData<ScanDetails?> get() = _scanResponse

    fun getAllArticles() {
        val client = articleRepository.getAllArticles()
        client.enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(
                call: Call<ArticleResponse>,
                response: Response<ArticleResponse>
            ) {
                if (response.isSuccessful) {
                    _articles.value = response.body()?.details?.articles
                    if (BuildConfig.DEBUG) Log.d(WasteScanViewModel.TAG, "onResponse: ${response.body()}")
                }
                else {
                    if (BuildConfig.DEBUG) Log.d(WasteScanViewModel.TAG, "onFailResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                if (BuildConfig.DEBUG) Log.d(WasteScanViewModel.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun postScan(image: File, types: List<String>) {
        viewModelScope.launch {
            val response = scanRepository.postScan(image, types)
            if (response.details != null) _scanResponse.value = response.details
        }
    }

    companion object {
        const val TAG = "WasteScanViewModel"
    }
}