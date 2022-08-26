package com.agdemidov.nasaclient.services.apod

import com.agdemidov.nasaclient.models.ApodModel
import com.agdemidov.nasaclient.repositories.*
import com.agdemidov.nasaclient.repositories.apod.ApodRepository
import com.agdemidov.nasaclient.services.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApodService private constructor(private val repository: ApodRepository) {

    private var curPage: Int = 0
    private var apodItemsList: MutableList<ApodModel> = mutableListOf()

    suspend fun fetchApods(isPTR: Boolean): List<ApodModel> = withContext(Dispatchers.IO) {
        if (isPTR) {
            curPage = 0
        }
        return@withContext when (val apiResponse = repository.fetchApodsList(curPage)) {
            is SuccessResponse -> {
                runCatching { ApodMapper.mapApodsList(apiResponse.body) }
                    .onSuccess {
                        if (isPTR) {
                            apodItemsList = it.toMutableList()
                        } else {
                            apodItemsList.addAll(it)
                        }
                        curPage++
                    }
                    .onFailure {
                        throw DataConvertServiceException(
                            it::class.simpleName ?: "",
                            it.message ?: ""
                        )
                    }
                apodItemsList.sortedByDescending { it.date }
            }
            is ErrorResponse -> {
                ErrorResponseProcessor.processError(apiResponse)
            }
        }
    }

    companion object {
        val instance: ApodService by lazy {
            ApodService(ApodRepository())
        }
    }
}