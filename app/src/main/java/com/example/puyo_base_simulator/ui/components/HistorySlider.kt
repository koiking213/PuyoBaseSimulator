package com.example.puyo_base_simulator.ui.components

import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.math.max


@Composable
fun SliderFrame(
    onValueChange: (Float) -> Unit,
    index: Float,
    max: Int,
    modifier: Modifier = Modifier
) {
    Slider(
        value = index,
        onValueChange = onValueChange,
        modifier = modifier,
        steps = max(max-1, 0),
        valueRange = 0f..(max.toFloat()),
    )
}
