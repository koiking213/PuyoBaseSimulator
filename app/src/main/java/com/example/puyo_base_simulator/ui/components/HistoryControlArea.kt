package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp


@Composable
fun HistoryControlArea(
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onSliderChange: (Float) -> Unit,
    sliderValue: Float,
    max: Int,
    size: Dp,
    enabled: Boolean,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SliderFrame(
            index = sliderValue,
            max = max,
            onValueChange = onSliderChange,
            enabled = enabled,
            modifier = Modifier.height(size/2)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionIcon(
                icon = Icons.Filled.Undo,
                size = size,
                enabled = enabled,
                onClick = onUndoClick
            )
            ActionIcon(
                icon = Icons.Filled.Redo,
                size = size,
                enabled = enabled,
                onClick = onRedoClick
            )
        }
    }
}
