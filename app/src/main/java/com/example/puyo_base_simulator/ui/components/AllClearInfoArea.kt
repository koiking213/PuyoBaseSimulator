package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.example.puyo_base_simulator.data.AllClearInfo


@Composable
fun AllClearInfoArea(
    info: AllClearInfo,
    onCheckClick: () -> Unit,
    enabled: Boolean,
    loading: Boolean,
) {
    Row {
        if (loading) {
            CircularProgressIndicator()
        }
        Column {
            ActionButton(
                text = "全消しチェック",
                onClick = onCheckClick,
                enabled = enabled,
            )
            Text("現在ツモで全消し可能数: ${info.get(0).size}", fontSize = 10.sp)
            Text("ねくすとで全消し可能数: ${info.get(1).size}", fontSize = 10.sp)
            Text("ねくねくで全消し可能数: ${info.get(2).size}", fontSize = 10.sp)
        }
    }
}