package com.agdemidov.nasaclient.repositories.neo

import com.agdemidov.nasaclient.httpclient.RetrofitClient
import com.agdemidov.nasaclient.repositories.ApiResponse
import com.agdemidov.nasaclient.repositories.NoNetworkResponse
import com.agdemidov.nasaclient.repositories.neo.api.NeoApi
import com.agdemidov.nasaclient.repositories.neo.dto.NeoDto
import com.agdemidov.nasaclient.utils.DateGetter.getToday
import com.agdemidov.nasaclient.utils.NetworkManager
import retrofit2.Response
import retrofit2.awaitResponse

class NeoRepository {
    suspend fun fetchNeoObjects(
        startDate: String = getToday(),
        endDate: String = getToday(),
    ): ApiResponse<NeoDto> =
        try {
            if (!NetworkManager.isNetworkAvailable) {
                NoNetworkResponse()
            } else {
                val response: Response<NeoDto> = neoApiEndPoints.fetchTodayNeoObjects(
                    startDate = startDate,
                    endDate = endDate
                ).awaitResponse()
                ApiResponse.createResponse(response)
            }

        } catch (t: Throwable) {
            ApiResponse.createError(t)
        }

    companion object {
        val neoApiEndPoints: NeoApi by lazy {
            RetrofitClient.clientInstance
                .build()
                .create(NeoApi::class.java)
        }
    }
}