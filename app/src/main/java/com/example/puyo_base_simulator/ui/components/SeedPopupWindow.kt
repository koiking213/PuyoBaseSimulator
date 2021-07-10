package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.puyo_base_simulator.data.PuyoColor
import com.example.puyo_base_simulator.utils.puyoResourceId


@ExperimentalComposeUiApi
@Composable
fun SeedPopupWindow(
    seeds: List<Int>,
    closeFun: () -> Unit,
    getTsumoFun: (Int, Int) -> List<PuyoColor>,
    onSeedClick: (Int) -> Unit,
    onItemRemoveClick: (Int) -> Unit,
) {
    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(focusable = true),
        onDismissRequest = closeFun,
    ) {
        Surface(color = MaterialTheme.colors.background) {
            Card(
                Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Black)
            ) {
                Column {
                    SeedPicker(
                        seeds = seeds,
                        getTsumoFun = getTsumoFun,
                        onSeedClicked = {
                            onSeedClick(it)
                            closeFun()
                        },
                        onItemRemoveClick = onItemRemoveClick
                    )
                }
            }
        }
    }
}

@Composable
fun SeedPicker(
    seeds: List<Int>,
    getTsumoFun: (Int, Int) -> List<PuyoColor>,
    onSeedClicked: (Int) -> Unit,
    onItemRemoveClick: (Int) -> Unit,
) {
    Column {
        LazyColumn ()
        {
            items(seeds.size) { idx ->
                Card(
                    Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color.Black)
                        .clickable(onClick = { onSeedClicked(seeds[idx]) })
                ) {
                    SeedPickerItem(seeds[idx], getTsumoFun, onItemRemoveClick)
                }
            }
        }
    }
}

@Composable
private fun SeedPickerItem(
    seed: Int,
    getTsumoFun: (Int, Int) -> List<PuyoColor>,
    onItemRemoveClick: (Int) -> Unit,
) {
    val (deleted, setDeleted) = remember { mutableStateOf(false) }
    val style = if (deleted) {
        TextStyle(textDecoration = TextDecoration.LineThrough)
    } else {
        TextStyle()
    }

    Row {
        Text("${seed}:",
            fontSize = 12.sp,
            style = style
        )
        val tsumoNum = 5
        val emptyPair = Array(2) { puyoResourceId(PuyoColor.EMPTY) }
        val colors = Array(tsumoNum) { i ->
            getTsumoFun(seed, i).map { puyoResourceId(it) }.reversed().toTypedArray()
        }
        val field = Array(tsumoNum * 2) { i -> if (i % 2 == 0) colors[i / 2] else emptyPair }
        PuyoField(transpose(field), 12.dp)
        ActionIcon(icon = Icons.Filled.Clear, size = 20.dp) {
            onItemRemoveClick(seed)
            setDeleted(true)
        }
    }
}

private inline fun <reified T> transpose(mat: Array<Array<T>>) : Array<Array<T>>{
    val row = mat.size
    val col = mat[0].size
    return Array(col) { i -> Array(row) { j -> mat[j][i]} }

}