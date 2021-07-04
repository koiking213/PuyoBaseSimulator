package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.puyo_base_simulator.data.Base
import com.example.puyo_base_simulator.ui.home.TsumoController
import com.example.puyo_base_simulator.utils.getColor


@ExperimentalComposeUiApi
@Composable
fun LoadPopupWindow(
    size: Dp,
    closeFun: () -> Unit,
    onShowSeedClick: (Int) -> MutableList<Base>,
    onShowPatternClick: (String) -> MutableList<Base>,
    onShowAllClick: () -> MutableList<Base>,
    onFieldClick: (Base) -> Unit,
) {
    val (bases, setBases) = remember { mutableStateOf(mutableListOf<Base>()) }
        Popup(
            alignment = Alignment.Center,
            properties = PopupProperties(focusable = true),
            onDismissRequest = closeFun,
        ) {
            Surface (color = MaterialTheme.colors.background) {
            Card(
                Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Black)
            ) {
                Column {
                    SeedInputField(
                        size = size,
                        onClick = { setBases(onShowSeedClick(it)) },
                        textLabel = "search by seed"
                    )
                    PatternInputField(
                        size = size,
                        onClick = { setBases(onShowPatternClick(it)) },
                        textLabel = "search by pattern"
                    )
                    ActionButton(
                        text = "すべて表示",
                        onClick = {
                            setBases(onShowAllClick())
                        },
                    )
                    FieldPicker(
                        header = "${bases.size}件",
                        bases = bases,
                        onFieldClicked = {
                            onFieldClick(it)
                            closeFun()
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun FieldPicker(
    header: String,
    bases: List<Base>,
    onFieldClicked: (Base) -> Unit
) {
    val rows = 5
    val chunkedList = bases.chunked(rows)
    Column {
        Text(header, style = MaterialTheme.typography.h5)  // stickyHeaderとどう使い分ける？
        Divider()
        LazyColumn (
            modifier = Modifier.size((60*5).dp, 200.dp)
                )
        {
            items(chunkedList.size) { idx ->
                Row {
                    chunkedList[idx].forEachIndexed { _, base ->
                        Card(
                            Modifier
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .border(1.dp, Color.Black)
                                .clickable(onClick = { onFieldClicked(base) })
                        ) {
                            FieldPickerItem(base)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldPickerItem(base: Base) {
    val numOfPlacement =  TsumoController.getNumOfPlacement(base.placementHistory)
    Column {
        Text(
            if (base.allClear) {
                "全消し"
            } else {
                ""
            }
            ,fontSize=9.sp
        )
        Text("${numOfPlacement}手目", fontSize=9.sp)
        Text("${base.point}点", fontSize=9.sp)

        val colors = fieldStrToColors(base.field)
        PuyoField(colors.reversed().toTypedArray(), 10.dp)
    }
}
private fun fieldStrToColors(str: String): Array<Array<Int>> {
    return Array(13) { i -> Array(6) { j -> getColor(str[i * 6 + j]) } }
}
