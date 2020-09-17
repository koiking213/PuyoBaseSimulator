package com.example.puyo_base_simulator.ui.home;

import android.app.Activity;
import android.content.res.AssetManager;
import android.widget.ImageView;

import com.example.puyo_base_simulator.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Stack;

class TsumoController {
    String[] haipuyo = new String[65536];
    int tsumoCounter = 0;
    int seed;
    Integer currentCursorColumnIndex = 3;
    Rotation currentCursorRotate = Rotation.DEGREE0;
    PuyoColor[] currentColor = new PuyoColor[2];
    PuyoColor[][] nextColor = new PuyoColor[2][2];

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
        info.currentCursorRotate = currentCursorRotate;
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

public class HomePresenter implements HomeContract.Presenter {
    Activity mActivity;
    Field currentField = new Field(1);
    Stack<Field> fieldStack = new Stack<>();
    Stack<Field> fieldRedoStack = new Stack<>();
    TsumoController tsumoController = TsumoController.getInstance();
    private static final Random RANDOM = new Random();
    HomeFragment mView;

    HomePresenter(HomeFragment view, AssetManager asset, Activity activity) {
        mView = view;
        mActivity = activity;
        tsumoController.seed = RANDOM.nextInt(65536);
        InputStream is;
        try {
            is = asset.open("haipuyo.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (int i = 0; i < 65536; i++) {
                tsumoController.haipuyo[i] = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tsumoController.setTsumo();

        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.updateField(currentField, tsumoInfo);
        mView.drawTsumo(tsumoInfo, currentField);
    }

    public void rotateLeft() {
        tsumoController.rotateCurrentRight();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.updateField(currentField, tsumoInfo);
        mView.drawTsumo(tsumoInfo, currentField);
    }

    ;

    public void rotateRight() {
        tsumoController.rotateCurrentLeft();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.updateField(currentField, tsumoInfo);
        mView.drawTsumo(tsumoInfo, currentField);
    }

    ;

    public void moveLeft() {
        tsumoController.moveCurrentLeft();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.updateField(currentField, tsumoInfo);
        mView.drawTsumo(tsumoInfo, currentField);
    }

    ;

    public void moveRight() {
        tsumoController.moveCurrentRight();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.updateField(currentField, tsumoInfo);
        mView.drawTsumo(tsumoInfo, currentField);
    }

    ;

    public void dropDown() {
        fieldStack.push(currentField);
        currentField = currentField.clone();
        fieldRedoStack.clear();
        mView.disableRedoButton();

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
                currentField.addPuyo(currentCursorColumnIndex + 1, tsumoController.getSubColor());
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
                currentField.addPuyo(currentCursorColumnIndex - 1, tsumoController.getSubColor());
                break;
        }
        mView.drawField(currentField);
        currentField.evalNextField();
        tsumoController.incrementTsumo();
        if (currentField.nextField == null) {
            mView.drawTsumo(tsumoController.makeTsumoInfo(), currentField);
        } else {
            mView.eraseCurrentPuyo();
            mView.disableAllButtons();
            drawFieldRecursively(currentField);
            currentField = getLastField(currentField);
        }
    }

    public void undo() {
        fieldRedoStack.push(currentField);
        currentField = fieldStack.pop();
        tsumoController.decrementTsumo();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.updateField(currentField, tsumoInfo);
        mView.drawTsumo(tsumoInfo, currentField);
        if (fieldStack.isEmpty()) {  // 履歴がなくなったらUNDOボタンを無効化
            mView.disableUndoButton();
        }
        mView.enableRedoButton();

    }

    ;

    public void redo() {
        fieldStack.push(currentField);
        currentField = fieldRedoStack.pop();
        tsumoController.incrementTsumo();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.updateField(currentField, tsumoInfo);
        mView.drawTsumo(tsumoInfo, currentField);
        mView.enableUndoButton();
        if (fieldRedoStack.isEmpty()) {  // 履歴がなくなったらREDOボタンを無効化
            mView.disableRedoButton();
        }
    }

    Field getLastField(Field field) {
        if (field.nextField == null) {
            return field;
        } else {
            return getLastField(field.nextField);
        }
    }

    void drawFieldRecursively(final Field field) {
        if (field.nextField == null) {  // 連鎖終わり
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mView.enableAllButtons();
                }
            });
            // reset chain
            field.chainNum = 1;
            // get next puyo
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
                    mView.updateField(field, tsumoInfo);
                    mView.drawTsumo(tsumoInfo, field);
                }
            });
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String text = "" + field.bonus + " * " + field.disappearPuyo.size() + " = " + field.accumulatedPoint + "点";
                    mView.drawPoint(text);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.drawField(field.nextField);
                        }
                    });
                    drawFieldRecursively(field.nextField);
                }
            }).start();
        }
    }
}
