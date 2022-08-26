package com.agdemidov.nasaclient.services.neo

import com.agdemidov.nasaclient.models.CloseApproachData
import com.agdemidov.nasaclient.models.NeoModel
import com.agdemidov.nasaclient.models.NeoModels
import com.agdemidov.nasaclient.repositories.neo.dto.NeoDto

object NeoMapper {
    fun mapNeoData(neoData: NeoDto): NeoModels {
        val neoMap = sortedMapOf<String, List<NeoModel>>()
        neoData.nearEarthObjects?.let {
            for (date in neoData.nearEarthObjects.keys) {
                val neoModelsList = neoData.nearEarthObjects.getValue(date).map { dto ->
                    NeoModel(
                        name = dto.name ?: "N/A",
                        nasa_jpl_url = dto.nasaJplUrl ?: "N/A",
                        absoluteMagnitude = dto.absoluteMagnitude?.toString() ?: "N/A",
                        estimatedDiameterMetersMin = dto.estimatedDiameter?.meters?.min?.toString()
                            ?: "N/A",
                        estimatedDiameterMetersMax = dto.estimatedDiameter?.meters?.max?.toString()
                            ?: "N/A",
                        potentiallyHazardous = dto.potentiallyHazardous,
                        closeApproachData = dto.closeApproachData?.getOrNull(0)?.let {
                            CloseApproachData(
                                it.closeApproachDateFull ?: "N/A",
                                it.relativeVelocity?.min ?: "N/A",
                                it.relativeVelocity?.max ?: "N/A",
                                it.missDistance?.min?.toString() ?: "N/A",
                                it.missDistance?.max?.toString() ?: "N/A",
                            )
                        }
                    )
                }
                neoMap.putIfAbsent(date, neoModelsList)
            }
        }
        return NeoModels(
            neoData.neoCount,
            neoMap
        )
    }
}