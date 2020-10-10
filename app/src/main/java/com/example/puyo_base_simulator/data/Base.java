package com.example.puyo_base_simulator.data;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity
public class Base {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "hash")
    private int hash;

    @ColumnInfo(name = "placementOrder")
    private String placementOrder;

    @ColumnInfo(name = "field")
    private String field;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public int getHash() {
        return hash;
    }

    public void setPlacementOrder(String order) {
        this.placementOrder = order;
    }

    public String getPlacementOrder() {
        return this.placementOrder;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return this.field;
    }
}

