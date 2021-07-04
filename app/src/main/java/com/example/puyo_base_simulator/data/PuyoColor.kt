package com.example.puyo_base_simulator.data

enum class PuyoColor(val char: Char) {
    EMPTY(' '),
    RED('r'),
    BLUE('b'),
    YELLOW('y'),
    GREEN('g'),
    PURPLE('p'),
    DISAPPEAR('*');

    companion object {
        fun getPuyoColor(c: Char) = values().find { it.char == c } ?: EMPTY
    }
}