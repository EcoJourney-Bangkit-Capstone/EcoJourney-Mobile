package com.bangkit.ecojourney.ui.wastescan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecojourney.data.repository.ArticleRepository
import com.bangkit.ecojourney.data.repository.ScanRepository
import com.bangkit.ecojourney.data.response.ArticlesItem
import com.bangkit.ecojourney.data.response.ScanDetails
import com.bangkit.ecojourney.data.response.ScanResponse
import kotlinx.coroutines.launch
import java.io.File

class WasteScanViewModel(private val articleRepository: ArticleRepository, private val scanRepository: ScanRepository): ViewModel() {
    private val _articles = MutableLiveData<List<ArticlesItem>>()
    val articles: LiveData<List<ArticlesItem>> = _articles

    private val _scanResponse = MutableLiveData<ScanDetails?>()
    val scanResponse: LiveData<ScanDetails?> get() = _scanResponse

    fun getArticles() {
        viewModelScope.launch {
            val response = articleRepository.getArticles()
            if (response.details != null) _articles.value = response.details.articles
        }
    }

    fun postScan(image: File, types: List<String>) {
        viewModelScope.launch {
            val response = scanRepository.postScan(image, types)
            if (response.details != null) _scanResponse.value = response.details
        }
    }
}