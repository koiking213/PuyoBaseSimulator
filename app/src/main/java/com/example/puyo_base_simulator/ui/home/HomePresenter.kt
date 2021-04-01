package com.example.puyo_base_simulator.ui.home

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.view.inputmethod.InputMethodManager
import androidx.room.Room
import com.example.puyo_base_simulator.data.AppDatabase
import com.example.puyo_base_simulator.data.Base
import com.example.puyo_base_simulator.ui.home.HomeContract.Presenter
import org.apache.commons.lang.SerializationUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class HomePresenter internal constructor(private val view: HomeFragment, asset: AssetManager, private val activity: Activity) : Presenter {
    private var currentField = Field()
    private var fieldStack = StackWithButton<Field>({ this.view.enableUndoButton() }) { this.view.disableUndoButton() }
    private var fieldRedoStack = StackWithButton<Placement>({ this.view.enableRedoButton() }) { this.view.disableRedoButton() }
    private var tsumoController: TsumoController
    private var mDB: AppDatabase = Room.databaseBuilder(activity.applicationContext,
            AppDatabase::class.java, "database-name")
            .allowMainThreadQueries() // Main thread でも動作させたい場合
            .build()

    override fun rotateLeft() {
        tsumoController.rotateCurrentRight()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        view.update(currentField, tsumoInfo)
    }

    override fun rotateRight() {
        tsumoController.rotateCurrentLeft()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        view.update(currentField, tsumoInfo)
    }

    override fun moveLeft() {
        tsumoController.moveCurrentLeft()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        view.update(currentField, tsumoInfo)
    }

    override fun moveRight() {
        tsumoController.moveCurrentRight()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        view.update(currentField, tsumoInfo)
    }

    private fun setPairOnField(field: Field, col: Int, rot: Rotation, mainColor: PuyoColor, subColor: PuyoColor): Field? {
        val newField = SerializationUtils.clone(field) as Field
        val success = when (rot) {
            Rotation.DEGREE0 -> newField.addPuyo(col, mainColor) and newField.addPuyo(col, subColor)
            Rotation.DEGREE90 -> newField.addPuyo(col, mainColor) and newField.addPuyo(col + 1, subColor)
            Rotation.DEGREE180 -> newField.addPuyo(col, subColor) and newField.addPuyo(col, mainColor) // 上下が逆転している
            Rotation.DEGREE270 -> newField.addPuyo(col, mainColor) and newField.addPuyo(col - 1, subColor)
        }
        return if (success) newField else null
    }

    private fun setPairOnCurrentField(): Field? {
        return setPairOnField(currentField, tsumoController.currentCursorColumnIndex, tsumoController.currentCursorRotate, tsumoController.mainColor, tsumoController.subColor)
    }

    override fun dropDown() {
        val newField = setPairOnCurrentField() ?: return
        fieldStack.push(currentField)
        fieldRedoStack.clear()
        tsumoController.pushPlacementOrder()
        tsumoController.incrementTsumo()
        view.drawField(newField)
        newField.evalNextField()
        currentField = if (newField.nextField == null) {
            view.drawTsumo(tsumoController.makeTsumoInfo(), newField)
            newField
        } else {
            view.eraseCurrentPuyo()
            view.disableAllButtons()
            drawFieldChain(newField)
            getLastField(newField)
        }
    }

    override fun undo() {
        fieldRedoStack.push(tsumoController.popPlacementOrder())
        currentField = fieldStack.pop()
        tsumoController.decrementTsumo()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        view.update(currentField, tsumoInfo)
        view.drawPoint(0, 0, 0, currentField.accumulatedPoint)
    }

    override fun redo() {
        tsumoController.restorePlacement(fieldRedoStack.pop())
        fieldStack.push(currentField)
        val field = setPairOnCurrentField()!!
        tsumoController.pushPlacementOrder()
        view.drawField(field)
        field.evalNextField()
        tsumoController.incrementTsumo()
        currentField = if (field.nextField == null) {
            view.drawTsumo(tsumoController.makeTsumoInfo(), field)
            field
        } else {
            view.eraseCurrentPuyo()
            view.disableAllButtons()
            drawFieldChain(field)
            getLastField(field)
        }
    }

    private fun getPreviousColor(): Pair<PuyoColor, PuyoColor> {
        tsumoController.decrementTsumo()
        val c1 = tsumoController.mainColor
        val c2 = tsumoController.subColor
        tsumoController.incrementTsumo()
        return c1 to c2
    }

    override fun save() {
        val base = Base()
        base.hash = tsumoController.seed
        base.placementOrder = tsumoController.placementOrderToString()
        base.allClear = currentField.allClear()
        base.point = currentField.accumulatedPoint
        base.field = if (currentField.allClear()) {
            val (mainColor, subColor) = getPreviousColor()
            val p = tsumoController.placementOrder.peek()
            val f = setPairOnField(fieldStack.peek(), p.currentCursorColumnIndex, p.currentCursorRotate, mainColor, subColor)
            f.toString()
        } else {
            currentField.toString()
        }
        mDB.baseDao().insert(base)
    }

    override fun load(fieldPreview: FieldPreview) {
        val base = mDB.baseDao().findById(fieldPreview.id)
        if (base != null) {
            currentField = Field()
            tsumoController.stringToPlacementOrder(base.placementOrder)
            fieldRedoStack.clear()
            while (!tsumoController.placementOrder.isEmpty()) {
                fieldRedoStack.push(tsumoController.popPlacementOrder())
            }
            fieldStack.clear()
            tsumoController = TsumoController(Haipuyo[base.hash], base.hash)
            initFieldPreference()
        }
    }

    private fun getLastField(field: Field): Field {
        val next = field.nextField
        return if (next == null) {
            field
        } else {
            getLastField(next)
        }
    }

    private fun drawFieldChain(field: Field) {
        drawFieldChainRecursive(field, true, 0)
    }

    private fun drawFieldChainRecursive(field: Field, disappear: Boolean, sum: Int) {
        Thread {
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            if (disappear) {
                val point = field.bonus * field.disappearPuyo.size * 10
                view.drawPoint(field.bonus, field.disappearPuyo.size, sum + point, field.accumulatedPoint)
                view.drawChainNum(field.chainNum)
                activity.runOnUiThread { view.drawDisappearField(field) }
                drawFieldChainRecursive(field.nextField!!, false, sum + point)
            } else {
                activity.runOnUiThread { view.drawField(field) }
                if (field.disappearPuyo.isEmpty()) {
                    // 終了処理
                    activity.runOnUiThread {
                        view.enableAllButtons()
                        if (fieldRedoStack.isEmpty()) {
                            view.disableRedoButton()
                        }
                        val tsumoInfo = tsumoController.makeTsumoInfo()
                        view.update(field, tsumoInfo)
                    }
                } else {
                    drawFieldChainRecursive(field, true, sum)
                }
            }
        }.start()
    }

    override fun setSeed() {
        try {
            val newSeed = view.specifiedSeed
            tsumoController = TsumoController(Haipuyo[newSeed], newSeed)
            fieldRedoStack.clear()
            fieldStack.clear()
            view.setSeedText(newSeed)
            currentField = Field()
            view.update(currentField, tsumoController.makeTsumoInfo())
        } catch (ignored: NumberFormatException) {
        }
    }

    override fun restart() {
        currentField = Field()
        fieldRedoStack.clear()
        fieldStack.clear()
        val seed = RANDOM.nextInt(65536)
        tsumoController = TsumoController(Haipuyo[seed], seed)
        initFieldPreference()
    }

    private fun initFieldPreference() {
        view.setSeedText(tsumoController.seed)
        view.clearPoint()
        view.clearChainNum()
        view.update(currentField, tsumoController.makeTsumoInfo())
    }

    companion object {
        private val RANDOM = Random()
    }

    init {
        try {
            val haipuyoIs = asset.open("haipuyo.txt")
            val haipuyoBr = BufferedReader(InputStreamReader(haipuyoIs))
            val sortedIs = asset.open("sorted_haipuyo.txt")
            val sortedBr = BufferedReader(InputStreamReader(sortedIs))
            Haipuyo.load(haipuyoBr, sortedBr)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val seed = RANDOM.nextInt(65536)
        tsumoController = TsumoController(Haipuyo[seed], seed)
        initFieldPreference()
    }
}