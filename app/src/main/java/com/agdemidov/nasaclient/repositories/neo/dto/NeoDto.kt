package com.agdemidov.nasaclient.repositories.neo.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class NeoDto(
    @SerializedName("links")
    val links: JsonElement? = null,
    @SerializedName("element_count")
    val neoCount: Int = 0,
    @SerializedName("near_earth_objects")
    val nearEarthObjects: Map<String, List<NeoDetailsDto>>? = null
)

data class NeoDetailsDto(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("nasa_jpl_url")
    val nasa_jpl_url: String? = null,
)