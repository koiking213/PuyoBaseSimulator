package com.example.puyo_base_simulator.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;

import com.example.puyo_base_simulator.BuildConfig;
import com.example.puyo_base_simulator.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

enum Rotation {
    DEGREE0,
    DEGREE90,
    DEGREE180,
    DEGREE270
}

enum PuyoColor {
    EMPTY,
    RED,
    BLUE,
    YELLOW,
    GREEN,
    PURPLE
}

class Puyo {
    int row;
    int column;
    PuyoColor color;
    Puyo(int row, int column, PuyoColor color) {
        this.row = row;
        this.column = column;
        this.color = color;
    }
}

class FieldEvaluation {
    Field nextField;
    ArrayList<Puyo> disappearPuyo;
    FieldEvaluation(Field nextField, ArrayList<Puyo> disappearPuyo) {
        this.nextField = nextField;
        this.disappearPuyo = disappearPuyo;
    }
}

// TODO: public
class Field {
    Puyo[][] field;
    int[] heights = {0,0,0,0,0,0,0};
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
    // 4つくっついたぷよは消える
    FieldEvaluation evalChain() {
        ArrayList<Puyo> disappear = new ArrayList<>();
        Field newField = new Field();
        // 消えるぷよを探す
        for (int i=1; i<13; i++) {
            for (int j=1; j<7; j++) {
                Puyo puyo = field[i][j];
                if (getConnectionCount(puyo) >= 4) {
                    disappear.add(puyo);
                } else if (puyo.color != PuyoColor.EMPTY){
                    newField.addPuyo(j, puyo.color);
                }
            }
        }

        // todo: 点数計算
        return new FieldEvaluation(newField, disappear);
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
    int getConnectionCount(Puyo puyo) {
        if (puyo.color == PuyoColor.EMPTY) return 0;
        Stack<Puyo> sameColorStack = new Stack<>();
        sameColorStack.push(puyo);
        ArrayList<Puyo> connected = new ArrayList<>();
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
        return connected.size();
    }
}
public class HomeFragment extends Fragment {
    ImageView[][] currentPuyoView;
    ImageView[][] fieldView;
    Integer currentCursorColumnIndex = 3;
    Rotation currentCursorRotate = Rotation.DEGREE0;
    PuyoColor[] currentColor = new PuyoColor[2];
    Field currentField = new Field();
    GridLayout currentPuyoLayout;

    // ツモはいずれ完全ランダムではなくすので暫定
    private static final List<PuyoColor> PUYO_VALUES =
            Collections.unmodifiableList(Arrays.asList(PuyoColor.values()));
    private static final Random RANDOM = new Random();
    public static PuyoColor getRandomPuyo() {
        return PUYO_VALUES.get(RANDOM.nextInt(4) + 1);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // current puyo area
        currentPuyoLayout = root.findViewById(R.id.currentPuyoLayout);
        currentPuyoView = new ImageView[3][7];
        for(int i=0;i<3;i++){
            for(int j=0;j<7;j++){
                ImageView view = new ImageView(getActivity());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                view.setLayoutParams(params);
                view.setImageResource(R.drawable.blank);
                currentPuyoLayout.addView(view);
                currentPuyoView[i][j] = view;
            }
        }

        // main field
        final GridLayout fieldLayout = root.findViewById(R.id.fieldLayout);
        fieldView = new ImageView[14][8];
        for(int i=0;i<14;i++){
            for(int j=0;j<8;j++){
                ImageView view = new ImageView(getActivity());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                view.setLayoutParams(params);
                fieldLayout.addView(view);
                fieldView[13-i][j] = view;
            }
        }

        // wall
        for (int i=0; i<14; i++) {
            fieldView[i][0].setImageResource(R.drawable.wall);
            fieldView[i][7].setImageResource(R.drawable.wall);
        }
        for (int j=1; j<7; j++) {
            fieldView[0][j].setImageResource(R.drawable.wall);
        }

        // next puyo
        currentColor[0] = getRandomPuyo();
        currentColor[1] = getRandomPuyo();
        drawCurrentPuyo();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Activity activity = getActivity();
        assert activity != null;
        Button buttonLeft = (Button)activity.findViewById(R.id.buttonLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(currentCursorColumnIndex == 1 || (currentCursorColumnIndex == 2 && currentCursorRotate == Rotation.DEGREE270))) {
                    currentCursorColumnIndex--;
                }
                drawCurrentPuyo();
            }
        });
        Button buttonRight = (Button)activity.findViewById(R.id.buttonRight);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(currentCursorColumnIndex == 6 || (currentCursorColumnIndex == 5 && currentCursorRotate == Rotation.DEGREE90))) {
                    currentCursorColumnIndex++;
                }
                drawCurrentPuyo();
            }
        });
        Button buttonDown = (Button)activity.findViewById(R.id.buttonDown);
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentCursorRotate) {
                    case DEGREE0:
                        // jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, currentColor[0]);
                        // non-jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, currentColor[1]);
                        break;
                    case DEGREE90:
                        // jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, currentColor[0]);
                        // non-jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex+1, currentColor[1]);
                        break;
                    case DEGREE180:
                        // 上下が逆転している
                        // non-jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, currentColor[1]);
                        // jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, currentColor[0]);
                        break;
                    case DEGREE270:
                        // jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, currentColor[0]);
                        // non-jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex-1, currentColor[1]);
                        break;
                }
                drawField(currentField);
                evaluateFieldRecursively();

            }
        });

        Button buttonA = (Button)activity.findViewById(R.id.buttonA);
        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentCursorRotate) {
                    case DEGREE0:
                        currentCursorRotate = Rotation.DEGREE90;
                        if (currentCursorColumnIndex == 6) {
                            currentCursorColumnIndex = 5;
                        }
                        break;
                    case DEGREE90:
                        currentCursorRotate = Rotation.DEGREE180;
                        break;
                    case DEGREE180:
                        currentCursorRotate = Rotation.DEGREE270;
                        if (currentCursorColumnIndex == 1) {
                            currentCursorColumnIndex = 2;
                        }
                        break;
                    case DEGREE270:
                        currentCursorRotate = Rotation.DEGREE0;
                        break;
                }
                drawCurrentPuyo();
            }
        });

        Button buttonB = (Button)activity.findViewById(R.id.buttonB);
        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentCursorRotate) {
                    case DEGREE0:
                        currentCursorRotate = Rotation.DEGREE270;
                        if (currentCursorColumnIndex == 1) {
                            currentCursorColumnIndex = 2;
                        }
                        break;
                    case DEGREE90:
                        currentCursorRotate = Rotation.DEGREE0;
                        break;
                    case DEGREE180:
                        currentCursorRotate = Rotation.DEGREE90;
                        if (currentCursorColumnIndex == 6) {
                            currentCursorColumnIndex = 5;
                        }
                        break;
                    case DEGREE270:
                        currentCursorRotate = Rotation.DEGREE180;
                        break;
                }
                drawCurrentPuyo();
            }
        });

    }

    public void evaluateFieldRecursively() {
        final Activity activity = getActivity();
        assert activity != null;
        new Thread(new Runnable(){
            @Override public void run() {
                FieldEvaluation fieldEvaluation = currentField.evalChain();
                if (fieldEvaluation.disappearPuyo.size() != 0) {
                    activity.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Button buttonDown = (Button)activity.findViewById(R.id.buttonDown);
                            buttonDown.setEnabled(false);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    currentField = fieldEvaluation.nextField;
                    activity.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            drawField(currentField);
                        }
                    });
                    evaluateFieldRecursively();
                } else {
                    activity.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Button buttonDown = (Button)activity.findViewById(R.id.buttonDown);
                            buttonDown.setEnabled(true);
                        }
                    });
                    // get next puyo
                    currentCursorColumnIndex = 3;
                    currentCursorRotate = Rotation.DEGREE0;
                    currentColor[0] = getRandomPuyo();
                    currentColor[1] = getRandomPuyo();
                    drawCurrentPuyo();
                }
            }
        }).start();
    }

    public void drawField(Field field) {
        // draw field puyo
        for (int i=1; i<14; i++) {
            for (int j=1; j<7; j++) {
                Puyo puyo = field.field[i][j];
                fieldView[i][j].setImageResource(getPuyoImage(puyo.color));
            }
        }
    }

    // id型みたいなのはない？
    public int getPuyoImage(PuyoColor color) {
        switch (color) {
            case RED:
                return R.drawable.pr;
            case BLUE:
                return R.drawable.pb;
            case YELLOW:
                return R.drawable.py;
            case GREEN:
                return R.drawable.pg;
            case PURPLE:
                return R.drawable.pp;
            case EMPTY:
                return R.drawable.blank;
            default:
                return -1;
        }
    }

    public int getDotImage(PuyoColor color) {
        switch (color) {
            case RED:
                return R.drawable.dotr;
            case BLUE:
                return R.drawable.dotb;
            case YELLOW:
                return R.drawable.doty;
            case GREEN:
                return R.drawable.dotg;
            case PURPLE:
                return R.drawable.dotp;
            default:
                return -1;
        }
    }


    public void drawCurrentPuyo() {
        drawField(currentField);
        // clear
        for (int i=0; i<3; i++) {
            for (int j=0; j<7; j++) {
                currentPuyoView[i][j].setImageResource(R.drawable.blank);
            }
        }
        int jikuColor = getPuyoImage(currentColor[0]);
        int nonJikuColor = getPuyoImage(currentColor[1]);
        // draw jiku-puyo
        currentPuyoView[1][currentCursorColumnIndex].setImageResource(jikuColor);

        // draw not-jiku-puyo
        switch (currentCursorRotate) {
            case DEGREE0:
                currentPuyoView[0][currentCursorColumnIndex].setImageResource(nonJikuColor);
                drawDot(currentCursorColumnIndex, Arrays.asList(currentColor[0], currentColor[1]), currentField);
                break;
            case DEGREE90:
                if (BuildConfig.DEBUG && currentCursorColumnIndex == 6) {
                    throw new AssertionError("Assertion failed");
                }
                currentPuyoView[1][currentCursorColumnIndex + 1].setImageResource(nonJikuColor);
                drawDot(currentCursorColumnIndex, Collections.singletonList(currentColor[0]), currentField);
                drawDot(currentCursorColumnIndex + 1, Collections.singletonList(currentColor[1]), currentField);
                break;
            case DEGREE180:
                currentPuyoView[2][currentCursorColumnIndex].setImageResource(nonJikuColor);
                drawDot(currentCursorColumnIndex, Arrays.asList(currentColor[1], currentColor[0]), currentField);
                break;
            case DEGREE270:
                if (BuildConfig.DEBUG && currentCursorColumnIndex == 1) {
                    throw new AssertionError("Assertion failed");
                }
                currentPuyoView[1][currentCursorColumnIndex - 1].setImageResource(nonJikuColor);
                drawDot(currentCursorColumnIndex, Collections.singletonList(currentColor[0]), currentField);
                drawDot(currentCursorColumnIndex - 1, Collections.singletonList(currentColor[1]), currentField);
                break;
        }
    }

    // 内部関数にできない？ (Javaにあるのか？)
    public void drawDot(int column, List<PuyoColor> colors, Field field) {
        int row = field.heights[column] + 1;
        for (PuyoColor color : colors) {
            fieldView[row++][column].setImageResource(getDotImage(color));
        }
    }
}