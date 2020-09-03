package com.example.puyo_base_simulator.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;

import com.example.puyo_base_simulator.BuildConfig;
import com.example.puyo_base_simulator.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class HomeFragment extends Fragment {
    ImageView[][] currentPuyoView;
    ImageView[][] nextPuyoView;
    ImageView[][] fieldView;
    Integer currentCursorColumnIndex = 3;
    Rotation currentCursorRotate = Rotation.DEGREE0;
    PuyoColor[] currentColor = new PuyoColor[2];
    PuyoColor[][] nextColor = new PuyoColor[2][2];
    Stack<Field> fieldStack = new Stack<>();
    Stack<Field> fieldRedoStack = new Stack<>();
    Field currentField = new Field(1);
    GridLayout currentPuyoLayout;
    GridLayout nextPuyoLayout;
    String[] haipuyo = new String[65536];
    int tsumoCounter;
    int haipuyoIndex;

    private static final Random RANDOM = new Random();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        haipuyoIndex = RANDOM.nextInt(65536);
        InputStream is;
        try {
            is = requireActivity().getAssets().open("haipuyo.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (int i=0; i<65536; i++) {
                haipuyo[i] = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        // next puyo area
        nextPuyoLayout = root.findViewById(R.id.nextPuyoLayout);
        ImageView[][] views = new ImageView[4][2];
        for(int i=0;i<4;i++){
            for(int j=0;j<2;j++){
                ImageView view = new ImageView(getActivity());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                view.setLayoutParams(params);
                view.setImageResource(R.drawable.blank);
                nextPuyoLayout.addView(view);
                views[i][j] = view;
            }
        }
        nextPuyoView = new ImageView[2][2];
        nextPuyoView[0][0] = views[0][0];
        nextPuyoView[0][1] = views[1][0];
        nextPuyoView[1][0] = views[2][1];
        nextPuyoView[1][1] = views[3][1];

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

        ((TextView)root.findViewById(R.id.pointTextView)).setText("0点");

        tsumoCounter = 0;
        setTsumo();
        drawNextPuyo();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Activity activity = getActivity();
        assert activity != null;

        final Button buttonUndo = activity.findViewById(R.id.buttonUndo);
        final Button buttonRedo = activity.findViewById(R.id.buttonRedo);

        buttonUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldRedoStack.push(currentField);
                currentField = fieldStack.pop();
                tsumoCounter -= 2;
                setTsumo();
                drawNextPuyo();
                if (fieldStack.isEmpty()) {  // 履歴がなくなったらUNDOボタンを無効化
                    buttonUndo.setEnabled(false);
                }
                buttonRedo.setEnabled(true);
            }
        });
        buttonUndo.setEnabled(false);

        buttonRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldStack.push(currentField);
                currentField = fieldRedoStack.pop();
                tsumoCounter += 2;
                setTsumo();
                drawNextPuyo();
                buttonUndo.setEnabled(true);
                if (fieldRedoStack.isEmpty()) {  // 履歴がなくなったらREDOボタンを無効化
                    buttonRedo.setEnabled(false);
                }
            }
        });
        buttonRedo.setEnabled(false);

        Button buttonLeft = activity.findViewById(R.id.buttonLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(currentCursorColumnIndex == 1 || (currentCursorColumnIndex == 2 && currentCursorRotate == Rotation.DEGREE270))) {
                    currentCursorColumnIndex--;
                }
                drawNextPuyo();
            }
        });

        Button buttonRight = activity.findViewById(R.id.buttonRight);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(currentCursorColumnIndex == 6 || (currentCursorColumnIndex == 5 && currentCursorRotate == Rotation.DEGREE90))) {
                    currentCursorColumnIndex++;
                }
                drawNextPuyo();
            }
        });

        Button buttonDown = activity.findViewById(R.id.buttonDown);
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldStack.push(currentField);
                currentField = currentField.clone();
                fieldRedoStack.clear();
                buttonRedo.setEnabled(false);
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
                currentField.evalNextField();
                drawFieldRecursively();
            }
        });

        Button buttonA = activity.findViewById(R.id.buttonA);
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
                drawNextPuyo();
            }
        });

        Button buttonB = activity.findViewById(R.id.buttonB);
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
                drawNextPuyo();
            }
        });

    }

    PuyoColor getPuyoColor(char c) {
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

    void setTsumo() {
        currentColor[1] = getPuyoColor(haipuyo[haipuyoIndex].charAt(tsumoCounter));
        currentColor[0] = getPuyoColor(haipuyo[haipuyoIndex].charAt(tsumoCounter+1));
        nextColor[0][0] = getPuyoColor(haipuyo[haipuyoIndex].charAt(tsumoCounter+2));
        nextColor[0][1] = getPuyoColor(haipuyo[haipuyoIndex].charAt(tsumoCounter+3));
        nextColor[1][0] = getPuyoColor(haipuyo[haipuyoIndex].charAt(tsumoCounter+4));
        nextColor[1][1] = getPuyoColor(haipuyo[haipuyoIndex].charAt(tsumoCounter+5));
    }

    void drawFieldRecursively() {
        final Activity activity = getActivity();
        assert activity != null;
        if (currentField.nextField == null) {  // 連鎖終わり
            setButtonStatus(true);
            // reset chain
            currentField.chainNum = 1;
            // get next puyo
            currentCursorColumnIndex = 3;
            currentCursorRotate = Rotation.DEGREE0;
            tsumoCounter += 2;
            setTsumo();
            activity.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    drawNextPuyo();
                }
            });
        } else {
            setButtonStatus(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    drawPoint(currentField);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    currentField = currentField.nextField;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drawField(currentField);
                        }
                    });
                    drawFieldRecursively();
                }
            }).start();
        }
    }

    void setButtonStatus(final boolean val) {
        final Activity activity = getActivity();
        assert activity != null;
        activity.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                activity.findViewById(R.id.buttonDown).setEnabled(val);
                activity.findViewById(R.id.buttonLeft).setEnabled(val);
                activity.findViewById(R.id.buttonRight).setEnabled(val);
                activity.findViewById(R.id.buttonA).setEnabled(val);
                activity.findViewById(R.id.buttonB).setEnabled(val);
                activity.findViewById(R.id.buttonUndo).setEnabled(val);
                activity.findViewById(R.id.buttonRedo).setEnabled(val);
            }
        });
    }

    void drawField(Field field) {
        // draw field puyo
        for (int i=1; i<14; i++) {
            for (int j=1; j<7; j++) {
                Puyo puyo = field.field[i][j];
                fieldView[i][j].setImageResource(getPuyoImage(puyo.color));
            }
        }
    }

    void drawPoint(Field field) {
        final Activity activity = getActivity();
        assert activity != null;
        //String text = "" + fieldEvaluation.accumulatedPoint + "点";
        final String text = "" + field.bonus + " * " + field.disappearPuyo.size() + " = " + field.accumulatedPoint +  "点";
        activity.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                ((TextView)activity.findViewById(R.id.pointTextView)).setText(text);
            }
        });
    }

    int getPuyoImage(PuyoColor color) {
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

    int getDotImage(PuyoColor color) {
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


    void drawNextPuyo() {
        drawField(currentField);

        // draw next and double next
        for (int i=0; i<2; i++) {
            for (int j=0; j<2; j++) {
                nextPuyoView[i][j].setImageResource(getPuyoImage(nextColor[i][j]));
            }
        }

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
    void drawDot(int column, List<PuyoColor> colors, Field field) {
        int row = field.heights[column] + 1;
        for (PuyoColor color : colors) {
            fieldView[row++][column].setImageResource(getDotImage(color));
        }
    }
}