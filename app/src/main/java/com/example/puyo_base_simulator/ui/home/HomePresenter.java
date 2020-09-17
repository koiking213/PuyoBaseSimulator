package com.example.puyo_base_simulator.ui.home;

public class HomePresenter implements HomeContract.Presenter {
    HomeFragment mView;
    HomePresenter (HomeFragment view) {
        mView = view;
    }
    public void rotateLeft() {
        tsumoController.moveCurrentLeft();
        drawTsumo(tsumoController.makeTsumoInfo());
        updateField(currentField);
    };

    public void rotateRight() {};

    public void moveLeft() {};

    public void moveRight() {};

    public void dropDown() {};

    public void undo() {
        fieldRedoStack.push(currentField);
        currentField = fieldStack.pop();
        tsumoController.decrementTsumo();
        drawTsumo(tsumoController.makeTsumoInfo());
        updateField(currentField);
        if (fieldStack.isEmpty()) {  // 履歴がなくなったらUNDOボタンを無効化
            disableUndoButton();
        }
        enableRedoButton();

    };

    public void redo() {
        fieldStack.push(currentField);
        currentField = fieldRedoStack.pop();
        tsumoController.incrementTsumo();
        drawTsumo(tsumoController.makeTsumoInfo());
        updateField(currentField);
        enableUndoButton();
        if (fieldRedoStack.isEmpty()) {  // 履歴がなくなったらREDOボタンを無効化
            disableRedoButton();
        }
    };
}
