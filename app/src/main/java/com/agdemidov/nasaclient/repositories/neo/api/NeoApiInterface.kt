package com.agdemidov.nasaclient.repositories.neo.api

import com.agdemidov.nasaclient.repositories.neo.dto.NeoDto
import com.agdemidov.nasaclient.utils.NASA_API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NeoApiInterface {
    @GET("/neo/rest/v1/feed?api_key=${NASA_API_KEY}")
    fun fetchTodayNeoObjects(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): Call<NeoDto>
}
