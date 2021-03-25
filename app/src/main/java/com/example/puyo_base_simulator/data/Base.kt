package com.example.puyo_base_simulator.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Base {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "hash")
    var hash = 0

    @ColumnInfo(name = "placementOrder")
    var placementOrder: String = ""

    @ColumnInfo(name = "field")
    var field: String = ""

    @ColumnInfo(name = "point")
    var point = 0

    @ColumnInfo(name = "allClear")
    var allClear = false

}