package com.agdemidov.nasaclient.ui.apod

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.agdemidov.nasaclient.models.ApodModel
import com.agdemidov.nasaclient.services.apod.ApodService
import com.agdemidov.nasaclient.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ApodsViewModel(
    private val apodService: ApodService
) : BaseViewModel() {

    private val TAG = ApodsViewModel::class.simpleName

    private val _apodItems = MutableSharedFlow<List<ApodModel>>(replay = 1)
    private val _isFirstLaunch = MutableSharedFlow<Boolean>(replay = 1)

    init {
        viewModelScope.launch {
            _isFirstLaunch.emit(true)
        }
    }

    fun fetchCachedApodsOnStart() =
        viewModelScope.launch(Dispatchers.Default) {
            _isFirstLaunch.emit(false)
            val cachedItems = apodService.reloadAllApodsFromCache()
            if (cachedItems.isEmpty()) {
                Log.i(TAG, "no cached apods in db, load first page")
                fetchApodsList(true)
            } else {
                Log.i(TAG, "return cached items amount: ${cachedItems.size}")
                _apodItems.emit(cachedItems)
            }
        }

    fun fetchApodsList(isPTR: Boolean = true) =
        viewModelScope.launch(Dispatchers.Default) {
            showProgressIndicator(isPTR)
            runCatching { apodService.fetchApods(isPTR) }
                .onSuccess { _apodItems.emit(it) }
                .onFailure { onRequestFailure(it) }
            showProgressIndicator(false)
        }

    val apodItems: SharedFlow<List<ApodModel>>
        get() = _apodItems
    val isFirstLaunch: SharedFlow<Boolean>
        get() = _isFirstLaunch
}
