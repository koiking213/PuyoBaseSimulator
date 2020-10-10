package com.example.puyo_base_simulator.ui.home;

import java.util.List;

import androidx.annotation.NonNull;

import static java.lang.Integer.parseInt;

public class Placement {
    int currentCursorColumnIndex;
    Rotation currentCursorRotate;
    int tsumoCounter;
    Placement (int idx, Rotation rot, int counter) {
        currentCursorColumnIndex = idx;
        currentCursorRotate = rot;
        this.tsumoCounter = counter;
    }

    Placement (String str) {
        String[] values = str.split(",");
        currentCursorColumnIndex = parseInt(values[0]);
        currentCursorRotate = Rotation.values()[parseInt(values[1])];
        tsumoCounter = parseInt(values[2]);
    }

    @NonNull
    public String toString() {
        return "" + currentCursorColumnIndex + "," + currentCursorRotate.ordinal() + "," + tsumoCounter + ";";
    }
}
