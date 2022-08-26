package com.agdemidov.nasaclient.ui.apod

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agdemidov.nasaclient.models.ApodModel
import com.agdemidov.nasaclient.services.apod.ApodService
import com.agdemidov.nasaclient.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApodsViewModel(private val apodService: ApodService) : BaseViewModel() {

    private val _apodItems = MutableLiveData<List<ApodModel>>()

    fun fetchApodsList(isPTR: Boolean = true) =
        viewModelScope.launch(Dispatchers.Main) {
            showProgressIndicator(isPTR)
            runCatching { apodService.fetchApods(isPTR) }
                .onSuccess { _apodItems.value = it }
                .onFailure { onRequestFailure(it) }
            showProgressIndicator(false)
        }

    val apodItems: LiveData<List<ApodModel>> = _apodItems
}