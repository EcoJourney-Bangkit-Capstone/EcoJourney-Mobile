package com.bangkit.ecojourney.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecojourney.data.repository.ScanRepository
import com.bangkit.ecojourney.data.response.HistoryItem
import com.bangkit.ecojourney.data.response.HistoryResponse
import kotlinx.coroutines.launch

class HistoryViewModel(private val scanRepository: ScanRepository) : ViewModel() {

    private var _history = MutableLiveData<List<HistoryItem>>()
    val history: LiveData<List<HistoryItem>> = _history

    fun getHistory() {
        viewModelScope.launch {
            val response = scanRepository.getHistory()
            if (response.details != null) _history.value = response.details.history
        }
    }



}