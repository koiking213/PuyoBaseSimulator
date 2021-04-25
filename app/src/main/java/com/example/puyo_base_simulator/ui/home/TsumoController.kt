package com.example.puyo_base_simulator.ui.home

import com.example.puyo_base_simulator.ui.home.Placement.Companion.from
import com.example.puyo_base_simulator.ui.home.PuyoColor.Companion.getPuyoColor

class TsumoController(private val tsumo: String, val seed: Int) {
    var tsumoCounter = 0
    var currentCursorColumnIndex = 3
    var currentCursorRotate = Rotation.DEGREE0 // 時計回り
    private var currentColor = arrayOf(PuyoColor.RED, PuyoColor.RED)
    private var nextColor = arrayOf(arrayOf(PuyoColor.RED, PuyoColor.RED), arrayOf(PuyoColor.RED, PuyoColor.RED))
    var placementHistory = History<Placement>()
    fun addPlacementHistory() {
        placementHistory.undo() // 確定前の手を編集
        placementHistory.add(Placement(currentCursorColumnIndex, currentCursorRotate, tsumoCounter))
        incrementTsumo()
        placementHistory.add(Placement(currentCursorColumnIndex, currentCursorRotate, tsumoCounter)) // 確定前の手を保持
    }

    fun redoPlacementHistory() {
        val p = placementHistory.redo()
        if (p == null) {
            tsumoCounter += 2
            restorePlacement(Placement(3, Rotation.DEGREE0, tsumoCounter))
        } else restorePlacement(p)
    }
    fun undoPlacementHistory() {
        placementHistory.undo()?.let { restorePlacement(it) }
    }
    fun latestPlacementHistory() : Placement = placementHistory.latest()
    fun currentPlacementHistory() : Placement = placementHistory.current()
    fun rollbackPlacementHistory() {
        restorePlacement(placementHistory.undoAll())
    }
    fun setHistoryIndex(idx : Int) {
        if (placementHistory.set(idx)) {
            restorePlacement(placementHistory.current())
        }
    }

    private fun restorePlacement(plc: Placement) {
        tsumoCounter = plc.tsumoCounter
        setTsumo()
        currentCursorColumnIndex = plc.currentCursorColumnIndex
        currentCursorRotate = plc.currentCursorRotate
    }

    fun placementOrderToString() : String {
        val list = placementHistory.toList().subList(0, placementHistory.index)
        return list.joinToString(";") { it.toString() }
    }

    fun stringToPlacementOrder(str: String) : MutableList<Placement> {
        placementHistory.clear()
        for (placementStr in str.split(";").toTypedArray()) {
            placementHistory.add(from(placementStr))
        }
        tsumoCounter = placementHistory.latest().tsumoCounter + 2
        val ret = placementHistory.toMutableList()
        placementHistory.add(Placement(currentCursorColumnIndex, currentCursorRotate, tsumoCounter))
        setTsumo()
        return ret
    }

    private fun setTsumo() {
        currentCursorColumnIndex = 3
        currentCursorRotate = Rotation.DEGREE0
        currentColor = arrayOf (getPuyoColor(tsumo[tsumoCounter + 1]), getPuyoColor(tsumo[tsumoCounter]))
        nextColor = arrayOf(
                arrayOf(getPuyoColor(tsumo[tsumoCounter + 2]), getPuyoColor(tsumo[tsumoCounter + 3])),
                arrayOf(getPuyoColor(tsumo[tsumoCounter + 4]), getPuyoColor(tsumo[tsumoCounter + 5])))
    }

    private fun incrementTsumo() {
        tsumoCounter += 2
        setTsumo()
    }

    fun makeTsumoInfo() = TsumoInfo(currentColor, nextColor, currentCursorColumnIndex, currentCursorRotate)
    fun makeTsumoInfo(p : Placement) : TsumoInfo {
        val currentColor = arrayOf (getPuyoColor(tsumo[p.tsumoCounter + 1]), getPuyoColor(tsumo[p.tsumoCounter]))
        val nextColor = arrayOf(
                arrayOf(getPuyoColor(tsumo[p.tsumoCounter + 2]), getPuyoColor(tsumo[p.tsumoCounter + 3])),
                arrayOf(getPuyoColor(tsumo[p.tsumoCounter + 4]), getPuyoColor(tsumo[p.tsumoCounter + 5])))
        return TsumoInfo(currentColor, nextColor, p.currentCursorColumnIndex, p.currentCursorRotate)
    }

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