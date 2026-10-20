package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WinSoftwareEntity::class], version = 1, exportSchema = false)
abstract class WinDatabase : RoomDatabase() {
    abstract fun winSoftwareDao(): WinSoftwareDao

    companion object {
        @Volatile
        private var INSTANCE: WinDatabase? = null

        fun getInstance(context: Context): WinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WinDatabase::class.java,
                    "winbox10_software.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
