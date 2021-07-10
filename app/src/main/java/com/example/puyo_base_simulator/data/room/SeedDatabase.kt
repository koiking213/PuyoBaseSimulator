package com.example.puyo_base_simulator.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SeedEntity::class], version = 1, exportSchema = false)
abstract class SeedDatabase : RoomDatabase() {
    abstract fun seedDao(): SeedDao
}