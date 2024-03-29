package com.example.puyo_base_simulator.data

import com.example.puyo_base_simulator.data.PuyoColor.Companion.getPuyoColor
import com.example.puyo_base_simulator.utils.Rotation
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*

class Field : Serializable {
    var nextField: Field? = null
    var field = Array(13) { i -> Array(6) { j -> Puyo(i+1, j+1, PuyoColor.EMPTY) } }
    private fun getFieldContent(row: Int, column: Int) : Puyo { return field[row-1][column-1]}
    private var heights = Array(6 ) { 0 }
    private var uppermostRowAvailable = Array(6) {true}
    fun getHeight(column: Int) : Int { return heights[column-1]}
    var disappearPuyo = mutableListOf<Puyo>()
    var accumulatedPoint = 0  // 試合の累計ポイント
    var chainPoint = 0  // 連鎖中の累計ポイント
    var fieldPoint = 0  // 盤面でのポイント
    var bonus = 0
    var chainNum = 0
    val disappearingField : Field
        get() {
            val f = SerializationUtils.clone(this) as Field
            for (p in f.field.flatten()) {
                if (p in f.disappearPuyo) p.color = PuyoColor.DISAPPEAR
            }
            return f
        }

    fun addPuyo(column: Int, color: PuyoColor): Boolean {
        val row = heights[column-1] + 1
        if (row == 14) {
            return if (uppermostRowAvailable[column-1]) {
                uppermostRowAvailable[column-1] = false
                true
            } else {
                false
            }
        }
        field[row-1][column-1] = Puyo(row, column, color)
        heights[column-1]++
        return true
    }

    fun allClear() = heights.all {it == 0}

    fun isDisappear(puyo: Puyo) = disappearPuyo.contains(puyo)

    fun setPairOnField(tsumoInfo: TsumoInfo): Field? {
        val col = tsumoInfo.column
        val mainColor = tsumoInfo.currentColor[0]
        val subColor = tsumoInfo.currentColor[1]
        val newField = SerializationUtils.clone(this) as Field
        val success = when (tsumoInfo.rot) {
            Rotation.DEGREE0 -> newField.addPuyo(col, mainColor) and newField.addPuyo(col, subColor)
            Rotation.DEGREE90 -> newField.addPuyo(col, mainColor) and newField.addPuyo(col + 1, subColor)
            Rotation.DEGREE180 -> newField.addPuyo(col, subColor) and newField.addPuyo(col, mainColor) // 上下が逆転している
            Rotation.DEGREE270 -> newField.addPuyo(col, mainColor) and newField.addPuyo(col - 1, subColor)
        }
        return if (success) newField else null
    }

    fun getLast() : Field {
        return this.nextField?.getLast() ?: this
    }

    fun evalNextField() {
        val newField = Field()
        if (chainNum == 1) chainPoint = 0
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
        // 13段目にぷよがあれば、消えずに降ってくる
        for (puyo in field[12]) {
            if (puyo.color != PuyoColor.EMPTY) {
                newField.addPuyo(puyo.column, puyo.color)
            }
        }

        if (disappearPuyo.size == 0) {
            chainNum = 0
            return
        }
        if (newField.allClear()) accumulatedPoint += allClearBonus

        // 消えるぷよがある場合のみ次の盤面を評価
        val colorNum = disappearPuyo.map {it.color}.toSet().size
        bonus = colorBonusConstant[colorNum] + connectionBonus + chainBonusConstant[chainNum]
        if (bonus == 0) bonus = 1
        fieldPoint = bonus * disappearPuyo.size * 10
        accumulatedPoint += fieldPoint
        chainPoint += fieldPoint
        nextField = newField
        nextField!!.accumulatedPoint = accumulatedPoint
        nextField!!.chainPoint = chainPoint
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
        return neighbors.filter {it.color != PuyoColor.EMPTY }
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
        private val chainBonusConstant = intArrayOf(0, 8, 16, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 480, 512)
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
            ret.field = Array(13) { i -> Array(6) { j -> Puyo(i+1, j+1, getPuyoColor(colorString[i][j])) } }
            for (col in 0..5) {
                var i = 0
                while (i < 13 && ret.field[i++][col].color != PuyoColor.EMPTY) {
                    ret.heights[col]++
                }
            }
            return ret
        }
    }
}