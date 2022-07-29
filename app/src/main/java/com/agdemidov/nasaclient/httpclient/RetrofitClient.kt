package com.agdemidov.nasaclient.httpclient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val clientInstance: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(NasaApiConstants.NASA_BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }
}