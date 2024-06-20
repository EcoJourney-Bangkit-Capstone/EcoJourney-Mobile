package com.bangkit.ecojourney.ui.article

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.ecojourney.BuildConfig
import com.bangkit.ecojourney.data.pref.UserModel
import com.bangkit.ecojourney.data.repository.ArticleRepository
import com.bangkit.ecojourney.data.response.ArticleResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleViewModel(private val articleRepository: ArticleRepository) : ViewModel() {

    private val _articles = MutableLiveData<ArticleResponse>()
    val articles: LiveData<ArticleResponse> = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorToast = MutableLiveData<Boolean?>()
    val errorToast: LiveData<Boolean?> = _errorToast

    fun getAllArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = articleRepository.getAllArticles()
            if (response.details != null) {
                _articles.value = response
                _isLoading.value = false
            }
        }
    }

    fun resetToast() {
        _errorToast.value = null
    }

    companion object {
        private const val TAG = "ArticleViewModel"
    }
}