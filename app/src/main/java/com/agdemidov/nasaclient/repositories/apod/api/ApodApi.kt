package com.agdemidov.nasaclient.repositories.apod.api

import com.agdemidov.nasaclient.httpclient.NasaApiConstants
import com.agdemidov.nasaclient.repositories.apod.dto.ApodDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApodApi {
    @GET("/planetary/apod?api_key=${NasaApiConstants.NASA_API_KEY}")
    fun fetchApod(): Call<ApodDto>

    @GET("/planetary/apod")
    fun fetchApodsPage(
        @Query("api_key") apiKey: String = NasaApiConstants.NASA_API_KEY,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): Call<List<ApodDto>>
}