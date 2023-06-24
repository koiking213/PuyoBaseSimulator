package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp


@Composable
fun ActionIcon(
    icon: Painter,
    size: Dp,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box (
            ) {
        if (enabled) {
            Icon(
                icon,
                contentDescription = "",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier
                        .size(size)
                        .clickable(onClick = onClick)
            )
        } else {
            Icon(
                icon,
                contentDescription = "",
                tint = Color(0xffe0e0e0),
                modifier = Modifier.size(size)
            )
        }
    }
}