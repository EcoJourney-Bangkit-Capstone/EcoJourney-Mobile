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
    private val _articles = MutableLiveData<ArticleResponse>()
    val articles: LiveData<ArticleResponse> = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorToast = MutableLiveData<Boolean?>()
    val errorToast: LiveData<Boolean?> = _errorToast

    private val _scanResponse = MutableLiveData<ScanDetails?>()
    val scanResponse: LiveData<ScanDetails?> get() = _scanResponse

    fun searchArticle(keyword: String) {
        viewModelScope.launch {
            val response = articleRepository.searchArticles(keyword)
            if (response.details != null) _articles.value = response
        }

    }

    fun postScan(image: File, types: List<String>) {
        viewModelScope.launch {
            val response = scanRepository.postScan(image, types)
            if (response.details != null) _scanResponse.value = response.details
        }
    }

    fun resetToast() {
        _errorToast.value = null
    }

    companion object {
        private const val TAG = "WasteScanViewModel"

    }
}