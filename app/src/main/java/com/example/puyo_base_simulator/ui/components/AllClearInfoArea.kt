package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.puyo_base_simulator.data.AllClearInfo


@Composable
fun AllClearInfoArea(
    info: AllClearInfo,
    onCheckClick: () -> Unit,
    enabled: Boolean,
) {
    Column() {
        ActionButton(
            text = "全消しチェック",
            onClick = onCheckClick,
            enabled = enabled,
        )
        for (i in 0..1) {
            Text("${i+1}手先で全消し可能数: ${info.get(i).size}")
        }
    }
}