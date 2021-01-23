package com.example.puyo_base_simulator.ui.home

import com.example.puyo_base_simulator.ui.home.PuyoColor
import com.example.puyo_base_simulator.ui.home.PuyoColor.Companion.getPuyoColor
import java.io.Serializable
import java.util.*

class Field : Serializable {
    @JvmField
    var nextField: Field? = null
    var field = Array(13) { i -> Array(6) { j -> Puyo(i+1, j+1, PuyoColor.EMPTY)} }
    private fun getFieldContent(row: Int, column: Int) : Puyo { return field[row-1][column-1]}
    private var heights = Array(6 ) { 0 }
    fun getHeight(column: Int) : Int { return heights[column-1]}
    @JvmField
    var disappearPuyo = mutableListOf<Puyo>()
    @JvmField
    var accumulatedPoint = 0
    @JvmField
    var bonus = 0
    private var chainNum = 0

    fun addPuyo(column: Int, color: PuyoColor): Boolean {
        val row = heights[column-1] + 1
        if (row == 14) return false
        field[row-1][column-1] = Puyo(row, column, color)
        heights[column-1]++
        return true
    }

    fun allClear() = heights.all {it == 0}

    fun isDisappear(puyo: Puyo) = disappearPuyo.contains(puyo)

    fun evalNextField() {
        val newField = Field()
        newField.chainNum = chainNum + 1
        var connectionBonus = 0
        // 消えるぷよを探す
        for (puyo in field.sliceArray(0..11).flatten()) {
            if (disappearPuyo.contains(puyo) || puyo.color == PuyoColor.EMPTY) continue
            val connection = getConnection(puyo)
            if (connection.size >= 4) {
                disappearPuyo.addAll(connection)
                connectionBonus += connectionBonusConstant(connection.size)
            } else {
                newField.addPuyo(puyo.column, puyo.color)
            }
        }
        if (newField.allClear()) accumulatedPoint += allClearBonus
        if (disappearPuyo.size == 0) {
            chainNum = 1
            return
        }

        // 消えるぷよがある場合のみ次の盤面を評価
        val colorNum = disappearPuyo.map {it.color}.toSet().size
        var bonus = colorBonusConstant[colorNum] + connectionBonus + chainBonusConstant[chainNum]
        if (bonus == 0) bonus = 1
        val point = accumulatedPoint + bonus * disappearPuyo.size * 10
        this.bonus = bonus
        accumulatedPoint = point
        nextField = newField
        nextField!!.accumulatedPoint = point
        newField.evalNextField()
    }

    private fun getNeighborPuyo(puyo: Puyo): List<Puyo> {
        val row = puyo.row
        val column = puyo.column
        val neighbors = mutableListOf<Puyo>()
        // left
        if (column != 1) {
            neighbors.add(getFieldContent(row,column - 1))
        }
        // right
        if (column != 6) {
            neighbors.add(getFieldContent(row,column + 1))
        }
        // up
        if (row != 12) {
            neighbors.add(getFieldContent(row + 1,column))
        }
        // down
        if (row != 1) {
            neighbors.add(getFieldContent(row - 1,column))
        }
        return neighbors.filter {it.color != PuyoColor.EMPTY}
    }

    // 連結数
    private fun getConnection(puyo: Puyo): List<Puyo> {
        val connected = ArrayList<Puyo>()
        if (puyo.color === PuyoColor.EMPTY) return connected
        val sameColorStack = Stack<Puyo>()
        sameColorStack.push(puyo)
        connected.add(puyo)
        while (!sameColorStack.isEmpty()) {
            for (p in getNeighborPuyo(sameColorStack.pop())) {
                if (p.color === puyo.color && !connected.contains(p)) {
                    sameColorStack.push(p)
                    connected.add(p)
                }
            }
        }
        return connected
    }

    override fun toString(): String {
        return field.joinToString("") { row -> row.map { it.color.char }.joinToString("") }
    }

    companion object {
        private val chainBonusConstant = intArrayOf(0, 0, 8, 16, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 480, 512)
        private val colorBonusConstant = intArrayOf(0, 0, 3, 6, 12)
        private const val allClearBonus = 2100
        private fun connectionBonusConstant(connectionNum: Int): Int {
            return when {
                connectionNum <= 4 -> 0
                connectionNum <= 10 -> connectionNum - 3
                else -> 10
            }
        }
        fun from(fieldStr: String) : Field {
            val ret = Field()
            val colorString = Array(13) { i -> fieldStr.padEnd(6*13).substring(i*6, (i+1)*6) }
            ret.field = Array(13) { i -> Array(6) { j -> Puyo(i+1, j+1, getPuyoColor(colorString[i][j]))} }
            for (col in 0..5) {
                var i = 0
                while (ret.field[i++][col].color != PuyoColor.EMPTY) {
                    ret.heights[col]++
                }
            }
            return ret
        }
    }
}