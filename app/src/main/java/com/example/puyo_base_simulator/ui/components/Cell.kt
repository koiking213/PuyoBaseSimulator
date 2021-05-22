package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp


@Composable
fun Cell(id: Int, size: Dp) {
    Image(
        painter = painterResource(id),
        contentDescription = null,
        modifier = Modifier.size(size, size)
    )
}
