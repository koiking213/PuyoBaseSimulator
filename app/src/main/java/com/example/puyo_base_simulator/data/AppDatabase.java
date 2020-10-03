package com.example.puyo_base_simulator.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Base.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BaseDao baseDao();
}

