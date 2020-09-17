package com.example.puyo_base_simulator.ui.home;

public class TsumoInfo {
    PuyoColor[] currentColor = new PuyoColor[2];
    PuyoColor[][] nextColor = new PuyoColor[2][2];
    int[] currentMainPos = {1, 3}; // row, column
    int[] currentSubPos = {0, 3};
    Rotation currentCursorRotate;
}
