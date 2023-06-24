package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.example.puyo_base_simulator.R


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
                icon = painterResource(id = R.drawable.ic_baseline_undo_24),
                size = size,
                enabled = enabled,
                onClick = onUndoClick
            )
            ActionIcon(
                icon = painterResource(id = R.drawable.ic_baseline_redo_24),
                size = size,
                enabled = enabled,
                onClick = onRedoClick
            )
        }
    }
}
