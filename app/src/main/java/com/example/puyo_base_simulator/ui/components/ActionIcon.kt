package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp


@Composable
fun ActionIcon(
    icon: ImageVector,
    size: Dp,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box (
            ) {
        Icon(
            icon,
            contentDescription = "move left",
            modifier = if (enabled) {
                Modifier
                    .background(Color.Red)
                    .size(size)
                    .clickable(onClick = onClick)
            } else {
                Modifier.size(size)
            }
        )
    }
}