package com.example.puyo_base_simulator.ui.home;

import java.io.Serializable;

public class Puyo implements Serializable {
    int row;
    int column;
    PuyoColor color;
    Puyo(int row, int column, PuyoColor color) {
        this.row = row;
        this.column = column;
        this.color = color;
    }
}

enum PuyoColor {
    EMPTY(' '),
    RED('r'),
    BLUE('b'),
    YELLOW('y'),
    GREEN('g'),
    PURPLE('p')
    ;

    private final char c;

    PuyoColor(char c) {
        this.c = c;
    }

    public char getChar() {
        return this.c;
    }

    public static PuyoColor getPuyoColor(final char c) {
        PuyoColor[] types = PuyoColor.values();
        for (PuyoColor pc : types) {
            if (pc.getChar() == c) {
                return pc;
            }
        }
        return null;
    }
}
