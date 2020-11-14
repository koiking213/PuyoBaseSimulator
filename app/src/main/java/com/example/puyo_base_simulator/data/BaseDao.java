package com.example.puyo_base_simulator.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BaseDao {
    @Query("SELECT * FROM Base")
    List<Base> getAll();

    @Query("SELECT * FROM Base WHERE id = :id LIMIT 1")
    Base findById(int id);

    @Query("SELECT * FROM Base WHERE hash LIKE :hash")
    List<Base> findByHash(int hash);

    @Insert
    void insert(Base base);

    @Delete
    void delete(Base base);
}
