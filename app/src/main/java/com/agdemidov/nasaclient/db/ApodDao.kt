package com.agdemidov.nasaclient.db

import androidx.room.*
import com.agdemidov.nasaclient.models.ApodModel

@Dao
interface ApodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: ApodModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apodModels: List<ApodModel>)

    @Query("SELECT * FROM apod_models ORDER BY apod_date DESC")
    suspend fun getAll(): List<ApodModel>

    @Query("SELECT * FROM apod_models LIMIT :pageSize OFFSET :firstIndex")
    suspend fun getPage(firstIndex: Int, pageSize: Int): List<ApodModel>

    @Query("DELETE FROM apod_models")
    suspend fun deleteAllApods()

    @Transaction
    suspend fun refreshAll(apodModels: List<ApodModel>) {
        deleteAllApods()
        insertAll(apodModels)
    }
}
