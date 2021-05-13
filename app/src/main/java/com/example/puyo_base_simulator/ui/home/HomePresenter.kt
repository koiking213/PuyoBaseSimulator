package com.example.puyo_base_simulator.ui.home

import android.app.Activity
import android.content.res.AssetManager
import androidx.room.Room
import com.example.puyo_base_simulator.data.AppDatabase
import com.example.puyo_base_simulator.data.Base
import org.apache.commons.lang.SerializationUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class HomePresenter internal constructor(private val view: HomeFragment, asset: AssetManager, private val activity: Activity) {
    var currentField = Field()
    private var tsumoController: TsumoController
    private var mDB: AppDatabase = Room.databaseBuilder(activity.applicationContext,
            AppDatabase::class.java, "database-name")
            .allowMainThreadQueries() // Main thread でも動作させたい場合
            .build()

    fun getTsumoInfo() : TsumoInfo = tsumoController.makeTsumoInfo()

    fun rotateLeft() {
        tsumoController.rotateCurrentRight()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        view.update(currentField, tsumoInfo)
    }

    fun rotateRight() {
        tsumoController.rotateCurrentLeft()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        view.update(currentField, tsumoInfo)
    }

    fun moveLeft() {
        tsumoController.moveCurrentLeft()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        view.update(currentField, tsumoInfo)
    }

    fun moveRight() {
        tsumoController.moveCurrentRight()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        view.update(currentField, tsumoInfo)
    }

    private fun setPairOnField(field: Field, tsumoInfo: TsumoInfo): Field? {
        val col = tsumoInfo.column
        val mainColor = tsumoInfo.currentColor[0]
        val subColor = tsumoInfo.currentColor[1]
        val newField = SerializationUtils.clone(field) as Field
        val success = when (tsumoInfo.rot) {
            Rotation.DEGREE0 -> newField.addPuyo(col, mainColor) and newField.addPuyo(col, subColor)
            Rotation.DEGREE90 -> newField.addPuyo(col, mainColor) and newField.addPuyo(col + 1, subColor)
            Rotation.DEGREE180 -> newField.addPuyo(col, subColor) and newField.addPuyo(col, mainColor) // 上下が逆転している
            Rotation.DEGREE270 -> newField.addPuyo(col, mainColor) and newField.addPuyo(col - 1, subColor)
        }
        return if (success) newField else null
    }

    fun dropDown() {
        val newField = setPairOnField(currentField, tsumoController.makeTsumoInfo()) ?: return
        tsumoController.addPlacementHistory()
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
        view.appendHistory(currentField)
    }

    fun undo() {
        tsumoController.undoPlacementHistory()
        currentField = view.undoHistory()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        view.update(currentField, tsumoInfo)
        view.drawPoint(0, 0, 0, currentField.accumulatedPoint)
    }

    fun redo() {
        view.redoHistory()
        val field = setPairOnField(currentField, tsumoController.makeTsumoInfo(tsumoController.currentPlacementHistory()))!!
        tsumoController.redoPlacementHistory()
        view.drawField(field)
        field.evalNextField()
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

    fun save() : Boolean {
        if (view.isHistoryFirst()) return false
        val base = Base()
        base.hash = tsumoController.seed
        base.placementHistory = tsumoController.placementOrderToString()
        base.allClear = currentField.allClear()
        base.point = currentField.accumulatedPoint
        base.field = if (currentField.allClear()) {
            val f = view.undoHistory()
            view.redoHistory()
            f.toString()
        } else {
            currentField.toString()
        }
        mDB.baseDao().insert(base)
        return true
    }

    fun load(fieldPreview: FieldPreview) {
        val base = mDB.baseDao().findById(fieldPreview.id)
        if (base != null) {
            currentField = Field()
            tsumoController = TsumoController(Haipuyo[base.hash], base.hash)
            view.clearHistory()
            var f = Field()
            for (p in tsumoController.stringToPlacementOrder(base.placementHistory)) {
                f = setPairOnField(f, tsumoController.makeTsumoInfo(p))!!
                view.appendHistory(f)
            }
            tsumoController.addPlacementHistory()
            tsumoController.rollbackPlacementHistory()
            currentField = view.undoAllHistory()
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
        drawFieldChainRecursive(field, true)
    }

    private fun drawFieldChainRecursive(field: Field, disappear: Boolean) {
        Thread {
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            if (disappear) {
                view.drawPoint(field.bonus, field.disappearPuyo.size, field.chainPoint, field.accumulatedPoint)
                view.drawChainNum(field.chainNum)
                activity.runOnUiThread { view.drawDisappearField(field) }
                drawFieldChainRecursive(field.nextField!!, false)
            } else {
                activity.runOnUiThread { view.drawField(field) }
                if (field.disappearPuyo.isEmpty()) {
                    // 終了処理
                    activity.runOnUiThread {
                        view.enableAllButtons()
                        val tsumoInfo = tsumoController.makeTsumoInfo()
                        view.update(field, tsumoInfo)
                    }
                } else {
                    drawFieldChainRecursive(field, true)
                }
            }
        }.start()
    }

    fun setSeed() {
        try {
            val newSeed = view.specifiedSeed
            tsumoController = TsumoController(Haipuyo[newSeed], newSeed)
            view.setSeedText(newSeed)
            currentField = Field()
            view.clearHistory()
            view.appendHistory(currentField)
            view.update(currentField, tsumoController.makeTsumoInfo())
        } catch (ignored: NumberFormatException) {
        }
    }

    fun generate() {
        currentField = Field()
        view.clearHistory()
        val seed = RANDOM.nextInt(65536)
        tsumoController = TsumoController(Haipuyo[seed], seed)
        initFieldPreference()
    }

    fun restart() {
        for (i in 0 until tsumoController.tsumoCounter/2) {
            undo()
        }
    }

    fun setHistoryIndex(idx: Int) {
        currentField = view.currentHistory()
        tsumoController.setHistoryIndex(idx)
        view.drawField(currentField)
        view.drawTsumo(tsumoController.makeTsumoInfo(), currentField)
    }

    fun evalHistory() {
        currentField.evalNextField()
        if (currentField.nextField != null) {
            view.eraseCurrentPuyo()
            view.disableAllButtons()
            drawFieldChain(currentField)
            currentField = getLastField(currentField)
        }
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