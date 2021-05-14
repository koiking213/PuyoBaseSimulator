package com.example.puyo_base_simulator.ui.home

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import androidx.room.Room
import com.example.puyo_base_simulator.data.AppDatabase
import com.example.puyo_base_simulator.data.Base
import org.apache.commons.lang.SerializationUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class HomePresenter internal constructor(asset: AssetManager) {
    var currentField = Field()
    var mFieldHistory = History<Field>()
    private var tsumoController: TsumoController
    private var mDB: AppDatabase? = null

    val tsumoInfo : TsumoInfo
        get() = tsumoController.makeTsumoInfo()

    val seed: Int
        get() = tsumoController.seed

    private fun getDB(context: Context) : AppDatabase {
        if (mDB == null) {
            val result = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "database-name"
            ).allowMainThreadQueries()
                    .build()
            mDB = result
        }
        return mDB!!
    }

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
        mFieldHistory.add(currentField)
        return newField
    }

    fun undo() {
        tsumoController.undoPlacementHistory()
        currentField = mFieldHistory.undo()!!
    }

    fun redo() : Field {
        mFieldHistory.redo()
        val field = setPairOnField(currentField, tsumoController.makeTsumoInfo(tsumoController.currentPlacementHistory()))!!
        tsumoController.redoPlacementHistory()
        field.evalNextField()
        currentField = if (field.nextField == null) field else getLastField(field)
        return field
    }

    fun save(context: Context) : Boolean {
        if (mFieldHistory.isFirst()) return false
        val base = Base()
        base.hash = tsumoController.seed
        base.placementHistory = tsumoController.placementOrderToString()
        base.allClear = currentField.allClear()
        base.point = currentField.accumulatedPoint
        base.field = if (currentField.allClear()) {
            mFieldHistory.previous().toString()
        } else {
            currentField.toString()
        }
        getDB(context).baseDao().insert(base)
        return true
    }

    fun load(context: Context, fieldPreview: FieldPreview) {
        val base = getDB(context).baseDao().findById(fieldPreview.id)
        if (base != null) {
            currentField = Field()
            tsumoController = TsumoController(Haipuyo[base.hash], base.hash)
            clearFieldHistory()
            var f = Field()
            for (p in tsumoController.stringToPlacementOrder(base.placementHistory)) {
                f = setPairOnField(f, tsumoController.makeTsumoInfo(p))!!
                mFieldHistory.add(f)
            }
            tsumoController.addPlacementHistory()
            tsumoController.rollbackPlacementHistory()
            currentField = mFieldHistory.undoAll()
        }
    }

    private fun clearFieldHistory() {
        mFieldHistory.clear()
        mFieldHistory.add(Field())
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
        clearFieldHistory()
    }

    fun generate() {
        currentField = Field()
        clearFieldHistory()
        val seed = RANDOM.nextInt(65536)
        tsumoController = TsumoController(Haipuyo[seed], seed)
    }

    fun restart() {
        for (i in 0 until tsumoController.tsumoCounter/2) {
            undo()
        }
    }

    fun setHistoryIndex(idx: Int) {
        mFieldHistory.index = idx
        currentField = mFieldHistory.current()
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
        clearFieldHistory()
    }
}