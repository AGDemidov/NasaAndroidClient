package com.agdemidov.nasaclient.services.neo

import com.agdemidov.nasaclient.models.NeoModels
import com.agdemidov.nasaclient.repositories.*
import com.agdemidov.nasaclient.repositories.neo.NeoRepository
import com.agdemidov.nasaclient.services.*
import com.agdemidov.nasaclient.utils.AppPreferences
import com.agdemidov.nasaclient.utils.DateGetter.getToday
import com.agdemidov.nasaclient.utils.DateGetter.getTomorrow
import com.agdemidov.nasaclient.utils.DateGetter.getYesterday
import com.agdemidov.nasaclient.utils.NEO_CACHED_OBJECT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NeoService private constructor(private val repository: NeoRepository) {
    private suspend fun fetchNeoObjects(startDate: String, endDate: String): NeoModels =
        withContext(Dispatchers.IO) {
            lateinit var response: NeoModels
            val result = when (val apiResponse = repository.fetchNeoObjects(startDate, endDate)) {
                is SuccessResponse -> {
                    runCatching { NeoMapper.mapNeoData(apiResponse.body) }
                        .onSuccess { response = it }
                        .onFailure {
                            throw DataConvertServiceException(
                                it::class.simpleName ?: "",
                                it.message ?: ""
                            )
                        }
                    cacheNeoData(response)
                    response
                }
                is ErrorResponse -> ErrorResponseProcessor.processError(apiResponse)
            }
            return@withContext result
        }

    suspend fun getCachedOrFetchNeoObjects() =
        AppPreferences.getModel(NEO_CACHED_OBJECT) ?: fetch3DaysNeoObjects()


    fun reloadNeoObjectsFromCache(): NeoModels {
        val cachedNeoModel: NeoModels? = AppPreferences.getModel(NEO_CACHED_OBJECT)
        return cachedNeoModel ?: NeoModels(0, sortedMapOf())
    }

    private fun cacheNeoData(neoModels: NeoModels) =
        AppPreferences.putModel(NEO_CACHED_OBJECT, neoModels)

    suspend fun fetchTodayNeoObjects() = fetchNeoObjects(getToday(), getToday())
    suspend fun fetch3DaysNeoObjects() = fetchNeoObjects(getYesterday(), getTomorrow())

    companion object {
        @Volatile
        private var instance: NeoService? = null

        fun getInstance(): NeoService =
            instance ?: synchronized(this) {
                instance ?: NeoService(
                    NeoRepository.getInstance()
                ).also { instance = it }
            }
    }
}
