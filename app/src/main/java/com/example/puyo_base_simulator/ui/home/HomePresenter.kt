package com.example.puyo_base_simulator.ui.home

import android.app.Activity
import android.content.res.AssetManager
import androidx.room.Room
import com.example.puyo_base_simulator.data.AppDatabase
import com.example.puyo_base_simulator.data.Base
import com.example.puyo_base_simulator.ui.home.HomeContract.Presenter
import org.apache.commons.lang.SerializationUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

internal interface ButtonUpdateFunction {
    fun func()
}

class HomePresenter internal constructor(view: HomeFragment, asset: AssetManager, activity: Activity) : Presenter {
    var mActivity: Activity
    var currentField: Field?
    var fieldStack: StackWithButton<Field?>
    var fieldRedoStack: StackWithButton<Placement>
    var tsumoController: TsumoController
    var mView: HomeFragment
    var mDB: AppDatabase
    override fun rotateLeft() {
        tsumoController.rotateCurrentRight()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        mView.update(currentField!!, tsumoInfo)
    }

    override fun rotateRight() {
        tsumoController.rotateCurrentLeft()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        mView.update(currentField!!, tsumoInfo)
    }

    override fun moveLeft() {
        tsumoController.moveCurrentLeft()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        mView.update(currentField!!, tsumoInfo)
    }

    override fun moveRight() {
        tsumoController.moveCurrentRight()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        mView.update(currentField!!, tsumoInfo)
    }

    private fun setPairOnField(): Field? {
        val newField = SerializationUtils.clone(currentField) as Field
        val currentCursorRotate = tsumoController.currentCursorRotate
        val currentCursorColumnIndex = tsumoController.currentCursorColumnIndex
        var success = true
        when (currentCursorRotate) {
            Rotation.DEGREE0 -> {
                // jiku puyo
                success = newField.addPuyo(currentCursorColumnIndex, tsumoController.mainColor)
                // non-jiku puyo
                success = success and newField.addPuyo(currentCursorColumnIndex, tsumoController.subColor)
            }
            Rotation.DEGREE90 -> {
                // jiku puyo
                success = newField.addPuyo(currentCursorColumnIndex, tsumoController.mainColor)
                // non-jiku puyo
                success = success and newField.addPuyo(currentCursorColumnIndex + 1, tsumoController.subColor)
            }
            Rotation.DEGREE180 -> {
                // 上下が逆転している
                // non-jiku puyo
                success = newField.addPuyo(currentCursorColumnIndex, tsumoController.subColor)
                // jiku puyo
                success = success and newField.addPuyo(currentCursorColumnIndex, tsumoController.mainColor)
            }
            Rotation.DEGREE270 -> {
                // jiku puyo
                success = newField.addPuyo(currentCursorColumnIndex, tsumoController.mainColor)
                // non-jiku puyo
                success = success and newField.addPuyo(currentCursorColumnIndex - 1, tsumoController.subColor)
            }
        }
        return if (success) {
            newField
        } else {
            null
        }
    }

    override fun dropDown() {
        val newFiled = setPairOnField() ?: return
        fieldStack.push(currentField)
        currentField = newFiled
        tsumoController.pushPlacementOrder()
        fieldRedoStack.clear()
        mView.drawField(currentField!!)
        currentField!!.evalNextField()
        tsumoController.incrementTsumo()
        if (currentField!!.nextField == null) {
            mView.drawTsumo(tsumoController.makeTsumoInfo(), currentField!!)
        } else {
            mView.eraseCurrentPuyo()
            mView.disableAllButtons()
            drawFieldChain(currentField)
            currentField = getLastField(currentField!!)
        }
    }

    override fun undo() {
        fieldRedoStack.push(tsumoController.popPlacementOrder())
        currentField = fieldStack.pop()
        tsumoController.decrementTsumo()
        val tsumoInfo = tsumoController.makeTsumoInfo()
        mView.update(currentField!!, tsumoInfo)
    }

    override fun redo() {
        tsumoController.restorePlacement(fieldRedoStack.pop())
        fieldStack.push(currentField)
        currentField = setPairOnField()
        tsumoController.pushPlacementOrder()
        mView.drawField(currentField!!)
        currentField!!.evalNextField()
        tsumoController.incrementTsumo()
        if (currentField!!.nextField == null) {
            mView.drawTsumo(tsumoController.makeTsumoInfo(), currentField!!)
        } else {
            mView.eraseCurrentPuyo()
            mView.disableAllButtons()
            drawFieldChain(currentField)
            currentField = getLastField(currentField!!)
        }
    }

    override fun save() {
        val base = Base()
        base.hash = tsumoController.seed
        base.field = currentField.toString()
        base.placementOrder = tsumoController.placementOrderToString()
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
            mView.update(currentField!!, tsumoController.makeTsumoInfo())
        }
    }

    fun getLastField(field: Field): Field {
        return if (field.nextField == null) {
            field
        } else {
            getLastField(field.nextField!!)
        }
    }

    fun drawFieldChain(field: Field?) {
        drawFieldChainRecursive(field, true)
    }

    fun drawFieldChainRecursive(field: Field?, disappear: Boolean) {
        Thread {
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            if (disappear) {
                val text = "" + field!!.bonus + " * " + field.disappearPuyo.size + " = " + field.accumulatedPoint + "点"
                mView.drawPoint(text)
                mActivity.runOnUiThread { mView.drawDisappearField(field) }
                drawFieldChainRecursive(field.nextField, false)
            } else {
                mActivity.runOnUiThread { mView.drawField(field!!) }
                if (field!!.disappearPuyo.isEmpty()) {
                    // 終了処理
                    mActivity.runOnUiThread {
                        mView.enableAllButtons()
                        if (fieldRedoStack.isEmpty()) {
                            mView.disableRedoButton()
                        }
                        val tsumoInfo = tsumoController.makeTsumoInfo()
                        mView.update(field, tsumoInfo)
                    }
                } else {
                    drawFieldChainRecursive(field, true)
                }
            }
        }.start()
    }

    override fun setSeed() {
        try {
            val newSeed = mView.specifiedSeed
            tsumoController = TsumoController(Haipuyo[newSeed], newSeed)
            fieldRedoStack.clear()
            fieldStack.clear()
            mView.setSeedText(newSeed)
            currentField = Field()
            mView.update(currentField!!, tsumoController.makeTsumoInfo())
        } catch (ignored: NumberFormatException) {
        }
    }

    companion object {
        private val RANDOM = Random()
    }

    init {
        mDB = Room.databaseBuilder(activity.applicationContext,
                AppDatabase::class.java, "database-name")
                .allowMainThreadQueries() // Main thread でも動作させたい場合
                .build()
        mView = view
        mActivity = activity
        try {
            val haipuyoIs = asset.open("haipuyo.txt")
            val haipuyoBr = BufferedReader(InputStreamReader(haipuyoIs))
            val sortedIs = asset.open("sorted_haipuyo.txt")
            val sortedBr = BufferedReader(InputStreamReader(sortedIs))
            Haipuyo.load(haipuyoBr, sortedBr)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        currentField = Field()
        fieldStack = StackWithButton({ mView.enableUndoButton() }) { mView.disableUndoButton() }
        fieldRedoStack = StackWithButton({ mView.enableRedoButton() }) { mView.disableRedoButton() }
        val seed = RANDOM.nextInt(65536)
        tsumoController = TsumoController(Haipuyo[seed], seed)
        mView.setSeedText(tsumoController.seed)
        mView.update(currentField!!, tsumoController.makeTsumoInfo())
    }
}