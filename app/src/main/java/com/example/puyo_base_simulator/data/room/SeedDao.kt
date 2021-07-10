package com.example.puyo_base_simulator.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SeedDao {
    @get:Query("SELECT * FROM SeedEntity")
    val all: List<SeedEntity>

    @Insert
    fun insert(base: SeedEntity)

    @Delete
    fun delete(base: SeedEntity)
}