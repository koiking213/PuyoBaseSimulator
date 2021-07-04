package com.example.puyo_base_simulator.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Base::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun baseDao(): BaseDao
}