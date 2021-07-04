package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import com.example.puyo_base_simulator.R
import com.example.puyo_base_simulator.data.Field
import com.example.puyo_base_simulator.data.PuyoColor
import com.example.puyo_base_simulator.utils.Rotation
import com.example.puyo_base_simulator.data.TsumoInfo
import com.example.puyo_base_simulator.utils.puyoResourceId

@Composable
fun PuyoField(colors: Array<Array<Int>>, size: Dp) {
    Column {
        for (row in colors) {
            Row {
                for (c in row) {
                    Cell(c, size)
                }
            }
        }
    }
}


@Composable
fun CurrentTsumoFrame(tsumoInfo: TsumoInfo, size: Dp) {
    val colors = createCurrentTsumoColorMap(tsumoInfo)
    PuyoField(colors, size)
}

@Composable
fun NextTsumoFrame(tsumoInfo: TsumoInfo, size: Dp, showDoubleNext: Boolean) {
    val colors = createNextTsumoColorMap(tsumoInfo, showDoubleNext)
    PuyoField(colors, size)
}

@Composable
fun FieldFrame(
    field: Field,
    tsumoInfo: TsumoInfo,
    size: Dp,
    duringChain: Boolean,
    showDoubleNext: Boolean)
{
    Row {
        Row(verticalAlignment = Alignment.Bottom)
        {
            SideWall(size)
            Column {
                CurrentTsumoFrame(tsumoInfo, size)
                Box() {
                    MainField(field, tsumoInfo, size, duringChain)
                    GhostPuyoMask(size = size)
                }
                BottomWall(size)
            }
            SideWall(size)
        }
        NextTsumoFrame(tsumoInfo, size, showDoubleNext)
    }
}


private fun createNextTsumoColorMap(tsumoInfo: TsumoInfo, showDoubleNext: Boolean) : Array<Array<Int>>{
    val ret = Array(4) { Array(2) { puyoResourceId(PuyoColor.EMPTY) } }
    ret[0][0] = puyoResourceId(tsumoInfo.nextColor[0][0])
    ret[1][0] = puyoResourceId(tsumoInfo.nextColor[0][1])
    ret[2][1] = puyoResourceId(tsumoInfo.nextColor[1][0])
    ret[3][1] = puyoResourceId(tsumoInfo.nextColor[1][1])
    return if (showDoubleNext) ret else ret.sliceArray(0..1)
}

private fun createCurrentTsumoColorMap(tsumoInfo: TsumoInfo) : Array<Array<Int>>{
    val ret = Array(3) {Array(6){puyoResourceId(PuyoColor.EMPTY)}}
    val p1 = tsumoInfo.currentMainPos
    ret[p1.row][p1.column-1] = puyoResourceId(tsumoInfo.currentColor[0])
    val p2 = tsumoInfo.currentSubPos
    ret[p2.row][p2.column-1] = puyoResourceId(tsumoInfo.currentColor[1])
    return ret
}

@Composable
fun Wall(size: Dp) {
    Cell(R.drawable.wall, size)
}

@Composable
fun MainField(field: Field, tsumoInfo: TsumoInfo, size: Dp, duringChain: Boolean) {
    val colors = Array(13) { i -> Array(6) { j -> puyoResourceId(field.field[i][j].color)} }
    if (duringChain) {
        PuyoField(colors.reversed().toTypedArray(), size)
    } else {
        val colorsWithDot = dotColors(tsumoInfo = tsumoInfo, field = field, colors)
        PuyoField(colorsWithDot.reversed().toTypedArray(), size)
    }
}

@Composable
fun GhostPuyoMask(size: Dp) {
    Row {
        for (c in 0..5) {
            Cell(R.drawable.ghost_puyo_mask, size)
        }
    }
}

@Composable
fun SideWall(size: Dp) {
    Column {
        for (r in 0..13) {
            Wall(size)
        }
    }
}

@Composable
fun BottomWall(size: Dp) {
    Row {
        for (c in 0..5) {
            Wall(size)
        }
    }
}



private fun dotColors(tsumoInfo: TsumoInfo, field: Field, colors: Array<Array<Int>>) : Array<Array<Int>> {
    // draw current
    val mainColor = tsumoInfo.currentColor[0]
    val subColor = tsumoInfo.currentColor[1]

    // draw dot
    when (tsumoInfo.rot) {
        Rotation.DEGREE0 -> drawDot(tsumoInfo.currentMainPos.column, listOf(mainColor, subColor), field, colors)
        Rotation.DEGREE90, Rotation.DEGREE270 -> {
            drawDot(tsumoInfo.currentMainPos.column, listOf(mainColor), field, colors)
            drawDot(tsumoInfo.currentSubPos.column, listOf(subColor), field, colors)
        }
        Rotation.DEGREE180 -> drawDot(tsumoInfo.currentMainPos.column, listOf(subColor, mainColor), field, colors)
    }
    return colors
}


private fun getDotResource(color: PuyoColor): Int {
    return when (color) {
        PuyoColor.RED -> R.drawable.dotr
        PuyoColor.BLUE -> R.drawable.dotb
        PuyoColor.GREEN -> R.drawable.dotg
        PuyoColor.YELLOW -> R.drawable.doty
        PuyoColor.PURPLE -> R.drawable.dotp
        PuyoColor.EMPTY -> R.drawable.blank
        PuyoColor.DISAPPEAR -> R.drawable.blank
    }
}

// リストで渡された順に下から積み上げる
private fun drawDot(column: Int, dots: List<PuyoColor>, field: Field, colors: Array<Array<Int>>) {
    var row = field.getHeight(column) + 1
    for (dot in dots) {
        if (row <= 13) {
            colors[row-1][column-1] = getDotResource(dot)
            row++
        }
    }
}