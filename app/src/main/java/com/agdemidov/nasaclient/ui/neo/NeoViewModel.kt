package com.agdemidov.nasaclient.ui.neo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agdemidov.nasaclient.models.NeoModels
import com.agdemidov.nasaclient.services.neo.NeoService
import com.agdemidov.nasaclient.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NeoViewModel(private val neoService: NeoService) : BaseViewModel() {

    private val TAG = NeoViewModel::class.simpleName

    private val _neoData = MutableLiveData<NeoModels>()
    private val _isFirstLaunch = MutableLiveData(true)

    fun fetchCachedTodayNeoListOnStart() =
        viewModelScope.launch(Dispatchers.Default) {
            _isFirstLaunch.postValue(false)
            val cachedItems = neoService.reloadNeoObjectsFromCache()
            if (cachedItems.count == 0) {
                Log.i(TAG, "no cached neo data in db, reload all")
                fetchTodayNeoList()
            } else {
                Log.i(TAG, "return cached items for days amount: ${cachedItems.neoModelsMap.size}")
                _neoData.postValue(cachedItems)
            }
        }

    fun fetchTodayNeoList() =
        viewModelScope.launch(Dispatchers.Default) {
            showProgressIndicator(true)
            runCatching { neoService.fetch3DaysNeoObjects() }
                .onSuccess { _neoData.postValue(it) }
                .onFailure { onRequestFailure(it) }
            showProgressIndicator(false)
        }

    val neoData: LiveData<NeoModels>
        get() = _neoData
    val isFirstLaunch: LiveData<Boolean>
        get() = _isFirstLaunch
}
