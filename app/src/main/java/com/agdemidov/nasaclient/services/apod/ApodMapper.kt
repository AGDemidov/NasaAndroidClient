package com.agdemidov.nasaclient.services.apod

import com.agdemidov.nasaclient.models.ApodModel
import com.agdemidov.nasaclient.repositories.apod.dto.ApodDto

object ApodMapper {
    fun mapApodsList(items: List<ApodDto>): List<ApodModel> {
        return items.map {
            ApodModel(
                title = it.title,
                url = it.url,
                date = it.date,
                explanation = it.explanation,
                hdUrl = it.hdurl,
                mediaType = it.mediaType
            )
        }
    }

    fun mapSingleApod(apod: ApodDto) = apod.run {
        ApodModel(
            title = title,
            url = url,
            date = date,
            explanation = explanation,
            hdUrl = hdurl,
            mediaType = mediaType
        )
    }
}
