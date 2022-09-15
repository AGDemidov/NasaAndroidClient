package com.agdemidov.nasaclient.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.agdemidov.nasaclient.services.apod.ApodService
import com.agdemidov.nasaclient.services.neo.NeoService
import com.agdemidov.nasaclient.ui.apod.ApodsViewModel
import com.agdemidov.nasaclient.ui.neo.NeoViewModel

const val unknownViewModelMessage = "Unknown ViewModel class name"

class ViewModelsFactory private constructor(
    vararg params: Any
) : ViewModelProvider.Factory {
    private var params: Array<Any> = arrayOf(*params)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApodsViewModel::class.java)) {
            require(params[0] is ApodService)
            @Suppress("UNCHECKED_CAST")
            return ApodsViewModel(params[0] as ApodService) as T
        } else if (modelClass.isAssignableFrom(NeoViewModel::class.java)) {
            require(params[0] is NeoService)
            @Suppress("UNCHECKED_CAST")
            return NeoViewModel(params[0] as NeoService) as T
        }
        throw IllegalArgumentException(unknownViewModelMessage)
    }

    companion object {
        fun provideApodViewModel(context: Context) =
            ViewModelsFactory(
                ApodService.getInstance(context)
            )

        fun provideNeoViewModel() =
            ViewModelsFactory(
                NeoService.getInstance()
            )
    }
}
