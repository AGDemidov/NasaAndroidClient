package com.agdemidov.nasaclient.repositories.apod.api

import com.agdemidov.nasaclient.repositories.apod.dto.ApodDto
import com.agdemidov.nasaclient.utils.NASA_API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApodApiInterface {
    @GET("/planetary/apod?api_key=${NASA_API_KEY}")
    fun fetchApod(): Call<ApodDto>

    @GET("/planetary/apod")
    fun fetchApodsPage(
        @Query("api_key") apiKey: String = NASA_API_KEY,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): Call<List<ApodDto>>
}
