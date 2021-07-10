package com.example.puyo_base_simulator.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SeedEntity {
    @PrimaryKey(autoGenerate = true)
    var seed = 0
}