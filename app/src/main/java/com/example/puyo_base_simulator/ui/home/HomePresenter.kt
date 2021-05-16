package com.example.puyo_base_simulator.ui.home

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.example.puyo_base_simulator.data.AppDatabase
import com.example.puyo_base_simulator.data.Base
import org.apache.commons.lang.SerializationUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class HomePresenter internal constructor(asset: AssetManager) : ViewModel() {
    var mFieldHistory = History<Field>()
    private var tsumoController: TsumoController
    private var mDB: AppDatabase? = null
    // todo: lateinitとか何かで消せない？
    private val sampleTsumoInfo = TsumoInfo(
        Array(2) {PuyoColor.RED},
        Array(2) {Array(2) {PuyoColor.RED}},
        3,
        Rotation.DEGREE0
    )
    private val _tsumoInfo = MutableLiveData(sampleTsumoInfo)
    val tsumoInfo: LiveData<TsumoInfo> = _tsumoInfo
    private val _currentField = MutableLiveData(Field())
    val currentField: LiveData<Field> = _currentField

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
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun rotateRight() {
        tsumoController.rotateCurrentLeft()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun moveLeft() {
        tsumoController.moveCurrentLeft()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun moveRight() {
        tsumoController.moveCurrentRight()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
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

    fun dropDown(activity: Activity) : Field? {
        val newField = setPairOnField(currentField.value!!, tsumoController.makeTsumoInfo()) ?: return null
        tsumoController.addPlacementHistory()
        newField.evalNextField()
        _currentField.value = newField
        mFieldHistory.add(newField)
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
        if (newField.nextField != null) {
            chain(newField, activity)
        }
        return newField
    }

    fun undo() {
        tsumoController.undoPlacementHistory()
        _currentField.value = mFieldHistory.undo()!!
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun redo(activity: Activity) : Field {
        mFieldHistory.redo()
        val field = setPairOnField(currentField.value!!, tsumoController.makeTsumoInfo(tsumoController.currentPlacementHistory()))!!
        tsumoController.redoPlacementHistory()
        field.evalNextField()
        _currentField.value = field
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
        if (field.nextField != null) {
            chain(field, activity)
        }
        return field
    }

    fun save(context: Context) : Boolean {
        if (mFieldHistory.isFirst()) return false
        val base = Base()
        base.hash = tsumoController.seed
        base.placementHistory = tsumoController.placementOrderToString()
        val field = currentField.value!!
        base.allClear = field.allClear()
        base.point = field.accumulatedPoint
        base.field = if (field.allClear()) {
            mFieldHistory.previous().toString()
        } else {
            field.toString()
        }
        getDB(context).baseDao().insert(base)
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
        return true
    }

    fun load(context: Context, fieldPreview: FieldPreview) {
        val base = getDB(context).baseDao().findById(fieldPreview.id)
        if (base != null) {
            tsumoController = TsumoController(Haipuyo[base.hash], base.hash)
            clearFieldHistory()
            var f = Field()
            for (p in tsumoController.stringToPlacementOrder(base.placementHistory)) {
                f = setPairOnField(f, tsumoController.makeTsumoInfo(p))!!
                f.evalNextField()
                f = getLastField(f)
                mFieldHistory.add(f)
            }
            tsumoController.addPlacementHistory()
            tsumoController.rollbackPlacementHistory()
            _currentField.value = mFieldHistory.undoAll()
        }
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
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
        _currentField.value = Field()
        clearFieldHistory()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun generate() {
        _currentField.value = Field()
        clearFieldHistory()
        val seed = RANDOM.nextInt(65536)
        tsumoController = TsumoController(Haipuyo[seed], seed)
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun restart() {
        for (i in 0 until tsumoController.tsumoCounter/2) {
            undo()
        }
    }

    fun setHistoryIndex(idx: Int) {
        mFieldHistory.index = idx
        _currentField.value = mFieldHistory.current()
        tsumoController.setHistoryIndex(idx)
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    private fun chain(field: Field, activity: Activity) {
        Thread {
            Thread.sleep(500)
            activity.runOnUiThread {
                _currentField.value = field.disappearingField
            }
            Thread.sleep(500)
            activity.runOnUiThread {
                _currentField.value = field.nextField!!
            }
            if (field.nextField!!.nextField != null) {
                chain(field.nextField!!, activity)
            }
        }.start()
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
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
        clearFieldHistory()
    }
}