package com.agdemidov.nasaclient.repositories.apod.dto

import com.google.gson.annotations.SerializedName

data class ApodDto(
    @SerializedName("copyright")
    val copyright: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("hdurl")
    val hdurl: String? = null,
    @SerializedName("explanation")
    val explanation: String? = null,
    @SerializedName("media_type")
    val mediaType: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("url")
    val url: String? = null
)
