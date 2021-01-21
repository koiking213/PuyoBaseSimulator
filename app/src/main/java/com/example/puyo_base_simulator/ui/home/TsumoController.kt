package com.example.puyo_base_simulator.ui.home

import com.example.puyo_base_simulator.BuildConfig
import com.example.puyo_base_simulator.ui.home.Placement.Companion.from
import com.example.puyo_base_simulator.ui.home.PuyoColor
import java.util.*

class TsumoController(var tsumo: String, var seed: Int) {
    var tsumoCounter = 0
    var currentCursorColumnIndex = 3
    var currentCursorRotate = Rotation.DEGREE0 // 時計回り
    var currentColor = Array<PuyoColor>(2) { PuyoColor.RED } // TODO: pairにする
    var nextColor = Array(2) { Array<PuyoColor>(2) { PuyoColor.RED } } // TODO: pairのpairにする
    var placementOrder = Stack<Placement>()
    fun pushPlacementOrder() {
        placementOrder.push(Placement(currentCursorColumnIndex, currentCursorRotate, tsumoCounter))
    }

    fun popPlacementOrder(): Placement {
        return placementOrder.pop()
    }

    fun restorePlacement(plc: Placement) {
        tsumoCounter = plc.tsumoCounter
        setTsumo()
        currentCursorColumnIndex = plc.currentCursorColumnIndex
        currentCursorRotate = plc.currentCursorRotate
    }

    fun placementOrderToString(): String {
        val list = mutableListOf<String>()
        // スタックの奥から順に取り出される
        for (p in placementOrder) {
            list.add(p.toString())
        }
        return list.joinToString(";")
    }

    fun stringToPlacementOrder(str: String) {
        placementOrder.clear()
        for (placementStr in str.split(";").toTypedArray()) {
            placementOrder.push(from(placementStr))
        }
    }

    private fun setTsumo() {
        currentCursorColumnIndex = 3
        currentCursorRotate = Rotation.DEGREE0
        currentColor[1] = getPuyoColor(tsumo[tsumoCounter])
        currentColor[0] = getPuyoColor(tsumo[tsumoCounter + 1])
        nextColor[0][0] = getPuyoColor(tsumo[tsumoCounter + 2])
        nextColor[0][1] = getPuyoColor(tsumo[tsumoCounter + 3])
        nextColor[1][0] = getPuyoColor(tsumo[tsumoCounter + 4])
        nextColor[1][1] = getPuyoColor(tsumo[tsumoCounter + 5])
    }

    fun incrementTsumo() {
        tsumoCounter += 2
        setTsumo()
    }

    fun decrementTsumo() {
        tsumoCounter -= 2
        setTsumo()
    }

    fun makeTsumoInfo(): TsumoInfo {
        return TsumoInfo(currentColor, nextColor, currentCursorColumnIndex, currentCursorRotate)
    }

    private fun getPuyoColor(c: Char): PuyoColor {
        return when (c) {
            'r' -> PuyoColor.RED
            'b' -> PuyoColor.BLUE
            'g' -> PuyoColor.GREEN
            'y' -> PuyoColor.YELLOW
            'p' -> PuyoColor.PURPLE
            else -> {
                if (BuildConfig.DEBUG) {
                    throw AssertionError("Assertion failed")
                }
                PuyoColor.EMPTY
            }
        }
    }

    // 軸ぷよ
    val mainColor: PuyoColor?
        get() = currentColor[0]

    // 軸ぷよでは無い方
    val subColor: PuyoColor?
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
                return
            }
            Rotation.DEGREE90 -> {
                currentCursorRotate = Rotation.DEGREE0
                return
            }
            Rotation.DEGREE180 -> {
                currentCursorRotate = Rotation.DEGREE90
                if (currentCursorColumnIndex == 6) {
                    currentCursorColumnIndex = 5
                }
                return
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
                return
            }
            Rotation.DEGREE90 -> {
                currentCursorRotate = Rotation.DEGREE180
                return
            }
            Rotation.DEGREE180 -> {
                currentCursorRotate = Rotation.DEGREE270
                if (currentCursorColumnIndex == 1) {
                    currentCursorColumnIndex = 2
                }
                return
            }
            Rotation.DEGREE270 -> currentCursorRotate = Rotation.DEGREE0
        }
    }

    init {
        setTsumo()
    }
}