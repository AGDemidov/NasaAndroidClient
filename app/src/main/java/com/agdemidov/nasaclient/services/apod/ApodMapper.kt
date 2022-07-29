package com.agdemidov.nasaclient.services.apod

import com.agdemidov.nasaclient.models.ApodModel
import com.agdemidov.nasaclient.repositories.apod.dto.ApodDto

object ApodMapper {
    fun mapApodsList(items: List<ApodDto>): List<ApodModel> {
        return items.map {
            ApodModel(
                it.title ?: "N/A",
                it.url ?: "N/A",
                it.date ?: "N/A",
            )
        }
    }

    fun mapApod(apod: ApodDto) = apod.run {
        ApodModel(
            title ?: "N/A",
            url ?: "N/A",
            date ?: "N/A"
        )
    }
}