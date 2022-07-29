package com.agdemidov.nasaclient.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.agdemidov.nasaclient.services.apod.ApodService
import com.agdemidov.nasaclient.services.neo.NeoService
import com.agdemidov.nasaclient.ui.apod.ApodsViewModel
import com.agdemidov.nasaclient.ui.neo.NeoViewModel

const val unknownViewModelMessage = "Unknown ViewModel class name"
const val unknownServiceTypeMessage = "Wrong service type for %s class"

class ViewModelsFactory(
    private val service: Any
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApodsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            if (service is ApodService) {
                return ApodsViewModel(service) as T
            }
            throw IllegalArgumentException(unknownServiceTypeMessage.format(modelClass.simpleName))
        } else if (modelClass.isAssignableFrom(NeoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            if (service is NeoService) {
                return NeoViewModel(service) as T
            }
            throw IllegalArgumentException(unknownServiceTypeMessage.format(modelClass.simpleName))
        }
        throw IllegalArgumentException(unknownViewModelMessage)
    }
}