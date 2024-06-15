package com.bangkit.ecojourney.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecojourney.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<String>()
    val loginResponse: LiveData<String> = _loginResponse

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val response = userRepository.login(email, password)
            _loginResponse.value = response
        }
    }
}
