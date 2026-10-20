package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WinSoftwareDao {
    @Query("SELECT * FROM win_software ORDER BY isUserUploaded DESC, addedTimestamp DESC")
    fun getAllSoftware(): Flow<List<WinSoftwareEntity>>

    @Query("SELECT COUNT(*) FROM win_software")
    suspend fun getSoftwareCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoftware(software: WinSoftwareEntity): Long

    @Update
    suspend fun updateSoftware(software: WinSoftwareEntity)

    @Query("DELETE FROM win_software WHERE id = :id")
    suspend fun deleteSoftwareById(id: Long)
}
