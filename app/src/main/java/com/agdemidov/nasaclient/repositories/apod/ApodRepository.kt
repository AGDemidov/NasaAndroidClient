package com.agdemidov.nasaclient.repositories.apod

import com.agdemidov.nasaclient.httpclient.RetrofitClient
import com.agdemidov.nasaclient.repositories.ApiResponse
import com.agdemidov.nasaclient.repositories.NoNetworkResponse
import com.agdemidov.nasaclient.repositories.apod.api.ApodApiInterface
import com.agdemidov.nasaclient.repositories.apod.dto.ApodDto
import com.agdemidov.nasaclient.utils.NetworkManager

import retrofit2.awaitResponse

class ApodRepository private constructor(
    private val apodApi: ApodApiInterface
) {
    suspend fun fetchApodsList(startDate: String, endDate: String): ApiResponse<List<ApodDto>> =
        try {
            if (!NetworkManager.isNetworkAvailable) {
                NoNetworkResponse()
            } else {
                val response = apodApi.fetchApodsPage(
                    startDate = startDate,
                    endDate = endDate
                ).awaitResponse()
                ApiResponse.createResponse(response)
            }
        } catch (t: Throwable) {
            ApiResponse.createError(t)
        }

    suspend fun fetchApod(): ApiResponse<ApodDto> {
        lateinit var result: ApiResponse<ApodDto>
        if (!NetworkManager.isNetworkAvailable) {
            result = NoNetworkResponse()
        }
        runCatching { apodApi.fetchApod().awaitResponse() }
            .onSuccess { result = ApiResponse.createResponse(it) }
            .onFailure { result = ApiResponse.createError(it) }
        return result
    }

    companion object {
        @Volatile
        private var instance: ApodRepository? = null

        private val apodHttpRequestsApi: ApodApiInterface by lazy {
            RetrofitClient.builderInstance
                .build()
                .create(ApodApiInterface::class.java)
        }

        fun getInstance(): ApodRepository =
            instance ?: synchronized(this) {
                instance ?: ApodRepository(
                    apodHttpRequestsApi
                ).also { instance = it }
            }
    }
}
