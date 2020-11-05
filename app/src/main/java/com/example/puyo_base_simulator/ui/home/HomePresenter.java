package com.example.puyo_base_simulator.ui.home;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.room.Room;

import com.example.puyo_base_simulator.data.AppDatabase;
import com.example.puyo_base_simulator.data.Base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.lang.Runnable;

class StackWithButton<T> extends Stack<T>{
    ButtonUpdateFunction enableFun;
    ButtonUpdateFunction disableFun;

    StackWithButton(ButtonUpdateFunction enableFun, ButtonUpdateFunction disableFun) {
        super();
        this.enableFun = enableFun;
        this.disableFun = disableFun;
    }

    @Override
    public T push(T elm) {
        enableFun.func();
        return super.push(elm);
    }

    @Override
    public T pop() {
        T elm =  super.pop();
        if (super.isEmpty()) disableFun.func();
        return elm;
    }

    @Override
    public void clear() {
        super.clear();
        disableFun.func();
    }
}

interface ButtonUpdateFunction {
    void func();
}

public class HomePresenter implements HomeContract.Presenter {
    Activity mActivity;
    Field currentField;
    StackWithButton<Field> fieldStack;
    StackWithButton<Placement> fieldRedoStack;
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
        tsumoController.reset(RANDOM.nextInt(65536));
    }

    public void start() {
        tsumoController.setTsumo();
        currentField =  new Field(1);
        fieldStack = new StackWithButton<>(new ButtonUpdateFunction() {
            @Override
            public void func() {
                mView.enableUndoButton();
            }
        }, new ButtonUpdateFunction() {
            @Override
            public void func() {
                mView.disableUndoButton();
            }
        });
        fieldRedoStack = new StackWithButton<>(new ButtonUpdateFunction() {
            @Override
            public void func() {
                mView.enableRedoButton();
            }
        }, new ButtonUpdateFunction() {
            @Override
            public void func() {
                mView.disableRedoButton();
            }
        });
        fieldRedoStack.clear();
        fieldStack.clear();
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
        Field newField = currentField.clone();
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
            drawFieldRecursively(currentField);
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
            drawFieldRecursively(currentField);
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

    public void load() {
        // DEBUG
        List<Base> bases = mDB.baseDao().findByHash(tsumoController.seed);
        Base base = bases.get(bases.size() - 1);
        if (base == null) {
            Log.d("load", "seed: " + tsumoController.seed + ", field: null");
        } else {
            Log.d("load", "seed: " + tsumoController.seed + ", field: " + base.getField());
            currentField = new Field(1);
            tsumoController.stringToPlacementOrder(base.getPlacementOrder());
            fieldRedoStack.clear();
            while (!tsumoController.placementOrder.isEmpty()) {
                fieldRedoStack.push(tsumoController.popPlacementOrder());
            }
            fieldStack.clear();
            tsumoController.reset(base.getHash());
            mView.update(currentField, tsumoController.makeTsumoInfo());
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
                    if (fieldRedoStack.isEmpty()) {
                        mView.disableRedoButton();
                    }
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

    public void setSeed() {
        int newSeed = mView.getSpecifiedSeed();
        tsumoController.reset(newSeed);
        this.start();
    }
}
