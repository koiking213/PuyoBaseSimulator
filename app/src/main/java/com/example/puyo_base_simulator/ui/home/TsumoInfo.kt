package com.example.puyo_base_simulator.ui.home


data class TsumoInfo(val currentColor: Pair<PuyoColor, PuyoColor>,
                val nextColor:Pair<Pair<PuyoColor,PuyoColor>, Pair<PuyoColor, PuyoColor>>,
                private var column: Int,
                val rot: Rotation) {
    val currentMainPos = Point(1, column) // row, column
    val currentSubPos = when (rot) {
        Rotation.DEGREE0 -> Point(0, column)
        Rotation.DEGREE90 -> Point(1, column+1)
        Rotation.DEGREE180 -> Point(2, column)
        Rotation.DEGREE270 -> Point(1, column-1)
    }
}