package com.example.puyo_base_simulator.ui.home

import com.example.puyo_base_simulator.ui.home.PuyoColor
import com.example.puyo_base_simulator.ui.home.PuyoColor.Companion.getPuyoColor
import java.io.Serializable
import java.util.*

class Field : Serializable {
    @JvmField
    var nextField: Field? = null
    private var field: Array<Array<Puyo?>>
    fun getFieldContent(row: Int, column: Int) : Puyo? { return field[row-1][column-1]}
    private var heights = intArrayOf(0, 0, 0, 0, 0, 0)
    fun getHeight(column: Int) : Int { return heights[column-1]}
    @JvmField
    var disappearPuyo: MutableList<Puyo?> = ArrayList()
    @JvmField
    var accumulatedPoint = 0
    @JvmField
    var bonus = 0
    private var chainNum = 0
    private val allClearBonus = 2100
    private val chainBonusConstant = intArrayOf(0, 0, 8, 16, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 480, 512)
    private val colorBonusConstant = intArrayOf(0, 0, 3, 6, 12)
    private fun connectionBonusConstant(connectionNum: Int): Int {
        return if (connectionNum <= 4) {
            0
        } else if (connectionNum <= 10) {
            connectionNum - 3
        } else {
            10
        }
    }

    init {
        field = Array(13) { i -> Array(6) { j -> Puyo(i+1, j+1, PuyoColor.EMPTY)} }
    }

    fun addPuyo(column: Int, color: PuyoColor?): Boolean {
        val row = heights[column-1] + 1
        if (row == 14) return false
        field[row-1][column-1] = Puyo(row, column, color!!)
        heights[column-1]++
        return true
    }

    fun allClear(): Boolean {
        for (h in heights) {
            if (h > 0) return false
        }
        return true
    }

    fun isDisappear(puyo: Puyo?): Boolean {
        return disappearPuyo.contains(puyo)
    }

    fun evalNextField() {
        val newField = Field()
        newField.chainNum = chainNum + 1
        val colors: MutableSet<PuyoColor> = HashSet()
        var connectionBonus = 0
        // 消えるぷよを探す
        for (i in 1..12) {
            for (j in 1..6) {
                val puyo = getFieldContent(i,j)
                val connection = getConnection(puyo)
                if (connection.size >= 4) {
                    disappearPuyo.add(puyo)
                    // 色ボーナスの評価
                    colors.add(puyo!!.color)
                    // 連結ボーナスの評価
                    var connectionIsNew = true
                    for (p in disappearPuyo) {
                        if (p!!.row < i || p.row == i && p.column < j) {
                            connectionIsNew = false
                            break
                        }
                    }
                    if (connectionIsNew) {
                        connectionBonus += connectionBonusConstant(connection.size)
                    }
                } else if (puyo!!.color !== PuyoColor.EMPTY) {
                    newField.addPuyo(j, puyo!!.color)
                }
            }
        }
        if (newField.allClear()) accumulatedPoint += allClearBonus
        if (disappearPuyo.size == 0) {
            chainNum = 1
            return
        }

        // 消えるぷよがある場合のみ次の盤面を評価
        var bonus = colorBonusConstant[colors.size] + connectionBonus + chainBonusConstant[chainNum]
        if (bonus == 0) bonus = 1
        val point = accumulatedPoint + bonus * disappearPuyo.size * 10
        this.bonus = bonus
        accumulatedPoint = point
        nextField = newField
        nextField!!.accumulatedPoint = point
        newField.evalNextField()
    }

    private fun getNeighborPuyo(puyo: Puyo?): List<Puyo?> {
        val row = puyo!!.row
        val column = puyo.column
        val ret: MutableList<Puyo?> = ArrayList()
        // left
        if (column != 1 && getFieldContent(row,column - 1)!!.color !== PuyoColor.EMPTY) {
            ret.add(getFieldContent(row,column - 1))
        }
        // right
        if (column != 6 && getFieldContent(row,column + 1)!!.color !== PuyoColor.EMPTY) {
            ret.add(getFieldContent(row,column + 1))
        }
        // up
        if (row != 12 && getFieldContent(row + 1,column)!!.color !== PuyoColor.EMPTY) {
            ret.add(getFieldContent(row + 1,column))
        }
        // down
        if (row != 1 && getFieldContent(row - 1,column)!!.color !== PuyoColor.EMPTY) {
            ret.add(getFieldContent(row - 1,column))
        }
        return ret
    }

    // 連結数
    private fun getConnection(puyo: Puyo?): List<Puyo?> {
        val connected = ArrayList<Puyo?>()
        if (puyo!!.color === PuyoColor.EMPTY) return connected
        val sameColorStack = Stack<Puyo?>()
        sameColorStack.push(puyo)
        connected.add(puyo)
        while (!sameColorStack.isEmpty()) {
            val currentPuyo = sameColorStack.pop()
            val neighbors = getNeighborPuyo(currentPuyo)
            for (p in neighbors) {
                if (p!!.color === puyo!!.color && !connected.contains(p)) {
                    sameColorStack.push(p)
                    connected.add(p)
                }
            }
        }
        return connected
    }

    override fun toString(): String {
        val str = StringBuilder()
        for (i in 1..13) {
            for (j in 1..6) {
                str.append(getFieldContent(i,j)!!.color.char)
            }
        }
        return str.toString()
    }

    // fromString
    companion object {
        fun from(fieldStr: String) : Field {
            var ret = Field()
            var fieldStr = fieldStr
            val fieldStrBuilder = StringBuilder(fieldStr)
            while (fieldStrBuilder.length < 6 * 13) {
                fieldStrBuilder.append(" ")
            }
            fieldStr = fieldStrBuilder.toString()
            var idx = 0
            ret.field = Array(13) { arrayOfNulls(6) }
            for (i in 1..13) {
                for (j in 1..6) {
                    val color = getPuyoColor(fieldStr[idx])
                    ret.field[i - 1][j - 1] = Puyo(i, j, color!!)
                    if (color !== PuyoColor.EMPTY) {
                        ret.heights[j - 1]++
                    }
                    idx++
                }
            }
            ret.chainNum = 1
            return ret
        }
    }
}