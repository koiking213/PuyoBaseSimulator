package com.example.puyo_base_simulator.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BaseDao {
    @get:Query("SELECT * FROM Base")
    val all: List<Base>

    @Query("SELECT * FROM Base WHERE id = :id LIMIT 1")
    fun findById(id: Int): Base?

    @Query("SELECT * FROM Base WHERE hash LIKE :hash")
    fun findByHash(hash: Int): List<Base>

    @Query("SELECT * FROM Base WHERE hash IN (:hashes)")
    fun findByAllHash(hashes: List<Int>): List<Base>

    @Insert
    fun insert(base: Base)

    @Delete
    fun delete(base: Base)
}