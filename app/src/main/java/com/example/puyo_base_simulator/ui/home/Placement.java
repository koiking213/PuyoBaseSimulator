package com.example.puyo_base_simulator.ui.home;

public class Placement {
    int currentCursorColumnIndex;
    Rotation currentCursorRotate;
    int tsumoCounter;
    Placement (int idx, Rotation rot, int counter) {
        currentCursorColumnIndex = idx;
        currentCursorRotate = rot;
        this.tsumoCounter = counter;
    }
}
