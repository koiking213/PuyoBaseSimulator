package com.example.puyo_base_simulator.ui.home

enum class PuyoColor(val char: Char) {
    EMPTY(' '),
    RED('r'),
    BLUE('b'),
    YELLOW('y'),
    GREEN('g'),
    PURPLE('p');

    companion object {
        fun getPuyoColor(c: Char) = values().find { it.char == c } ?: EMPTY
    }
}