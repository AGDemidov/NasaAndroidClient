package com.agdemidov.nasaclient.repositories.tle.api

import com.agdemidov.nasaclient.httpclient.RetrofitClient
import com.agdemidov.nasaclient.repositories.tle.dto.TleMembers
import retrofit2.Call
import retrofit2.http.GET

interface TleApiInterface {
    @GET("https://tle.ivanstanojevic.me/api/tle")
    fun FetchTle(): Call<TleMembers>

    companion object{
        val endpoints: TleApiInterface by lazy {
            RetrofitClient.clientInstance
                .build()
                .create(TleApiInterface::class.java)
        }
    }
}