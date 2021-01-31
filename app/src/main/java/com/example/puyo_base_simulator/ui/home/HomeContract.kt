package com.example.puyo_base_simulator.ui.home

interface HomeContract {
    interface View {
        // 6x13のフィールド部分を描画する
        fun drawField(field: Field)

        // 連鎖によって消えるぷよを区別してフィールドを描画する
        fun drawDisappearField(field: Field)

        // 現在のツモ、ねくすと、ねくねく、フィールド上のどこにぷよが落ちるか、を描画する
        fun drawTsumo(tsumoInfo: TsumoInfo, field: Field)

        // 画面上にfieldとtsumoInfoを反映する
        fun update(field: Field, tsumoInfo: TsumoInfo)
        fun drawPoint(text: String)
        fun eraseCurrentPuyo()
        fun disableUndoButton()
        fun enableUndoButton()
        fun disableRedoButton()
        fun enableRedoButton()
        fun disableAllButtons()
        fun enableAllButtons()
        val specifiedSeed: Int
        fun setSeedText(seed: Int)
    }

    interface Presenter {
        fun rotateLeft()
        fun rotateRight()
        fun moveLeft()
        fun moveRight()
        fun dropDown()
        fun undo()
        fun redo()
        fun save()
        fun load(fieldPreview: FieldPreview)
        fun setSeed()
    }
}