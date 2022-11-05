package com.agdemidov.nasaclient.ui.apod

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.agdemidov.nasaclient.models.ApodModel
import com.agdemidov.nasaclient.services.apod.ApodService
import com.agdemidov.nasaclient.ui.BaseViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ApodsViewModel(
    private val apodService: ApodService
) : BaseViewModel() {

    private val TAG = ApodsViewModel::class.simpleName

    private val _apodItems = MutableStateFlow<List<ApodModel>>(listOf())
    private val _isApodsBelowPageLoading = MutableStateFlow(false)
    private var _storedScrollPosition = 0

    init {
        viewModelScope.launch {
            val cachedItems = apodService.reloadAllApodsFromCache()
            if (cachedItems.isEmpty()) {
                Log.i(TAG, "no cached apods in db, load first page")
                fetchApodsList(true)
            } else {
                Log.i(TAG, "return cached items amount: ${cachedItems.size}")
                _apodItems.value = cachedItems
            }
        }
    }

    suspend fun doFetchApodsRequest(isPTR: Boolean = true) =
        runCatching { apodService.fetchApods(isPTR) }
            .onSuccess {
                _apodItems.value = it
                Log.i(TAG, "fetched ${_apodItems.value.size} items isPTR=$isPTR")
            }
            .onFailure { onRequestFailure(it) }

    fun fetchApodsList(isPTR: Boolean = true) =
        viewModelScope.launch {
            if (isPTR) {
                showProgressIndicator(true)
                doFetchApodsRequest()
                showProgressIndicator(false)
            } else if (!_isApodsBelowPageLoading.value) {
                _isApodsBelowPageLoading.value = true
                doFetchApodsRequest(false)
                _isApodsBelowPageLoading.value = false
            }
        }

    fun setScrollPosition(scrollPosition: Int) {
        _storedScrollPosition = scrollPosition
    }

    val apodItems: StateFlow<List<ApodModel>>
        get() = _apodItems
    val isApodsBelowPageLoading: StateFlow<Boolean>
        get() = _isApodsBelowPageLoading
    val storedScrollPosition
        get() = _storedScrollPosition
}
