@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection")

package com.example.puyo_base_simulator.ui.home

import com.example.puyo_base_simulator.ui.home.Field.Companion.from
import com.example.puyo_base_simulator.ui.home.PuyoColor
import com.google.common.truth.Truth
import org.apache.commons.lang.SerializationUtils
import org.junit.Before
import org.junit.Test
import java.util.*

class FieldTest {
    private var mField: Field? = null
    @Before
    fun setUp() {
        /*
         g
         rggbbr
         bbbgrr
         rrrbbb
        */
        val str = "rrrbbbbbbgrrrggbbrg"
        mField = from(str)
    }

    private fun fillFieldString(str: String): String {
        val remain = 6 * 13 - str.length
        return str + java.lang.String.join("", Collections.nCopies(remain, " "))
    }

    @Test
    fun addPuyo() {
        // add one puyo and check field content and heights
        val f = SerializationUtils.clone(mField) as Field
        Truth.assertThat(f.addPuyo(1, PuyoColor.RED)).isTrue()
        val expected = fillFieldString("rrrbbbbbbgrrrggbbrg     r")
        Truth.assertThat(f.toString()).isEqualTo(expected)
        Truth.assertThat(f.getHeight(1)).isEqualTo(mField!!.getHeight(1) + 1)
        for (i in 2..6) {
            Truth.assertThat(f.getHeight(i)).isEqualTo(mField!!.getHeight(i))
        }

        // fail add puyo
        for (i in 0..19) {
            if (f.getHeight(1) == 13) {
                Truth.assertThat(f.addPuyo(1, PuyoColor.RED)).isFalse()
            } else {
                Truth.assertThat(f.addPuyo(1, PuyoColor.RED)).isTrue()
            }
        }
    }

    @Test
    fun allClear() {
        Truth.assertThat(mField!!.allClear()).isFalse()
        Truth.assertThat(Field().allClear()).isTrue()
    }

    // 4個消し5連鎖
    @Test
    fun isDisappear() {
            var f: Field? = SerializationUtils.clone(mField) as Field
            f!!.evalNextField()
            Truth.assertThat(f.disappearPuyo.isEmpty()).isTrue()
            f.addPuyo(6, PuyoColor.RED)
            f.evalNextField() // 4個消し5連鎖
            Truth.assertThat(f.disappearPuyo.isEmpty()).isFalse()
            Truth.assertThat(f.disappearPuyo.size).isEqualTo(4)
            f = f.nextField
            Truth.assertThat(f!!.disappearPuyo.size).isEqualTo(4)
            f = f.nextField
            Truth.assertThat(f!!.disappearPuyo.size).isEqualTo(4)
            f = f.nextField
            Truth.assertThat(f!!.disappearPuyo.size).isEqualTo(4)
            f = f.nextField
            Truth.assertThat(f!!.disappearPuyo.size).isEqualTo(4)
        }

    @Test
    fun evalNextField() {
        // 基本的な連鎖と点数計算のテスト
        var f: Field? = SerializationUtils.clone(mField) as Field
        f!!.evalNextField()
        Truth.assertThat(f.disappearPuyo.isEmpty()).isTrue()
        f.addPuyo(1, PuyoColor.RED)
        f.addPuyo(6, PuyoColor.RED)
        f.evalNextField() // 4個消し5連鎖
        Truth.assertThat(f.accumulatedPoint).isEqualTo(40)
        f = f.nextField
        Truth.assertThat(f!!.accumulatedPoint).isEqualTo(360)
        f = f.nextField
        Truth.assertThat(f!!.accumulatedPoint).isEqualTo(1000)
        f = f.nextField
        Truth.assertThat(f!!.accumulatedPoint).isEqualTo(2280)
        f = f.nextField
        Truth.assertThat(f!!.accumulatedPoint).isEqualTo(4840)
        Truth.assertThat(f.allClear()).isFalse()

        // 13段目周りのテスト
        val str = "r     r     r     b     b     b     g     g     g     r     r     r     r"
        f = from(str)
        f.evalNextField()
        Truth.assertThat(f.disappearPuyo.isEmpty()).isTrue()
        f.addPuyo(2, PuyoColor.RED)
        f.evalNextField()
        Truth.assertThat(f.disappearPuyo.size).isEqualTo(4)
        f = f.nextField
        Truth.assertThat(f!!.disappearPuyo.size).isEqualTo(4)
        f = f.nextField
        Truth.assertThat(f!!.disappearPuyo.isEmpty()).isTrue()
        Truth.assertThat(f.toString()).isEqualTo(fillFieldString("b     b     b     g     g     g"))
        f.addPuyo(2, PuyoColor.BLUE)
        f.evalNextField()
        Truth.assertThat(f.disappearPuyo.size).isEqualTo(4)
        f = f.nextField
        Truth.assertThat(f.toString()).isEqualTo(fillFieldString("g     g     g"))
    }

    @Test
    fun testToString() {
        val expected = fillFieldString("rrrbbbbbbgrrrggbbrg")
        Truth.assertThat(mField.toString()).isEqualTo(expected)
    }
}