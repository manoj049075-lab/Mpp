package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "win_software")
data class WinSoftwareEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val exeFileName: String,
    val installPath: String,
    val architecture: String,
    val subsystem: String,
    val fileSizeBytes: Long,
    val requiredRamMb: Int,
    val peHeaderSummary: String,
    val extractedStrings: String,
    val appType: String,
    val isUserUploaded: Boolean = false,
    val keepInBackground: Boolean = true,
    val addedTimestamp: Long = System.currentTimeMillis()
)
