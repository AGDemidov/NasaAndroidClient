package com.agdemidov.nasaclient.services.neo

import com.agdemidov.nasaclient.models.NeoModel
import com.agdemidov.nasaclient.models.NeoModels
import com.agdemidov.nasaclient.repositories.neo.dto.NeoDto

object NeoMapper {
    fun mapNeoData(neoData: NeoDto): NeoModels {
        val neoMap = sortedMapOf<String, List<NeoModel>>()
        neoData.nearEarthObjects?.let {
            for (date in neoData.nearEarthObjects.keys) {
                val neoModelsList = neoData.nearEarthObjects.getValue(date)
                    .map {
                        NeoModel(it.name ?: "N/A", it.nasa_jpl_url ?: "N/A")
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