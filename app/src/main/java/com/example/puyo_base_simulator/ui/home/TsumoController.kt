package com.example.puyo_base_simulator.ui.home

import com.example.puyo_base_simulator.ui.home.Placement.Companion.from
import com.example.puyo_base_simulator.ui.home.PuyoColor.Companion.getPuyoColor
import java.util.*

class TsumoController(private val tsumo: String, val seed: Int) {
    private var tsumoCounter = 0
    var currentCursorColumnIndex = 3
    var currentCursorRotate = Rotation.DEGREE0 // 時計回り
    private var currentColor = arrayOf(PuyoColor.RED, PuyoColor.RED)
    private var nextColor = arrayOf(arrayOf(PuyoColor.RED, PuyoColor.RED), arrayOf(PuyoColor.RED, PuyoColor.RED))
    var placementOrder = Stack<Placement>()
    fun pushPlacementOrder() {
        placementOrder.push(Placement(currentCursorColumnIndex, currentCursorRotate, tsumoCounter))
    }

    fun popPlacementOrder(): Placement = placementOrder.pop()

    fun restorePlacement(plc: Placement) {
        tsumoCounter = plc.tsumoCounter
        setTsumo()
        currentCursorColumnIndex = plc.currentCursorColumnIndex
        currentCursorRotate = plc.currentCursorRotate
    }

    fun placementOrderToString() = placementOrder.joinToString(";") { it.toString() }

    fun stringToPlacementOrder(str: String) {
        placementOrder.clear()
        for (placementStr in str.split(";").toTypedArray()) {
            placementOrder.push(from(placementStr))
        }
    }

    private fun setTsumo() {
        currentCursorColumnIndex = 3
        currentCursorRotate = Rotation.DEGREE0
        currentColor = arrayOf (getPuyoColor(tsumo[tsumoCounter + 1]), getPuyoColor(tsumo[tsumoCounter]))
        nextColor = arrayOf(
                arrayOf(getPuyoColor(tsumo[tsumoCounter + 2]), getPuyoColor(tsumo[tsumoCounter + 3])),
                arrayOf(getPuyoColor(tsumo[tsumoCounter + 4]), getPuyoColor(tsumo[tsumoCounter + 5])))
    }

    fun incrementTsumo() {
        tsumoCounter += 2
        setTsumo()
    }

    fun decrementTsumo() {
        tsumoCounter -= 2
        setTsumo()
    }

    fun makeTsumoInfo() = TsumoInfo(currentColor, nextColor, currentCursorColumnIndex, currentCursorRotate)

    // 軸ぷよ
    val mainColor: PuyoColor
        get() = currentColor[0]

    // 軸ぷよでは無い方
    val subColor: PuyoColor
        get() = currentColor[1]

    fun moveCurrentLeft() {
        if (!(currentCursorColumnIndex == 1 || currentCursorColumnIndex == 2 && currentCursorRotate == Rotation.DEGREE270)) {
            currentCursorColumnIndex--
        }
    }

    fun moveCurrentRight() {
        if (!(currentCursorColumnIndex == 6 || currentCursorColumnIndex == 5 && currentCursorRotate == Rotation.DEGREE90)) {
            currentCursorColumnIndex++
        }
    }

    fun rotateCurrentLeft() {
        when (currentCursorRotate) {
            Rotation.DEGREE0 -> {
                currentCursorRotate = Rotation.DEGREE270
                if (currentCursorColumnIndex == 1) {
                    currentCursorColumnIndex = 2
                }
            }
            Rotation.DEGREE90 -> currentCursorRotate = Rotation.DEGREE0
            Rotation.DEGREE180 -> {
                currentCursorRotate = Rotation.DEGREE90
                if (currentCursorColumnIndex == 6) {
                    currentCursorColumnIndex = 5
                }
            }
            Rotation.DEGREE270 -> currentCursorRotate = Rotation.DEGREE180
        }
    }

    fun rotateCurrentRight() {
        when (currentCursorRotate) {
            Rotation.DEGREE0 -> {
                currentCursorRotate = Rotation.DEGREE90
                if (currentCursorColumnIndex == 6) {
                    currentCursorColumnIndex = 5
                }
            }
            Rotation.DEGREE90 -> currentCursorRotate = Rotation.DEGREE180
            Rotation.DEGREE180 -> {
                currentCursorRotate = Rotation.DEGREE270
                if (currentCursorColumnIndex == 1) {
                    currentCursorColumnIndex = 2
                }
            }
            Rotation.DEGREE270 -> currentCursorRotate = Rotation.DEGREE0
        }
    }

    companion object {
        fun getNumOfPlacement(placementOrder: String) : Int {
            return 1 + placementOrder.count { it == ';' }
        }
    }

    init {
        setTsumo()
    }
}