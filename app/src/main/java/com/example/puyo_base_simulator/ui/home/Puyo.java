package com.example.puyo_base_simulator.ui.home;

public class Puyo {
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
    EMPTY,
    RED,
    BLUE,
    YELLOW,
    GREEN,
    PURPLE
}
