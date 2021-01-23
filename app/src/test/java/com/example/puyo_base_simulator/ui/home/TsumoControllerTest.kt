package com.example.puyo_base_simulator.ui.home

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test


class TsumoControllerTest {
    private val mTC = TsumoController("rgrbyyrrgbrb", 0)
    @Before
    fun setUp() {
    }

    @Test
    fun pushPlacementOrder() {
    }

    @Test
    fun popPlacementOrder() {
        val tc = TsumoController("rgrbyyrrgbrb", 0)
        tc.pushPlacementOrder()
        val p = tc.popPlacementOrder()
        Truth.assertThat(p.currentCursorColumnIndex).isEqualTo(3)
        Truth.assertThat(p.currentCursorRotate).isEqualTo(Rotation.DEGREE0)
        Truth.assertThat(p.tsumoCounter).isEqualTo(0)
    }

    @Test
    fun restorePlacement() {
        val tc = TsumoController("rgrbyyrrgbrb", 0)
        tc.incrementTsumo()
        tc.rotateCurrentLeft()
        tc.moveCurrentLeft()
        tc.pushPlacementOrder()
        tc.pushPlacementOrder()
        val p = tc.popPlacementOrder()
        val tc2 = TsumoController("rgrbyyrrgbrb", 0)
        tc2.restorePlacement(p)
        tc2.pushPlacementOrder()
        val p2 = tc2.popPlacementOrder()
        Truth.assertThat(p2.toString()).isEqualTo(p.toString())
    }

    @Test
    fun placementOrderToString() {
        val tc = TsumoController("rgrbyyrrgbrb", 0)
        tc.pushPlacementOrder()
        val str = tc.placementOrderToString()
        val p = tc.popPlacementOrder()
        Truth.assertThat(str).isEqualTo(p.toString())
    }

    @Test
    fun stringToPlacementOrder() {
        val tc = TsumoController("rgrbyyrrgbrb", 0)
        tc.pushPlacementOrder()
        val tc2 = TsumoController("rgrbyyrrgbrb", 0)
        val str = tc.placementOrderToString()
        tc2.stringToPlacementOrder(str)
        Truth.assertThat(tc.popPlacementOrder().toString()).isEqualTo(tc2.popPlacementOrder().toString())
    }

    @Test
    fun incrementTsumo() {
        val tc = TsumoController("rgrbyyrrgbrb", 0)
        tc.incrementTsumo()
        val info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentColor[1]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.currentColor[0]).isEqualTo(PuyoColor.BLUE)
        Truth.assertThat(info.nextColor[0][0]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.nextColor[0][1]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.nextColor[1][0]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.nextColor[1][1]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(3)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(3)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)
    }

    @Test
    fun decrementTsumo() {
        val tc = TsumoController("rgrbyyrrgbrb", 0)
        tc.incrementTsumo()
        tc.decrementTsumo()
        val info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentColor[1]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.currentColor[0]).isEqualTo(PuyoColor.GREEN)
        Truth.assertThat(info.nextColor[0][0]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.nextColor[0][1]).isEqualTo(PuyoColor.BLUE)
        Truth.assertThat(info.nextColor[1][0]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.nextColor[1][1]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(3)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(3)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)
    }

    @Test
    fun makeTsumoInfo() {
        val tc = TsumoController("rgrbyyrrgbrb", 0)
        val info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentColor[1]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.currentColor[0]).isEqualTo(PuyoColor.GREEN)
        Truth.assertThat(info.nextColor[0][0]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.nextColor[0][1]).isEqualTo(PuyoColor.BLUE)
        Truth.assertThat(info.nextColor[1][0]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.nextColor[1][1]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(3)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(3)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)
    }

    @Test
    fun getMainColor() {
        Truth.assertThat(mTC.mainColor).isEqualTo(PuyoColor.GREEN)
    }

    @Test
    fun getSubColor() {
        Truth.assertThat(mTC.subColor).isEqualTo(PuyoColor.RED)
    }

    @Test
    fun moveCurrentLeft() {
        val tc = TsumoController("rgrbyyrrgbrb", 0)
        tc.moveCurrentLeft()
        var info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentColor[1]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.currentColor[0]).isEqualTo(PuyoColor.GREEN)
        Truth.assertThat(info.nextColor[0][0]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.nextColor[0][1]).isEqualTo(PuyoColor.BLUE)
        Truth.assertThat(info.nextColor[1][0]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.nextColor[1][1]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(2)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)
        tc.moveCurrentLeft()
        tc.moveCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(1)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)
    }

    @Test
    fun moveCurrentRight() {
        val tc = TsumoController("rgrbyyrrgbrb", 0)
        tc.moveCurrentRight()
        var info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentColor[1]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.currentColor[0]).isEqualTo(PuyoColor.GREEN)
        Truth.assertThat(info.nextColor[0][0]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.nextColor[0][1]).isEqualTo(PuyoColor.BLUE)
        Truth.assertThat(info.nextColor[1][0]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.nextColor[1][1]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(4)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(4)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)
        tc.moveCurrentRight()
        tc.moveCurrentRight()
        tc.moveCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(6)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(6)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)
    }

    @Test
    fun rotateCurrentLeft() {
        val tc = TsumoController("rgrbyyrrgbrb", 0)

        // その場で回転するテスト
        tc.rotateCurrentLeft()
        var info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentColor[1]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.currentColor[0]).isEqualTo(PuyoColor.GREEN)
        Truth.assertThat(info.nextColor[0][0]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.nextColor[0][1]).isEqualTo(PuyoColor.BLUE)
        Truth.assertThat(info.nextColor[1][0]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.nextColor[1][1]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(3)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(2)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE270)
        tc.rotateCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(3)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(3)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE180)
        tc.rotateCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(3)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(4)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE90)
        tc.rotateCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(3)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(3)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)

        // 左端で回転するテスト
        tc.moveCurrentLeft()
        tc.moveCurrentLeft()
        tc.rotateCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(1)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE270)
        tc.rotateCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(2)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE180)
        tc.rotateCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(3)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE90)
        tc.rotateCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(2)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)

        // 右端で回転するテスト
        tc.moveCurrentRight()
        tc.moveCurrentRight()
        tc.moveCurrentRight()
        tc.moveCurrentRight()
        tc.moveCurrentRight()
        tc.rotateCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(6)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(5)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE270)
        tc.rotateCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(6)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(6)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE180)
        tc.rotateCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(5)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(6)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE90)
        tc.rotateCurrentLeft()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(5)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(5)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)
    }

    @Test
    fun rotateCurrentRight() {
        val tc = TsumoController("rgrbyyrrgbrb", 0)

        // その場で回転するテスト
        tc.rotateCurrentRight()
        var info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentColor[1]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.currentColor[0]).isEqualTo(PuyoColor.GREEN)
        Truth.assertThat(info.nextColor[0][0]).isEqualTo(PuyoColor.RED)
        Truth.assertThat(info.nextColor[0][1]).isEqualTo(PuyoColor.BLUE)
        Truth.assertThat(info.nextColor[1][0]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.nextColor[1][1]).isEqualTo(PuyoColor.YELLOW)
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(3)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(4)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE90)
        tc.rotateCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(3)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(3)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE180)
        tc.rotateCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(3)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(2)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE270)
        tc.rotateCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(3)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(3)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)

        // 左端で回転するテスト
        tc.moveCurrentLeft()
        tc.moveCurrentLeft()
        tc.rotateCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(2)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE90)
        tc.rotateCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(1)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE180)
        tc.rotateCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(1)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE270)
        tc.rotateCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(2)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)

        // 右端で回転するテスト
        tc.moveCurrentRight()
        tc.moveCurrentRight()
        tc.moveCurrentRight()
        tc.moveCurrentRight()
        tc.moveCurrentRight()
        tc.rotateCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(5)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(6)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE90)
        tc.rotateCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(5)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(2)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(5)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE180)
        tc.rotateCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(5)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(1)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(4)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE270)
        tc.rotateCurrentRight()
        info = tc.makeTsumoInfo()
        Truth.assertThat(info.currentMainPos.row).isEqualTo(1)
        Truth.assertThat(info.currentMainPos.column).isEqualTo(5)
        Truth.assertThat(info.currentSubPos.row).isEqualTo(0)
        Truth.assertThat(info.currentSubPos.column).isEqualTo(5)
        Truth.assertThat(info.rot).isEqualTo(Rotation.DEGREE0)
    }
}