package com.agdemidov.nasaclient.models

import androidx.room.*

@Entity(tableName = "apod_models", indices = [Index(value = ["apod_url"], unique = true)])
data class ApodModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "apod_url")
    var url: String? = null,
    @ColumnInfo(name = "apod_name")
    var title: String? = null,
    @ColumnInfo(name = "apod_date")
    var date: String? = null,
    @ColumnInfo(name = "apod_explanation")
    var explanation: String? = null,
    @Ignore
    var hdUrl: String? = null,
    @Ignore
    var mediaType: String? = null
)
