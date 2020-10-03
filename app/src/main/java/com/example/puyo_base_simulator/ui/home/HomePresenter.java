package com.example.puyo_base_simulator.ui.home;

import android.app.Activity;
import android.content.res.AssetManager;

import androidx.room.Room;

import com.example.puyo_base_simulator.data.AppDatabase;
import com.example.puyo_base_simulator.data.Base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Stack;

public class HomePresenter implements HomeContract.Presenter {
    Activity mActivity;
    Field currentField = new Field(1);
    Stack<Field> fieldStack = new Stack<>();
    Stack<Field> fieldRedoStack = new Stack<>();
    TsumoController tsumoController = TsumoController.getInstance();
    private static final Random RANDOM = new Random();
    HomeFragment mView;
    AppDatabase mDB;

    HomePresenter(HomeFragment view, AssetManager asset, Activity activity) {
        mDB = Room.databaseBuilder(activity.getApplicationContext(),
              AppDatabase.class, "database-name")
              .allowMainThreadQueries() // Main thread でも動作させたい場合
              .build();
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
        mView.update(currentField, tsumoInfo);
    }

    public void start() {
        mView.disableRedoButton();
        mView.disableUndoButton();
    }

    public void rotateLeft() {
        tsumoController.rotateCurrentRight();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.update(currentField, tsumoInfo);
    }

    public void rotateRight() {
        tsumoController.rotateCurrentLeft();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.update(currentField, tsumoInfo);
    }

    public void moveLeft() {
        tsumoController.moveCurrentLeft();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.update(currentField, tsumoInfo);
    }

    public void moveRight() {
        tsumoController.moveCurrentRight();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.update(currentField, tsumoInfo);
    }

    public void dropDown() {
        fieldStack.push(currentField);
        currentField = currentField.clone();

        Rotation currentCursorRotate = tsumoController.currentCursorRotate;
        int currentCursorColumnIndex = tsumoController.currentCursorColumnIndex;
        boolean success = true;
        switch (currentCursorRotate) {
            case DEGREE0:
                // jiku puyo
                success = currentField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                // non-jiku puyo
                success &= currentField.addPuyo(currentCursorColumnIndex, tsumoController.getSubColor());
                break;
            case DEGREE90:
                // jiku puyo
                success = currentField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                // non-jiku puyo
                success &= currentField.addPuyo(currentCursorColumnIndex + 1, tsumoController.getSubColor());
                break;
            case DEGREE180:
                // 上下が逆転している
                // non-jiku puyo
                success = currentField.addPuyo(currentCursorColumnIndex, tsumoController.getSubColor());
                // jiku puyo
                success &= currentField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                break;
            case DEGREE270:
                // jiku puyo
                success = currentField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                // non-jiku puyo
                success &= currentField.addPuyo(currentCursorColumnIndex - 1, tsumoController.getSubColor());
                break;
        }
        if (!success) {
            currentField = fieldStack.pop();
            return;
        }
        mView.enableUndoButton();
        fieldRedoStack.clear();
        mView.disableRedoButton();
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
        mView.update(currentField, tsumoInfo);
        if (fieldStack.isEmpty()) {  // 履歴がなくなったらUNDOボタンを無効化
            mView.disableUndoButton();
        }
        mView.enableRedoButton();
    }

    public void redo() {
        fieldStack.push(currentField);
        currentField = fieldRedoStack.pop();
        tsumoController.incrementTsumo();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.update(currentField, tsumoInfo);
        mView.enableUndoButton();
        if (fieldRedoStack.isEmpty()) {  // 履歴がなくなったらREDOボタンを無効化
            mView.disableRedoButton();
        }
    }

    public void save() {
        Base base = new Base();
        base.setHash(tsumoController.seed);
        base.setField(currentField.toString());
        base.setTsumoNum(tsumoController.tsumoCounter);
        mDB.baseDao().insert(base);
    }

    public void load() {

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
                    mView.update(field, tsumoInfo);
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
