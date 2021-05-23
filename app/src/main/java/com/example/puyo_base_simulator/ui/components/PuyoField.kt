package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import com.example.puyo_base_simulator.R
import com.example.puyo_base_simulator.ui.home.Field
import com.example.puyo_base_simulator.ui.home.PuyoColor
import com.example.puyo_base_simulator.ui.home.Rotation
import com.example.puyo_base_simulator.ui.home.TsumoInfo
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
fun NextTsumoFrame(tsumoInfo: TsumoInfo, size: Dp) {
    val colors = createNextTsumoColorMap(tsumoInfo)
    PuyoField(colors, size)
}

@Composable
fun FieldFrame(field: Field, tsumoInfo: TsumoInfo, size: Dp) {
    Row {
        Row(verticalAlignment = Alignment.Bottom)
        {
            SideWall(size)
            Column {
                CurrentTsumoFrame(tsumoInfo, size)
                MainField(field, tsumoInfo, size)
                BottomWall(size)
            }
            SideWall(size)
        }
        NextTsumoFrame(tsumoInfo, size)
    }
}


private fun createNextTsumoColorMap(tsumoInfo: TsumoInfo) : Array<Array<Int>>{
    val ret = Array(4) {Array(2){puyoResourceId(PuyoColor.EMPTY)}}
    ret[0][0] = puyoResourceId(tsumoInfo.nextColor[0][0])
    ret[1][0] = puyoResourceId(tsumoInfo.nextColor[0][1])
    ret[2][1] = puyoResourceId(tsumoInfo.nextColor[1][0])
    ret[3][1] = puyoResourceId(tsumoInfo.nextColor[1][1])
    return ret
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
fun MainField(field: Field, tsumoInfo: TsumoInfo, size: Dp) {
    val colors = Array(13) { i -> Array(6) { j -> puyoResourceId(field.field[i][j].color)} }
    val colorsWithDot = dotColors(tsumoInfo = tsumoInfo, field = field, colors)
    PuyoField(colorsWithDot.reversed().toTypedArray(), size)
}


@Composable
fun SideWall(size: Dp) {
    Column {
        for (r in 0..12) {
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