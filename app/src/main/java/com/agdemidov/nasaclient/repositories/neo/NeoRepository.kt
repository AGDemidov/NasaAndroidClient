package com.agdemidov.nasaclient.repositories.neo

import com.agdemidov.nasaclient.httpclient.RetrofitClient
import com.agdemidov.nasaclient.repositories.ApiResponse
import com.agdemidov.nasaclient.repositories.NoNetworkResponse
import com.agdemidov.nasaclient.repositories.neo.api.NeoApiInterface
import com.agdemidov.nasaclient.repositories.neo.dto.NeoDto
import com.agdemidov.nasaclient.utils.DateGetter.getToday
import com.agdemidov.nasaclient.utils.NetworkManager
import retrofit2.Response
import retrofit2.awaitResponse

class NeoRepository private constructor(private val neoApi: NeoApiInterface) {
    suspend fun fetchNeoObjects(
        startDate: String = getToday(),
        endDate: String = getToday(),
    ): ApiResponse<NeoDto> =
        try {
            if (!NetworkManager.isNetworkAvailable) {
                NoNetworkResponse()
            } else {
                val response: Response<NeoDto> = neoApi.fetchTodayNeoObjects(
                    startDate = startDate,
                    endDate = endDate
                ).awaitResponse()
                ApiResponse.createResponse(response)
            }

        } catch (t: Throwable) {
            ApiResponse.createError(t)
        }

    companion object {
        @Volatile
        private var instance: NeoRepository? = null

        private val neoHttpRequestsApi: NeoApiInterface by lazy {
            RetrofitClient.builderInstance
                .build()
                .create(NeoApiInterface::class.java)
        }

        fun getInstance(): NeoRepository =
            instance ?: synchronized(this) {
                instance ?: NeoRepository(
                    neoHttpRequestsApi
                ).also { instance = it }
            }
    }
}
