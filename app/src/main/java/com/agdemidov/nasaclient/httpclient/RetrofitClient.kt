package com.agdemidov.nasaclient.httpclient

import com.agdemidov.nasaclient.utils.NASA_BASE_API_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val builderInstance: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(NASA_BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }
}
