package com.bangkit.ecojourney.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.ecojourney.BuildConfig
import com.bangkit.ecojourney.data.pref.UserModel
import com.bangkit.ecojourney.data.repository.ArticleRepository
import com.bangkit.ecojourney.data.repository.UserRepository
import com.bangkit.ecojourney.data.response.ArticleResponse
import com.bangkit.ecojourney.data.response.ScanDetails
import com.bangkit.ecojourney.data.response.SelfResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _selfResponse = MutableLiveData<SelfResponse?>()
    val selfResponse: LiveData<SelfResponse?> = _selfResponse

    fun getSelfInfo() {
        viewModelScope.launch {
            val response = userRepository.getSelfInfo()
            if (!response.error!!) _selfResponse.value = response
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    companion object {
        private const val TAG = "ArticleViewModel"
    }
}