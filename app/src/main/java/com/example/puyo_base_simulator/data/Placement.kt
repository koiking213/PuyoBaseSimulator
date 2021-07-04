package com.example.puyo_base_simulator.data

import com.example.puyo_base_simulator.utils.Rotation

class Placement(val currentCursorColumnIndex: Int,
                var currentCursorRotate: Rotation,
                var tsumoCounter: Int) {

    companion object {
        @JvmStatic
        fun from(str: String): Placement {
            val values = str.split(",").toTypedArray()
            val idx = values[0].toInt()
            val rotate = Rotation.values()[values[1].toInt()]
            val counter = values[2].toInt()
            return Placement(idx, rotate, counter)
        }
    }

    override fun toString(): String {
        return "" + currentCursorColumnIndex + "," + currentCursorRotate.ordinal + "," + tsumoCounter
    }
}