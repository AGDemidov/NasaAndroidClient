package com.agdemidov.nasaclient.ui.neo

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.agdemidov.nasaclient.models.NeoModel
import com.agdemidov.nasaclient.models.NeoModels
import com.agdemidov.nasaclient.services.neo.NeoService
import com.agdemidov.nasaclient.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NeoViewModel(private val neoService: NeoService) : BaseViewModel() {

    private val TAG = NeoViewModel::class.simpleName

    private val _neoData = MutableStateFlow<NeoModels?>(null)

    init {
        viewModelScope.launch {
            val cachedItems = neoService.reloadNeoObjectsFromCache()
            if (cachedItems.count == 0) {
                Log.i(TAG, "no cached neo data in db, reload all")
                fetchTodayNeoList()
            } else {
                Log.i(TAG, "return cached items for days amount: ${cachedItems.neoModelsMap.size}")
                _neoData.value = cachedItems
            }
        }
    }

    fun fetchTodayNeoList() =
        viewModelScope.launch {
            _neoData.value = null
            showProgressIndicator(true)
            runCatching { neoService.fetch3DaysNeoObjects() }
                .onSuccess { _neoData.value = it}
                .onFailure { onRequestFailure(it) }
            showProgressIndicator(false)
        }

    val neoData = _neoData.asStateFlow()
}
