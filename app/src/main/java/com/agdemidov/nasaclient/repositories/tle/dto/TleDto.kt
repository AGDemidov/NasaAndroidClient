package com.agdemidov.nasaclient.repositories.tle.dto

import com.google.gson.annotations.SerializedName

data class TleMembers(
    @SerializedName("member")
    val members: List<TleMember>
)

data class TleMember(
    @SerializedName("@id")
    val d: String,
    @SerializedName("@type")
    val type: String,
    @SerializedName("satelliteId")
    val satelliteId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("line1")
    val line1: String,
    @SerializedName("line2")
    val linie2: String
)