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


class HomePresenter internal constructor(private val view: HomeFragment, asset: AssetManager, activity: Activity) {
    var currentField = Field()
    private var tsumoController: TsumoController
    private var mDB: AppDatabase = Room.databaseBuilder(activity.applicationContext,
            AppDatabase::class.java, "database-name")
            .allowMainThreadQueries() // Main thread でも動作させたい場合
            .build()

    val tsumoInfo : TsumoInfo
        get() = tsumoController.makeTsumoInfo()

    val seed: Int
        get() = tsumoController.seed

    fun rotateLeft() {
        tsumoController.rotateCurrentRight()
    }

    fun rotateRight() {
        tsumoController.rotateCurrentLeft()
    }

    fun moveLeft() {
        tsumoController.moveCurrentLeft()
    }

    fun moveRight() {
        tsumoController.moveCurrentRight()
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

    fun dropDown() : Field? {
        val newField = setPairOnField(currentField, tsumoController.makeTsumoInfo()) ?: return null
        tsumoController.addPlacementHistory()
        newField.evalNextField()
        currentField = if (newField.nextField == null) newField else getLastField(newField)
        view.appendHistory(currentField)
        return newField
    }

    fun undo() {
        tsumoController.undoPlacementHistory()
        currentField = view.undoHistory()
    }

    fun redo() : Field {
        view.redoHistory()
        val field = setPairOnField(currentField, tsumoController.makeTsumoInfo(tsumoController.currentPlacementHistory()))!!
        tsumoController.redoPlacementHistory()
        field.evalNextField()
        currentField = if (field.nextField == null) field else getLastField(field)
        return field
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

    fun setSeed(newSeed: Int) {
        tsumoController = TsumoController(Haipuyo[newSeed], newSeed)
        currentField = Field()
        view.clearHistory()
    }

    fun generate() {
        currentField = Field()
        view.clearHistory()
        val seed = RANDOM.nextInt(65536)
        tsumoController = TsumoController(Haipuyo[seed], seed)
    }

    fun restart() {
        for (i in 0 until tsumoController.tsumoCounter/2) {
            undo()
        }
    }

    fun setHistoryIndex(idx: Int) {
        currentField = view.currentHistory()
        tsumoController.setHistoryIndex(idx)
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
    }
}