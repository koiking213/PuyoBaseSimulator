package com.example.puyo_base_simulator.ui.home;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Field implements Cloneable {
    Puyo[][] field;
    int[] heights = {0,0,0,0,0,0,0};
    ArrayList<Puyo> disappearPuyo;
    int accumulatedPoint;
    int bonus;
    int colorBonus;
    int chainNum;
    int connectionBonus;
    final int[] chainBonusConstant = {0, 0, 8, 16, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 480, 512};
    final int[] colorBonusConstant = {0, 0, 3, 6, 12};
    int connectionBonusConstant(int connectionNum) {
        if (connectionNum <= 4) {
            return 0;
        } else if (connectionNum <= 10){
            return connectionNum - 3;
        } else {
            return 10;
        }
    }

    @NonNull
    @Override
    public Field clone() {
        Field cloned = new Field();
        try {
            cloned = (Field) super.clone();
            cloned.field = this.field.clone();
            for (int i=1; i<13; i++) {
                cloned.field[i] = this.field[i].clone();
            }
            cloned.heights = this.heights.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cloned;
    }

    Field () {
        field = new Puyo[14][7];
        for (int i=1; i<14; i++) {
            for (int j=1; j<7; j++) {
                field[i][j] = new Puyo(i, j, PuyoColor.EMPTY);
            }
        }
    }

    Boolean addPuyo(int column, PuyoColor color) {
        int row = heights[column] + 1;
        if (row == 14) return false;

        field[row][column] = new Puyo(row, column, color);
        heights[column]++;
        return true;
    }

    // todo: evalChainはこっちにして元のevalChainは消す
    //       現状連鎖しながら描画しているが、先に連鎖結果をすべて保持するようにする
    List<Field> getFieldTransition() {
        Field nextField = evalChain(this.chainNum+1, 0).nextField;

        ArrayList<Puyo> disappear = new ArrayList<>();
        Field newField = new Field();
        Set<PuyoColor> colors = new HashSet<>();
        int connectionBonus = 0;
        // 消えるぷよを探す
        for (int i=1; i<13; i++) {
            for (int j=1; j<7; j++) {
                Puyo puyo = field[i][j];
                List<Puyo> connection = getConnection(puyo);
                if (connection.size() >= 4) {
                    disappear.add(puyo);
                    // 色ボーナスの評価
                    colors.add(puyo.color);
                    // 連結ボーナスの評価
                    boolean connectionIsNew = true;
                    for (Puyo p: disappear) {
                        if (p.row < i || p.row == i && p.column < j) {
                            connectionIsNew = false;
                            break;
                        }
                    }
                    if (connectionIsNew) {
                        connectionBonus += connectionBonusConstant(connection.size());
                    }
                } else if (puyo.color != PuyoColor.EMPTY){
                    newField.addPuyo(j, puyo.color);
                }
            }
        }
        int bonus = colorBonusConstant[colors.size()] + connectionBonus + chainBonusConstant[chainNum];
        if (bonus == 0) bonus = 1;
        int point = accumulatedPoint + bonus * disappear.size() * 10;

        this.colorBonus = colorBonusConstant[colors.size()];
        this.connectionBonus = connectionBonus;
        this.bonus = bonus;
        this.disappearPuyo = disappear;
        this.accumulatedPoint = point;
        List<Field> ret = newField.getFieldTransition();
        ret.add(0, this);
        return ret;
    }

    FieldEvaluation evalChain(int chainNum, int accumulatedPoint) {
        ArrayList<Puyo> disappear = new ArrayList<>();
        Field newField = new Field();
        Set<PuyoColor> colors = new HashSet<>();
        int connectionBonus = 0;
        // 消えるぷよを探す
        for (int i=1; i<13; i++) {
            for (int j=1; j<7; j++) {
                Puyo puyo = field[i][j];
                List<Puyo> connection = getConnection(puyo);
                if (connection.size() >= 4) {
                    disappear.add(puyo);
                    // 色ボーナスの評価
                    colors.add(puyo.color);
                    // 連結ボーナスの評価
                    boolean connectionIsNew = true;
                    for (Puyo p: disappear) {
                        if (p.row < i || p.row == i && p.column < j) {
                            connectionIsNew = false;
                            break;
                        }
                    }
                    if (connectionIsNew) {
                        connectionBonus += connectionBonusConstant(connection.size());
                    }
                } else if (puyo.color != PuyoColor.EMPTY){
                    newField.addPuyo(j, puyo.color);
                }
            }
        }
        int bonus = colorBonusConstant[colors.size()] + connectionBonusConstant(connectionBonus) + chainBonusConstant[chainNum];
        if (bonus == 0) bonus = 1;
        int point = accumulatedPoint + bonus * disappear.size() * 10;

        return new FieldEvaluation(newField, disappear, point, bonus);
    }

    List<Puyo> getNeighborPuyo(Puyo puyo) {
        int row = puyo.row;
        int column = puyo.column;
        List<Puyo> ret = new ArrayList<>();
        // left
        if (column != 1 && field[row][column-1].color != PuyoColor.EMPTY) {
            ret.add(field[row][column-1]);
        }
        // right
        if (column != 6 && field[row][column+1].color != PuyoColor.EMPTY) {
            ret.add(field[row][column+1]);
        }
        // up
        if (row != 12 && field[row+1][column].color != PuyoColor.EMPTY) {
            ret.add(field[row+1][column]);
        }
        // down
        if (row != 1 && field[row-1][column].color != PuyoColor.EMPTY) {
            ret.add(field[row-1][column]);
        }
        return ret;
    }

    // 連結数
    List<Puyo> getConnection(Puyo puyo) {
        ArrayList<Puyo> connected = new ArrayList<>();
        if (puyo.color == PuyoColor.EMPTY) return connected;
        Stack<Puyo> sameColorStack = new Stack<>();
        sameColorStack.push(puyo);
        connected.add(puyo);
        while (!sameColorStack.isEmpty()) {
            Puyo currentPuyo = sameColorStack.pop();
            List<Puyo> neighbors = getNeighborPuyo(currentPuyo);
            for (Puyo p: neighbors) {
                if (p.color == puyo.color && !connected.contains(p)) {
                    sameColorStack.push(p);
                    connected.add(p);
                }
            }
        }
        return connected;
    }
}
