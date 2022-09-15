package com.agdemidov.nasaclient.repositories.neo.dto

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class NeoDto(
    @SerializedName("links")
    val links: JsonObject? = null,
    @SerializedName("element_count")
    val neoCount: Int = 0,
    @SerializedName("near_earth_objects")
    val nearEarthObjects: Map<String, List<NeoDetailsDto>>? = null
)

data class NeoDetailsDto(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("nasa_jpl_url")
    val nasaJplUrl: String? = null,
    @SerializedName("absolute_magnitude_h")
    val absoluteMagnitude: Float? = null,
    @SerializedName("estimated_diameter")
    val estimatedDiameter: EstimatedDiameter? = null,
    @SerializedName("is_potentially_hazardous_asteroid")
    val potentiallyHazardous: Boolean? = null,
    @SerializedName("close_approach_data")
    val closeApproachData: List<CloseApproachData>? = null,
)

data class EstimatedDiameter(
    @SerializedName("meters")
    val meters: EstimatedDiameterMinMax? = null,
)

data class EstimatedDiameterMinMax(
    @SerializedName("estimated_diameter_min")
    val min: Double? = null,
    @SerializedName("estimated_diameter_max")
    val max: Double? = null
)

data class CloseApproachData(
    @SerializedName("close_approach_date_full")
    val closeApproachDateFull: String? = null,
    @SerializedName("relative_velocity")
    val relativeVelocity: RelativeVelocity? = null,
    @SerializedName("miss_distance")
    val missDistance: MissDistance? = null,
    @SerializedName("orbiting_body")
    val orbitingBodyObject: String? = null
)

data class RelativeVelocity(
    @SerializedName("kilometers_per_second")
    val min: String? = null,
    @SerializedName("kilometers_per_hour")
    val max: String? = null
)

data class MissDistance(
    @SerializedName("astronomical")
    val min: Double? = null,
    @SerializedName("kilometers")
    val max: Double? = null
)
