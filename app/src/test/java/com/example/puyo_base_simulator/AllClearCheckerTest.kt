package com.example.puyo_base_simulator
import com.example.puyo_base_simulator.data.*
import com.example.puyo_base_simulator.data.Field.Companion.from
import com.example.puyo_base_simulator.utils.Rotation
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AllClearCheckerTest {
    private val colors = arrayOf(PuyoColor.BLUE, PuyoColor.RED)
    private val tsumoInfo = TsumoInfo(colors, Array(2){colors}, 3, Rotation.DEGREE0)
    /*
     g
     rggbbr
     bbbgrr
     rrrbbb
    */
    private val field = from("rrrbbbbbbgrrrggbbrg")

    @Test
    fun testSearchAllClearFields() {
        val controller1 = TsumoController("rrrrggggbbbb", 1)
        val ret1 = searchAllClearFields(field, controller1, 2)
        assertThat(ret1.get(0).isNotEmpty())
        assertThat(ret1.get(1).isNotEmpty())
        assertThat(ret1.get(2).isEmpty())

        val controller2 = TsumoController("bbbbrrggbbbb", 1)
        val ret2 = searchAllClearFields(field, controller2, 2)
        assertThat(ret2.get(0).isEmpty())
        assertThat(ret2.get(1).isEmpty())
        assertThat(ret2.get(2).isNotEmpty())
    }
    @Test
    fun testGenerateAllTsumoPattern() {
        val result = generateAllTsumoPattern(tsumoInfo)
        assertThat(result.size).isEqualTo(22)
    }
    @Test
    fun testGenerateAllFields() {
        val result = generateAllFields(field, tsumoInfo)
        assertThat(result.size).isEqualTo(22)
    }
    @Test
    fun testGetAllClearFields() {
        val fields = generateAllFields(field, tsumoInfo)
        val result = getAllClearFields(fields)
        assertThat(result.size).isEqualTo(1)
    }
}