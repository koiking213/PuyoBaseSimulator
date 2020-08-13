package com.example.puyododaisim.ui.home;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.gridlayout.widget.GridLayout;

import com.example.puyododaisim.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
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
    Puyo field[][];
    int heights[] = {0,0,0,0,0,0,0};
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
        List<Puyo> ret = new ArrayList<Puyo>();
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
        Stack<Puyo> sameColorStack = new Stack<Puyo>();
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
    PuyoColor currentColor[] = new PuyoColor[2];
    Field field = new Field();
    GridLayout currentPuyoLayout;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
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
        currentPuyoView[1][3].setImageResource(R.drawable.pr);
        currentPuyoView[0][3].setImageResource(R.drawable.pb);
        currentColor[0] = PuyoColor.RED;
        currentColor[1] = PuyoColor.BLUE;
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Button buttonLeft = (Button)getActivity().findViewById(R.id.buttonLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCursorColumnIndex == 1 || (currentCursorColumnIndex == 2 && currentCursorRotate == Rotation.DEGREE270)) {
                    // do nothing
                } else {
                    currentCursorColumnIndex--;
                }
                drawCurrentPuyo();
            }
        });
        Button buttonRight = (Button)getActivity().findViewById(R.id.buttonRight);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCursorColumnIndex == 6 || (currentCursorColumnIndex == 5 && currentCursorRotate == Rotation.DEGREE90)) {
                    // do nothing
                } else {
                    currentCursorColumnIndex++;
                }
                drawCurrentPuyo();
            }
        });
        Button buttonDown = (Button)getActivity().findViewById(R.id.buttonDown);
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentCursorRotate) {
                    case DEGREE0:
                        // jiku puyo
                        field.addPuyo(currentCursorColumnIndex, currentColor[0]);
                        // non-jiku puyo
                        field.addPuyo(currentCursorColumnIndex, currentColor[1]);
                        break;
                    case DEGREE90:
                        // jiku puyo
                        field.addPuyo(currentCursorColumnIndex, currentColor[0]);
                        // non-jiku puyo
                        field.addPuyo(currentCursorColumnIndex+1, currentColor[1]);
                        break;
                    case DEGREE180:
                        // 上下が逆転している
                        // non-jiku puyo
                        field.addPuyo(currentCursorColumnIndex, currentColor[1]);
                        // jiku puyo
                        field.addPuyo(currentCursorColumnIndex, currentColor[0]);
                        break;
                    case DEGREE270:
                        // jiku puyo
                        field.addPuyo(currentCursorColumnIndex, currentColor[0]);
                        // non-jiku puyo
                        field.addPuyo(currentCursorColumnIndex-1, currentColor[1]);
                        break;
                }
                drawField();
                evaluateFieldRecursively();

            }
        });

        Button buttonA = (Button)getActivity().findViewById(R.id.buttonA);
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

        Button buttonB = (Button)getActivity().findViewById(R.id.buttonB);
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
        new Thread(new Runnable(){
            @Override public void run() {
                FieldEvaluation fieldEvaluation = field.evalChain();
                if (fieldEvaluation.disappearPuyo.size() != 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    field = fieldEvaluation.nextField;
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            drawField();
                            currentPuyoLayout.invalidate(); // いる？
                        }
                    });
                    evaluateFieldRecursively();
                } else {
                    // todo: ボタンの有効化
                    // get next puyo
                    currentCursorColumnIndex = 3;
                    currentCursorRotate = Rotation.DEGREE0;
                    drawCurrentPuyo();
                }
            }
        }).start();
    }

    // todo: fieldに持たせるか、fieldを引数に取る?
    public void drawField() {
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
        drawField();
        // clear
        for (int i=0; i<3; i++) {
            for (int j=0; j<7; j++) {
                currentPuyoView[i][j].setImageResource(R.drawable.blank);
            }
        }
        int jikuColor = getPuyoImage(PuyoColor.RED);
        int nonJikuColor = getPuyoImage(PuyoColor.BLUE);
        // draw jiku-puyo
        currentPuyoView[1][currentCursorColumnIndex].setImageResource(jikuColor);

        int row = field.heights[currentCursorColumnIndex] + 1;
        // draw not-jiku-puyo
        switch (currentCursorRotate) {
            case DEGREE0:
                currentPuyoView[0][currentCursorColumnIndex].setImageResource(nonJikuColor);
                drawDot(row, currentCursorColumnIndex, PuyoColor.RED);
                drawDot(row+1, currentCursorColumnIndex, PuyoColor.BLUE);
                break;
            case DEGREE90:
                assert(currentCursorColumnIndex != 5);
                currentPuyoView[1][currentCursorColumnIndex+1].setImageResource(nonJikuColor);
                drawDot(row, currentCursorColumnIndex, PuyoColor.RED);
                drawDot(row, currentCursorColumnIndex+1, PuyoColor.BLUE);
                break;
            case DEGREE180:
                currentPuyoView[2][currentCursorColumnIndex].setImageResource(nonJikuColor);
                drawDot(row+1, currentCursorColumnIndex, PuyoColor.RED);
                drawDot(row, currentCursorColumnIndex, PuyoColor.BLUE);
                break;
            case DEGREE270:
                assert(currentCursorColumnIndex != 0);
                currentPuyoView[1][currentCursorColumnIndex-1].setImageResource(nonJikuColor);
                drawDot(row, currentCursorColumnIndex, PuyoColor.RED);
                drawDot(row, currentCursorColumnIndex-1, PuyoColor.BLUE);
                break;
        }
    }

    // 内部関数にできない？ (Javaにあるのか？)
    public void drawDot(int row, int column, PuyoColor color) {
        fieldView[row][column].setImageResource(getDotImage(color));
    }
}