package com.agdemidov.nasaclient.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "apod_models")
data class ApodModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "apod_url")
    var url: String? = null,
    @ColumnInfo(name = "apod_name")
    var title: String? = null,
    @ColumnInfo(name = "apod_date")
    var date: String? = null,
    @Ignore
    var explanation: String? = null,
    @Ignore
    var hdUrl: String? = null,
    @Ignore
    var mediaType: String? = null
)
