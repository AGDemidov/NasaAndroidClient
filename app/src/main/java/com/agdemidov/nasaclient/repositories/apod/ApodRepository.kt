package com.agdemidov.nasaclient.repositories.apod

import com.agdemidov.nasaclient.httpclient.RetrofitClient
import com.agdemidov.nasaclient.repositories.ApiResponse
import com.agdemidov.nasaclient.repositories.NoNetworkResponse
import com.agdemidov.nasaclient.repositories.apod.api.ApodApi
import com.agdemidov.nasaclient.repositories.apod.dto.ApodDto
import com.agdemidov.nasaclient.utils.DateGetter.getDateWithOffset
import com.agdemidov.nasaclient.utils.DateGetter.getToday
import com.agdemidov.nasaclient.utils.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class ApodRepository {
    suspend fun fetchApodsList(): ApiResponse<List<ApodDto>> =
        try {
            if (!NetworkManager.isNetworkAvailable) {
                NoNetworkResponse()
            } else {
                val response = apodApiEndpoints.fetchApodsPage(
                    startDate = getDateWithOffset(-61),
                    endDate = getToday()
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
        kotlin.runCatching { apodApiEndpoints.fetchApod().awaitResponse() }
            .onSuccess { result = ApiResponse.createResponse(it) }
            .onFailure { result = ApiResponse.createError(it) }
        return result
    }

    companion object {
        val apodApiEndpoints: ApodApi by lazy {
            RetrofitClient.clientInstance
                .build()
                .create(ApodApi::class.java)
        }
    }
}
