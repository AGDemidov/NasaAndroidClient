package com.agdemidov.nasaclient.ui.neo

import androidx.lifecycle.viewModelScope
import com.agdemidov.nasaclient.models.NeoModels
import com.agdemidov.nasaclient.services.neo.NeoService
import com.agdemidov.nasaclient.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class NeoViewModel(private val neoService: NeoService) : BaseViewModel() {

    private val _neoData = MutableSharedFlow<NeoModels>(replay = 1)
    val neoData: SharedFlow<NeoModels>
        get() = _neoData

    fun fetchTodayNeoList() =
        viewModelScope.launch(Dispatchers.Main) {
            showProgressIndicator(true)
            runCatching { neoService.fetch3DaysNeoObjects() }
                .onSuccess { _neoData.emit(it) }
                .onFailure { onRequestFailure(it) }
            showProgressIndicator(false)
        }
}