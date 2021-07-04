package com.example.puyo_base_simulator.utils

import com.example.puyo_base_simulator.R
import com.example.puyo_base_simulator.data.PuyoColor


fun getColor(c: Char): Int {
    return when (c) {
        'r' -> R.drawable.pr
        'b' -> R.drawable.pb
        'g' -> R.drawable.pg
        'y' -> R.drawable.py
        'p' -> R.drawable.pp
        else -> R.drawable.blank
    }
}


fun puyoResourceId(color: PuyoColor) : Int {
    return when(color) {
        PuyoColor.RED -> R.drawable.pr
        PuyoColor.BLUE -> R.drawable.pb
        PuyoColor.GREEN -> R.drawable.pg
        PuyoColor.YELLOW -> R.drawable.py
        PuyoColor.PURPLE -> R.drawable.pp
        PuyoColor.EMPTY -> R.drawable.blank
        PuyoColor.DISAPPEAR -> R.drawable.disappear
    }
}
