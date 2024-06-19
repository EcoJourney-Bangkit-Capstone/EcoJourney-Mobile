package com.bangkit.ecojourney.ui.wastescan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecojourney.data.repository.ArticleRepository
import com.bangkit.ecojourney.data.response.ArticlesItem
import kotlinx.coroutines.launch

class WasteScanViewModel(private val articleRepository: ArticleRepository): ViewModel() {
    private val _articles = MutableLiveData<List<ArticlesItem>>()
    val articles: LiveData<List<ArticlesItem>> = _articles

    fun getArticles() {
        viewModelScope.launch {
            val response = articleRepository.getArticles()
            if (response.details != null) _articles.value = response.details.articles
        }
    }
}