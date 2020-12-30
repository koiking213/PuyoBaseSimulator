package com.example.puyo_base_simulator.ui.home;

import android.app.Activity;
import android.content.res.AssetManager;

import androidx.room.Room;

import com.example.puyo_base_simulator.data.AppDatabase;
import com.example.puyo_base_simulator.data.Base;

import org.apache.commons.lang.SerializationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Random;

interface ButtonUpdateFunction {
    void func();
}

public class HomePresenter implements HomeContract.Presenter {
    Activity mActivity;
    Field currentField;
    StackWithButton<Field> fieldStack;
    StackWithButton<Placement> fieldRedoStack;
    TsumoController tsumoController;
    private static final Random RANDOM = new Random();
    HomeFragment mView;
    AppDatabase mDB;
    Haipuyo mHaipuyo = Haipuyo.getInstance();

    HomePresenter(HomeFragment view, AssetManager asset, Activity activity) {
        mDB = Room.databaseBuilder(activity.getApplicationContext(),
              AppDatabase.class, "database-name")
              .allowMainThreadQueries() // Main thread でも動作させたい場合
              .build();
        mView = view;
        mActivity = activity;
        try {
            InputStream haipuyoIs = asset.open("haipuyo.txt");
            BufferedReader haipuyoBr = new BufferedReader(new InputStreamReader(haipuyoIs));
            InputStream sortedIs = asset.open("sorted_haipuyo.txt");
            BufferedReader sortedBr = new BufferedReader(new InputStreamReader(sortedIs));
            mHaipuyo.load(haipuyoBr, sortedBr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentField =  new Field(1);
        fieldStack = new StackWithButton<>(() -> mView.enableUndoButton(), () -> mView.disableUndoButton());
        fieldRedoStack = new StackWithButton<>(() -> mView.enableRedoButton(), () -> mView.disableRedoButton());
        int seed = RANDOM.nextInt(65536);
        tsumoController = new TsumoController(mHaipuyo.get(seed), seed);
        mView.setSeedText(tsumoController.seed);
        mView.update(currentField, tsumoController.makeTsumoInfo());
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

    private Field setPairOnField() {
        Field newField = (Field) SerializationUtils.clone(currentField);
        Rotation currentCursorRotate = tsumoController.currentCursorRotate;
        int currentCursorColumnIndex = tsumoController.currentCursorColumnIndex;
        boolean success = true;
        switch (currentCursorRotate) {
            case DEGREE0:
                // jiku puyo
                success = newField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                // non-jiku puyo
                success &= newField.addPuyo(currentCursorColumnIndex, tsumoController.getSubColor());
                break;
            case DEGREE90:
                // jiku puyo
                success = newField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                // non-jiku puyo
                success &= newField.addPuyo(currentCursorColumnIndex + 1, tsumoController.getSubColor());
                break;
            case DEGREE180:
                // 上下が逆転している
                // non-jiku puyo
                success = newField.addPuyo(currentCursorColumnIndex, tsumoController.getSubColor());
                // jiku puyo
                success &= newField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                break;
            case DEGREE270:
                // jiku puyo
                success = newField.addPuyo(currentCursorColumnIndex, tsumoController.getMainColor());
                // non-jiku puyo
                success &= newField.addPuyo(currentCursorColumnIndex - 1, tsumoController.getSubColor());
                break;
        }
        if (success) {
            return newField;
        } else {
            return null;
        }
    }

    public void dropDown() {
        Field newFiled = setPairOnField();
        if (newFiled == null) {
            return;
        }
        fieldStack.push(currentField);
        currentField = newFiled;
        tsumoController.pushPlacementOrder();
        fieldRedoStack.clear();
        mView.drawField(currentField);
        currentField.evalNextField();
        tsumoController.incrementTsumo();
        if (currentField.nextField == null) {
            mView.drawTsumo(tsumoController.makeTsumoInfo(), currentField);
        } else {
            mView.eraseCurrentPuyo();
            mView.disableAllButtons();
            drawFieldChain(currentField);
            currentField = getLastField(currentField);
        }
    }

    public void undo() {
        fieldRedoStack.push(tsumoController.popPlacementOrder());
        currentField = fieldStack.pop();
        tsumoController.decrementTsumo();
        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
        mView.update(currentField, tsumoInfo);
    }

    public void redo() {
        tsumoController.restorePlacement(fieldRedoStack.pop());
        fieldStack.push(currentField);
        currentField = setPairOnField();
        tsumoController.pushPlacementOrder();
        mView.drawField(currentField);
        currentField.evalNextField();
        tsumoController.incrementTsumo();
        if (currentField.nextField == null) {
            mView.drawTsumo(tsumoController.makeTsumoInfo(), currentField);
        } else {
            mView.eraseCurrentPuyo();
            mView.disableAllButtons();
            drawFieldChain(currentField);
            currentField = getLastField(currentField);
        }
    }

    public void save() {
        Base base = new Base();
        base.setHash(tsumoController.seed);
        base.setField(currentField.toString());
        base.setPlacementOrder(tsumoController.placementOrderToString());
        mDB.baseDao().insert(base);
    }

    public void load(FieldPreview fieldPreview) {
        Base base = mDB.baseDao().findById(fieldPreview.id);
        currentField = new Field(1);
        tsumoController.stringToPlacementOrder(base.getPlacementOrder());
        fieldRedoStack.clear();
        while (!tsumoController.placementOrder.isEmpty()) {
            fieldRedoStack.push(tsumoController.popPlacementOrder());
        }
        fieldStack.clear();
        tsumoController = new TsumoController(mHaipuyo.get(base.getHash()), base.getHash());
        mView.update(currentField, tsumoController.makeTsumoInfo());

    }

    Field getLastField(Field field) {
        if (field.nextField == null) {
            return field;
        } else {
            return getLastField(field.nextField);
        }
    }


    void drawFieldChain(final Field field) {
        drawFieldChainRecursive(field, true);
    }

    void drawFieldChainRecursive(final Field field, boolean disappear) {
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (disappear) {
                String text = "" + field.bonus + " * " + field.disappearPuyo.size() + " = " + field.accumulatedPoint + "点";
                mView.drawPoint(text);
                mActivity.runOnUiThread(() -> mView.drawDisappearField(field));
                drawFieldChainRecursive(field.nextField, false);
            } else {
                mActivity.runOnUiThread(() -> mView.drawField(field));
                if (field.disappearPuyo.isEmpty()) {
                    // 終了処理
                    mActivity.runOnUiThread(() -> {
                        mView.enableAllButtons();
                        if (fieldRedoStack.isEmpty()) {
                            mView.disableRedoButton();
                        }
                        TsumoInfo tsumoInfo = tsumoController.makeTsumoInfo();
                        mView.update(field, tsumoInfo);
                    });
                } else {
                    drawFieldChainRecursive(field, true);
                }
            }
        }).start();
    }

    public void setSeed() {
        try {
            int newSeed = mView.getSpecifiedSeed();
            tsumoController = new TsumoController(mHaipuyo.get(newSeed), newSeed);
            fieldRedoStack.clear();
            fieldStack.clear();
            mView.setSeedText(newSeed);
            currentField =  new Field(1);
            mView.update(currentField, tsumoController.makeTsumoInfo());
        } catch (NumberFormatException ignored) {

        }
    }
}
