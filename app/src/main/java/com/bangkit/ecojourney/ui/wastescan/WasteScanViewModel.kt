package com.bangkit.ecojourney.ui.wastescan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecojourney.data.repository.ArticleRepository
import com.bangkit.ecojourney.data.response.ArticleItem
import kotlinx.coroutines.launch

class WasteScanViewModel(private val articleRepository: ArticleRepository): ViewModel() {
    private val _articles = MutableLiveData<List<ArticleItem>>()
    val articles: LiveData<List<ArticleItem>> = _articles

    fun getArticles() {
        viewModelScope.launch {
            val response = articleRepository.getArticles()
            _articles.value = response.listArticle
        }
    }
}