package com.example.puyo_base_simulator.ui.home;

import com.example.puyo_base_simulator.BuildConfig;

import java.util.List;
import java.util.Stack;

public class TsumoController {
    String tsumo;
    int tsumoCounter = 0;
    int seed;
    int currentCursorColumnIndex = 3;
    Rotation currentCursorRotate = Rotation.DEGREE0;  // 時計回り
    PuyoColor[] currentColor = new PuyoColor[2];
    PuyoColor[][] nextColor = new PuyoColor[2][2];
    Stack<Placement> placementOrder = new Stack<>();

    public TsumoController(String tsumo, int seed) {
        this.tsumo = tsumo;
        this.seed = seed;
        this.setTsumo();
    }

    void pushPlacementOrder() {
        placementOrder.push(new Placement(currentCursorColumnIndex, currentCursorRotate, tsumoCounter));
    }

    Placement popPlacementOrder() {
        return placementOrder.pop();
    }

    void restorePlacement(Placement plc) {
        tsumoCounter = plc.tsumoCounter;
        this.setTsumo();
        currentCursorColumnIndex = plc.currentCursorColumnIndex;
        currentCursorRotate = plc.currentCursorRotate;
    }

    public String placementOrderToString() {
        StringBuilder str = new StringBuilder();
        // スタックの奥から順に取り出される
        for (Placement p: placementOrder) {
            str.append(p.toString());
        }
        return str.toString();
    }

    void stringToPlacementOrder(String str) {
        placementOrder.clear();
        for (String placementStr : str.split(";")) {
            placementOrder.push(new Placement(placementStr));
        }
    }

    private void setTsumo() {
        currentCursorColumnIndex = 3;
        currentCursorRotate = Rotation.DEGREE0;
        currentColor[1] = getPuyoColor(tsumo.charAt(tsumoCounter));
        currentColor[0] = getPuyoColor(tsumo.charAt(tsumoCounter+1));
        nextColor[0][0] = getPuyoColor(tsumo.charAt(tsumoCounter+2));
        nextColor[0][1] = getPuyoColor(tsumo.charAt(tsumoCounter+3));
        nextColor[1][0] = getPuyoColor(tsumo.charAt(tsumoCounter+4));
        nextColor[1][1] = getPuyoColor(tsumo.charAt(tsumoCounter+5));
    }

    void incrementTsumo() {
        tsumoCounter += 2;
        setTsumo();
    }

    void decrementTsumo() {
        tsumoCounter -= 2;
        setTsumo();
    }

    TsumoInfo makeTsumoInfo() {
        TsumoInfo info = new TsumoInfo();
        info.currentColor[0] = currentColor[0];
        info.currentColor[1] = currentColor[1];
        info.nextColor[0][0] = nextColor[0][0];
        info.nextColor[0][1] = nextColor[0][1];
        info.nextColor[1][0] = nextColor[1][0];
        info.nextColor[1][1] = nextColor[1][1];
        info.currentMainPos[0] = 1;
        info.currentMainPos[1] = currentCursorColumnIndex;
        switch (currentCursorRotate) {
            case DEGREE0:
                info.currentSubPos[0] = 0;
                info.currentSubPos[1] = currentCursorColumnIndex;
                break;
            case DEGREE90:
                info.currentSubPos[0] = 1;
                info.currentSubPos[1] = currentCursorColumnIndex + 1;
                break;
            case DEGREE180:
                info.currentSubPos[0] = 2;
                info.currentSubPos[1] = currentCursorColumnIndex;
                break;
            case DEGREE270:
                info.currentSubPos[0] = 1;
                info.currentSubPos[1] = currentCursorColumnIndex - 1;
                break;
        }
        info.currentCursorRotate = currentCursorRotate;
        return info;
    }

    private PuyoColor getPuyoColor(char c) {
        switch (c) {
            case 'r':
                return PuyoColor.RED;
            case 'b':
                return PuyoColor.BLUE;
            case 'g':
                return PuyoColor.GREEN;
            case 'y':
                return PuyoColor.YELLOW;
            case 'p':
                return PuyoColor.PURPLE;
            default:
                if (BuildConfig.DEBUG) {
                    throw new AssertionError("Assertion failed");
                }
                return PuyoColor.EMPTY;
        }
    }

    // 軸ぷよ
    PuyoColor getMainColor() {
        return currentColor[0];
    }

    // 軸ぷよでは無い方
    PuyoColor getSubColor() {
        return currentColor[1];
    }

    void moveCurrentLeft() {
        if (!(currentCursorColumnIndex == 1 || (currentCursorColumnIndex == 2 && currentCursorRotate == Rotation.DEGREE270))) {
            currentCursorColumnIndex--;
        }
    }

    void moveCurrentRight() {
        if (!(currentCursorColumnIndex == 6 || (currentCursorColumnIndex == 5 && currentCursorRotate == Rotation.DEGREE90))) {
            currentCursorColumnIndex++;
        }
    }

    void rotateCurrentLeft() {
        switch (currentCursorRotate) {
            case DEGREE0:
                currentCursorRotate = Rotation.DEGREE270;
                if (currentCursorColumnIndex == 1) {
                    currentCursorColumnIndex = 2;
                }
                return;
            case DEGREE90:
                currentCursorRotate = Rotation.DEGREE0;
                return;
            case DEGREE180:
                currentCursorRotate = Rotation.DEGREE90;
                if (currentCursorColumnIndex == 6) {
                    currentCursorColumnIndex = 5;
                }
                return;
            case DEGREE270:
                currentCursorRotate = Rotation.DEGREE180;
        }
    }

    void rotateCurrentRight(){
        switch (currentCursorRotate) {
            case DEGREE0:
                currentCursorRotate = Rotation.DEGREE90;
                if (currentCursorColumnIndex == 6) {
                    currentCursorColumnIndex = 5;
                }
                return;
            case DEGREE90:
                currentCursorRotate = Rotation.DEGREE180;
                return;
            case DEGREE180:
                currentCursorRotate = Rotation.DEGREE270;
                if (currentCursorColumnIndex == 1) {
                    currentCursorColumnIndex = 2;
                }
                return;
            case DEGREE270:
                currentCursorRotate = Rotation.DEGREE0;
        }
    }
}
