package com.example.puyo_base_simulator.data

import java.io.Serializable

data class Puyo(var row: Int, var column: Int, var color: PuyoColor) : Serializable