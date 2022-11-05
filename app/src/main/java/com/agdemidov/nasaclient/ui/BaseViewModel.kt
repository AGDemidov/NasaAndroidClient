package com.agdemidov.nasaclient.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agdemidov.nasaclient.R
import com.agdemidov.nasaclient.services.*
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.*
import java.util.zip.DataFormatException

abstract class BaseViewModel : ViewModel() {

    private val _topProgressIndicator = MutableStateFlow(false)
    private val _alertEvent = MutableSharedFlow<Pair<String, String>>(replay = 0)
    private val _toastEvent = MutableSharedFlow<String>(replay = 0)
    private val _snackBarEvent = MutableSharedFlow<Int>(replay = 0)


    val topProgressIndicator: StateFlow<Boolean>
        get() = _topProgressIndicator
    val alertEvent: SharedFlow<Pair<String, String>>
        get() = _alertEvent
    val toastEvent: SharedFlow<String>
        get() = _toastEvent
    val snackBarEvent: SharedFlow<Int>
        get() = _snackBarEvent

    protected fun showProgressIndicator(show: Boolean) {
        _topProgressIndicator.value = show
    }

    protected suspend fun onRequestFailure(t: Throwable) = when (t) {
        is HttpServiceException -> _alertEvent.emit(
            Pair("Http Request error code is ${t.errorCode}", t.errorMessage)
        )
        is GeneralServiceException -> _alertEvent.emit(
            Pair(t.title, t.errorMessage)
        )
        is DataConvertServiceException -> _alertEvent.emit(
            Pair("Data convert error in ${t.className}", t.errorMessage)
        )
        is NoNetworkServiceException -> _snackBarEvent.emit(R.string.no_network)
        is DataFormatException -> _alertEvent.emit(Pair("Date extraction error", t.message ?: ""))
        else -> {}
    }

    override fun onCleared() = viewModelScope.coroutineContext.cancelChildren()
}
