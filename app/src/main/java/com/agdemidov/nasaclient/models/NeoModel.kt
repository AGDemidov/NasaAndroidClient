package com.agdemidov.nasaclient.models

import java.util.*

data class NeoModels(
    val count: Int,
    val neoModelsMap: SortedMap<String, List<NeoModel>>
)

data class NeoModel(
    val name: String,
    val nasa_jpl_url: String
)