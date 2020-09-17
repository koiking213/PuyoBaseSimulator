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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static java.lang.Math.max;

enum Rotation {
    DEGREE0,
    DEGREE90,
    DEGREE180,
    DEGREE270
}

class TsumoController {
    String[] haipuyo = new String[65536];
    int tsumoCounter = 0;
    int seed;
    Integer currentCursorColumnIndex = 3;
    Rotation currentCursorRotate = Rotation.DEGREE0;
    PuyoColor[] currentColor = new PuyoColor[2];
    PuyoColor[][] nextColor = new PuyoColor[2][2];
    ImageView[][] nextPuyoView = new ImageView[2][2];
    ImageView[][] currentPuyoView = new ImageView[3][7];

    void setTsumo() {
        currentCursorColumnIndex = 3;
        currentCursorRotate = Rotation.DEGREE0;
        currentColor[1] = getPuyoColor(haipuyo[seed].charAt(tsumoCounter));
        currentColor[0] = getPuyoColor(haipuyo[seed].charAt(tsumoCounter+1));
        nextColor[0][0] = getPuyoColor(haipuyo[seed].charAt(tsumoCounter+2));
        nextColor[0][1] = getPuyoColor(haipuyo[seed].charAt(tsumoCounter+3));
        nextColor[1][0] = getPuyoColor(haipuyo[seed].charAt(tsumoCounter+4));
        nextColor[1][1] = getPuyoColor(haipuyo[seed].charAt(tsumoCounter+5));
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
        return info;
    }

    PuyoColor getPuyoColor(char c) {  //ここにいるべきか？
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
                return;
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
                return;
        }
    }

    // Singleton instance.
    private static final TsumoController INSTANCE = new TsumoController();
    // private constructor to prevent instantiation from other classes.
    private TsumoController() {}
    // static method to get the instance.
    public static TsumoController getInstance() {
        return INSTANCE;
    }

}

public class HomeFragment extends Fragment implements HomeContract.View {
    ImageView[][] fieldView;
    Stack<Field> fieldStack = new Stack<>();
    Stack<Field> fieldRedoStack = new Stack<>();
    Field currentField = new Field(1);
    GridLayout currentPuyoLayout;
    GridLayout nextPuyoLayout;
    TsumoController tsumoController = TsumoController.getInstance();
    private HomePresenter mPresenter = new HomePresenter(this);
    private Button mUndoButton;
    private Button mRedoButton;
    private Button mLeftButton;
    private Button mRightButton;
    private Button mDownButton;
    private Button mAButton;
    private Button mBButton;

    private static final Random RANDOM = new Random();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        tsumoController.seed = RANDOM.nextInt(65536);
        InputStream is;
        try {
            is = requireActivity().getAssets().open("haipuyo.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (int i=0; i<65536; i++) {
                tsumoController.haipuyo[i] = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // current puyo area
        currentPuyoLayout = root.findViewById(R.id.currentPuyoLayout);
        for(int i=0;i<3;i++){
            for(int j=0;j<7;j++){
                ImageView view = new ImageView(getActivity());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                view.setLayoutParams(params);
                view.setImageResource(R.drawable.blank);
                currentPuyoLayout.addView(view);
                tsumoController.currentPuyoView[i][j] = view;
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
        tsumoController.nextPuyoView[0][0] = views[0][0];
        tsumoController.nextPuyoView[0][1] = views[1][0];
        tsumoController.nextPuyoView[1][0] = views[2][1];
        tsumoController.nextPuyoView[1][1] = views[3][1];

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

        tsumoController.setTsumo();
        drawTsumo(tsumoController.makeTsumoInfo());
        updateField(currentField);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Activity activity = getActivity();
        assert activity != null;

        mUndoButton = activity.findViewById(R.id.buttonUndo);
        mRedoButton = activity.findViewById(R.id.buttonRedo);

        mUndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.undo();
            }
        });
        disableUndoButton();

        mRedoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.redo();
            }
        });
        disableRedoButton();

        mLeftButton = activity.findViewById(R.id.buttonLeft);
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.moveLeft();
            }
        });

        mRightButton = activity.findViewById(R.id.buttonRight);
        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsumoController.moveCurrentRight();
                drawTsumo(tsumoController.makeTsumoInfo());
                updateField(currentField);
            }
        });

        mDownButton = activity.findViewById(R.id.buttonDown);
        mDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldStack.push(currentField);
                currentField = currentField.clone();
                fieldRedoStack.clear();
                disableRedoButton();

                Rotation currentCursorRotate = tsumoController.currentCursorRotate;
                int currentCursorColumnIndex = tsumoController.currentCursorColumnIndex;
                switch (currentCursorRotate) {
                    case DEGREE0:
                        // jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                        // non-jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, tsumoController.getSubColor());
                        break;
                    case DEGREE90:
                        // jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                        // non-jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex+1, tsumoController.getSubColor());
                        break;
                    case DEGREE180:
                        // 上下が逆転している
                        // non-jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, tsumoController.getSubColor());
                        // jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                        break;
                    case DEGREE270:
                        // jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                        // non-jiku puyo
                        currentField.addPuyo(currentCursorColumnIndex-1, tsumoController.getSubColor());
                        break;
                }
                drawField(currentField);
                currentField.evalNextField();
                animateFieldChain(currentField);
            }
        });

        mAButton = activity.findViewById(R.id.buttonA);
        mAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsumoController.rotateCurrentRight();
                drawTsumo(tsumoController.makeTsumoInfo());
                updateField(currentField);
            }
        });

        mBButton = activity.findViewById(R.id.buttonB);
        mBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsumoController.rotateCurrentLeft();
                drawTsumo(tsumoController.makeTsumoInfo());
                updateField(currentField);
            }
        });

    }


    void drawFieldRecursively(final Field field) {
        final Activity activity = getActivity();
        assert activity != null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String text = "" + field.bonus + " * " + field.disappearPuyo.size() + " = " + field.accumulatedPoint + "点";
                drawPoint(text);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawField(field.nextField);
                    }
                });
                if (field.nextField.nextField != null) {
                    drawFieldRecursively(field.nextField);
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

    public void drawPoint(final String text) {
        final Activity activity = getActivity();
        assert activity != null;
        activity.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                ((TextView)activity.findViewById(R.id.pointTextView)).setText(text);
            }
        });
    }

    // TODO: Puyoのフィールドにする
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


    public void updateField (Field field) {
        drawField(currentField);
        PuyoColor[] currentColor = new PuyoColor[2];
        currentColor[0] = tsumoController.getMainColor();
        currentColor[1] = tsumoController.getSubColor();
        Rotation currentCursorRotate = tsumoController.currentCursorRotate;
        int currentCursorColumnIndex = tsumoController.currentCursorColumnIndex;
        switch (currentCursorRotate) {
            case DEGREE0:
                drawDot(currentCursorColumnIndex, Arrays.asList(currentColor[0], currentColor[1]), field);
                break;
            case DEGREE90:
                if (BuildConfig.DEBUG && currentCursorColumnIndex == 6) {
                    throw new AssertionError("Assertion failed");
                }
                drawDot(currentCursorColumnIndex, Collections.singletonList(currentColor[0]), field);
                drawDot(currentCursorColumnIndex + 1, Collections.singletonList(currentColor[1]), field);
                break;
            case DEGREE180:
                drawDot(currentCursorColumnIndex, Arrays.asList(currentColor[1], currentColor[0]), field);
                break;
            case DEGREE270:
                if (BuildConfig.DEBUG && currentCursorColumnIndex == 1) {
                    throw new AssertionError("Assertion failed");
                }
                drawDot(currentCursorColumnIndex, Collections.singletonList(currentColor[0]), field);
                drawDot(currentCursorColumnIndex - 1, Collections.singletonList(currentColor[1]), field);
                break;
        }
    }

    // リストで渡された順に下から積み上げる
    void drawDot(int column, List<PuyoColor> colors, Field field) {
        int row = field.heights[column] + 1;
        for (PuyoColor color : colors) {
            if (row <= 13) {
                fieldView[row++][column].setImageResource(getDotImage(color));
            }
        }
    }

    public void drawTsumo(TsumoInfo tsumoInfo) {
        // draw current
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {
                tsumoController.currentPuyoView[i][j].setImageResource(R.drawable.blank);
            }
        }
        int jikuColor = getPuyoImage(tsumoInfo.currentColor[0]);
        int nonJikuColor = getPuyoImage(tsumoInfo.currentColor[1]);
        // draw jiku-puyo
        tsumoController.currentPuyoView[tsumoInfo.currentMainPos[0]][tsumoInfo.currentMainPos[1]].setImageResource(jikuColor);

        // draw not-jiku-puyo
        tsumoController.currentPuyoView[tsumoInfo.currentSubPos[0]][tsumoInfo.currentSubPos[1]].setImageResource(nonJikuColor);

        // draw next and next next
        for (int i=0; i<2; i++) {
            for (int j=0; j<2; j++) {
                tsumoController.nextPuyoView[i][j].setImageResource(getPuyoImage(tsumoInfo.nextColor[i][j]));
            }
        }

    }
    public void disableUndoButton() {
        mUndoButton.setEnabled(false);
    };
    public void enableUndoButton() {
        mUndoButton.setEnabled(true);
    };

    public void disableRedoButton() {
        mRedoButton.setEnabled(false);
    };
    public void enableRedoButton() {
        mRedoButton.setEnabled(true);
    };

    public void disableAllButtons() {
        mLeftButton.setEnabled(false);
        mRightButton.setEnabled(false);
        mDownButton.setEnabled(false);
        mAButton.setEnabled(false);
        mBButton.setEnabled(false);
        mUndoButton.setEnabled(false);
        mRedoButton.setEnabled(false);
    };

    public void enableAllButtons() {
        mLeftButton.setEnabled(true);
        mRightButton.setEnabled(true);
        mDownButton.setEnabled(true);
        mAButton.setEnabled(true);
        mBButton.setEnabled(true);
        mUndoButton.setEnabled(true);
        mRedoButton.setEnabled(true);
    };

    // TODO: これ自体Presenterでやる
    void animateFieldChain(final Field field) {
        disableAllButtons();

        if (field.nextField != null) {
            drawFieldRecursively(field);
        }

        final Activity activity = getActivity();
        assert activity != null;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableAllButtons();
            }
        });
        // reset chain
        field.chainNum = 1;
        // get next puyo
        tsumoController.incrementTsumo();
        activity.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                drawTsumo(tsumoController.makeTsumoInfo());
                updateField(field);
            }
        });
        currentField = getLastField(field);
    };

    Field getLastField(Field field) {
        if (field.nextField == null) {
            return field;
        } else {
            return getLastField(field.nextField);
        }
    }
}