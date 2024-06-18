package com.bangkit.ecojourney.ui.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bangkit.ecojourney.data.pref.UserModel
import com.bangkit.ecojourney.data.repository.UserRepository

class SplashScreenViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }
}