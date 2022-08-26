package com.agdemidov.nasaclient.models

import java.util.*

data class NeoModels(
    val count: Int,
    val neoModelsMap: SortedMap<String, List<NeoModel>>
)

data class NeoModel(
    val name: String,
    val nasa_jpl_url: String,
    val absoluteMagnitude: String,
    val estimatedDiameterMetersMin: String,
    val estimatedDiameterMetersMax: String,
    val potentiallyHazardous: Boolean?,
    val closeApproachData: CloseApproachData? = null,
    var isExpanded: Boolean = false,
)

data class CloseApproachData(
    val closeApproachDateFull: String,
    val relativeVelocityMin: String,
    val relativeVelocityMax: String,
    val missDistanceMin: String,
    val missDistanceMax: String
)