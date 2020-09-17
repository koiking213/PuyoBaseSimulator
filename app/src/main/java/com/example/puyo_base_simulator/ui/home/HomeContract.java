package com.example.puyo_base_simulator.ui.home;

public interface HomeContract {
    interface View {
        // 現在のぷよの落下点にdotを描画しない
        void drawField(Field field);

        // 現在のぷよの落下点にdotを描画する
        // TODO: 整理してdrawFieldとupdateFieldのどちらかのみ公開するよう修正する
        void updateField(Field field, TsumoInfo tsumoInfo);

        void drawTsumo(TsumoInfo tsumoInfo, Field field);

        void drawPoint(String text);

        void eraseCurrentPuyo();

        void disableUndoButton();
        void enableUndoButton();

        void disableRedoButton();
        void enableRedoButton();

        void disableAllButtons();
        void enableAllButtons();
    }
    interface Presenter {
        void rotateLeft();

        void rotateRight();

        void moveLeft();

        void moveRight();

        void dropDown();

        void undo();

        void redo();

    }
}
