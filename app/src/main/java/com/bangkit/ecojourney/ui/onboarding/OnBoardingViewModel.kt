package com.bangkit.ecojourney.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecojourney.data.repository.UserRepository
import kotlinx.coroutines.launch

class OnBoardingViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<String>()
    val loginResponse: LiveData<String> = _loginResponse

    private val _registerResponse = MutableLiveData<String>()
    val registerResponse: LiveData<String> = _registerResponse

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val response = userRepository.login(email, password)
            _loginResponse.value = response
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            val response = userRepository.register(name, email, password, confirmPassword)
            _registerResponse.value = response
        }
    }
}
