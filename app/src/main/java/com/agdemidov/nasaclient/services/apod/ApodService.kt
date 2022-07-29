package com.agdemidov.nasaclient.services.apod

import com.agdemidov.nasaclient.models.ApodModel
import com.agdemidov.nasaclient.repositories.*
import com.agdemidov.nasaclient.repositories.apod.ApodRepository
import com.agdemidov.nasaclient.services.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApodService private constructor(private val repository: ApodRepository) {
    suspend fun fetchApodsList(): List<ApodModel> = withContext(Dispatchers.IO) {
        lateinit var response: List<ApodModel>
        return@withContext when (val apiResponse = repository.fetchApodsList()) {
            is SuccessResponse -> {
                runCatching { ApodMapper.mapApodsList(apiResponse.body) }
                    .onSuccess { response = it }
                    .onFailure {
                        throw DataConvertServiceException(
                            it::class.simpleName ?: "",
                            it.message ?: ""
                        )
                    }
                response.sortedByDescending { it.date }
            }
            is ErrorResponse -> {
                ErrorResponseProcessor.processError(apiResponse)
                response
            }
        }
    }

    companion object {
        val instance: ApodService by lazy {
            ApodService(ApodRepository())
        }
    }
}