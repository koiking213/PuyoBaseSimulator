package com.example.puyo_base_simulator.ui.home

enum class PuyoColor(val char: Char) {
    EMPTY(' '), RED('r'), BLUE('b'), YELLOW('y'), GREEN('g'), PURPLE('p');

    companion object {
        @JvmStatic
        fun getPuyoColor(c: Char): PuyoColor? {
            val types = values()
            for (pc in types) {
                if (pc.char == c) {
                    return pc
                }
            }
            return null
        }
    }
}