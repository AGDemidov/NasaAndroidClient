package com.agdemidov.nasaclient.services.apod

import android.content.Context
import android.util.Log
import com.agdemidov.nasaclient.db.ApodDao
import com.agdemidov.nasaclient.db.AppDatabase
import com.agdemidov.nasaclient.models.ApodModel
import com.agdemidov.nasaclient.repositories.*
import com.agdemidov.nasaclient.repositories.apod.ApodRepository
import com.agdemidov.nasaclient.services.*
import com.agdemidov.nasaclient.utils.DateGetter
import com.agdemidov.nasaclient.utils.PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApodService private constructor(
    private val repository: ApodRepository, private val apodDao: ApodDao
) {

    private val TAG = ApodService::class.simpleName

    private var apodItemsList: MutableList<ApodModel> = mutableListOf()

    suspend fun fetchApods(isPTR: Boolean): List<ApodModel> = withContext(Dispatchers.IO) {
        val currentPage = when (isPTR) {
            true -> 0
            else -> apodItemsList.size / PAGE_SIZE
        }

        val range = calculateStartEndDatesForPage(currentPage)
        val start = range.first
        val end = range.second
        return@withContext when (val apiResponse = repository.fetchApodsList(start, end)) {
            is SuccessResponse -> {
                runCatching { ApodMapper.mapApodsList(apiResponse.body) }
                    .onSuccess {
                        if (isPTR) {
                            apodItemsList = it.toMutableList()
                        } else {
                            if(!apodItemsList.containsAll(it)) {
                                apodItemsList.addAll(it)
                            }
                        }
                        cacheApods(isPTR, it)
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

    suspend fun reloadAllApodsFromCache(): List<ApodModel> {
        apodItemsList.clear()
        apodItemsList.addAll(apodDao.getAll())
        Log.i(TAG, "reloadAllApodsFromCache: ${apodItemsList.size}")
        return apodItemsList
    }

    private suspend fun cacheApods(isPTR: Boolean, items: List<ApodModel>) {
        if (isPTR) {
            Log.i(TAG, "delete all cached data and add items: ${items.size}")
            apodDao.refreshAll(items)
        } else {
            Log.i(TAG, "add cached items: ${items.size}")
            apodDao.insertAll(items)
        }
    }

    private fun calculateStartEndDatesForPage(page: Int): Pair<String, String> {
        val startDateOffset = -(PAGE_SIZE * (page + 1))
        val endDateOffset = when (page) {
            0 -> 0
            else -> startDateOffset + PAGE_SIZE - 1
        }
        val startDate = DateGetter.getDateWithOffset(startDateOffset)
        val endDate = DateGetter.getDateWithOffset(endDateOffset)
        Log.e(TAG, "StartDay: $startDate EndDay:$endDate")
        return Pair(startDate, endDate)
    }

    companion object {
        @Volatile
        private var instance: ApodService? = null

        fun getInstance(context: Context): ApodService =
            instance ?: synchronized(this) {
                instance ?: ApodService(
                    ApodRepository.getInstance(),
                    AppDatabase.getInstance(context).apodDao()
                ).also { instance = it }
            }
    }
}
