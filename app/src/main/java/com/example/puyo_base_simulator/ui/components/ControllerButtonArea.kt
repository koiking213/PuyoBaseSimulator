package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TsumoControlButtonArea(
    onAClick: () -> Unit,
    onBClick: () -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onDownClick: () -> Unit,
    size: Dp,
    enabled: Boolean,
) {
    Row {
        CursorKeys(onLeftClick, onRightClick, onDownClick, size, enabled)
        RotationKeys(onAClick, onBClick, size, enabled)
    }
}

@Composable
fun CursorKeys(
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onDownClick: () -> Unit,
    size: Dp,
    enabled: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp)
    ) {
        Row(
            modifier = Modifier.padding(5.dp)
        ) {
            ActionIcon(icon = Icons.Filled.ArrowLeft, size = size, enabled = enabled, onClick = onLeftClick)
            ActionIcon(icon = Icons.Filled.ArrowRight, size = size, enabled = enabled, onClick = onRightClick)
        }
        ActionIcon(icon = Icons.Filled.ArrowDropDown, size = size, enabled = enabled, onClick = onDownClick)
    }
}

@Composable
fun RotationKeys(
    onAClick: () -> Unit,
    onBClick: () -> Unit,
    size: Dp,
    enabled: Boolean,
) {
    Column(
        modifier = Modifier.padding(5.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .width(size * 2)
                .padding(5.dp)
        ) {
            ActionIcon(icon = Icons.Filled.RotateRight, size = size, enabled = enabled, onClick = onAClick)
        }
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.width(size)
        ) {
            ActionIcon(icon = Icons.Filled.RotateLeft, size = size, enabled = enabled, onClick = onBClick)
        }
    }
}
