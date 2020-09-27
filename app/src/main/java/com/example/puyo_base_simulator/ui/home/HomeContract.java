package com.example.puyo_base_simulator.ui.home;

public interface HomeContract {
    interface View {
        // 6x13のフィールド部分を描画する
        void drawField(Field field);

        // 現在のツモ、ねくすと、ねくねく、フィールド上のどこにぷよが落ちるか、を描画する
        void drawTsumo(TsumoInfo tsumoInfo, Field field);

        // 画面上にfieldとtsumoInfoを反映する
        void update(Field field, TsumoInfo tsumoInfo);

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

        void start();

    }
}
