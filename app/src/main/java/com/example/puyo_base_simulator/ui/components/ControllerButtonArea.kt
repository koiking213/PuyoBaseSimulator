package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.layout.*
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
            ActionButton(
                text = "←",
                onClick = onLeftClick,
                enabled = enabled,
                modifier = Modifier.size(size)
            )
            ActionButton(
                text = "→",
                onClick = onRightClick,
                enabled = enabled,
                modifier = Modifier.size(size)
            )
        }
        ActionButton(
            text = "↓",
            onClick = onDownClick,
            enabled = enabled,
            modifier = Modifier.size(size)
        )
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
                .width(size*2)
                .padding(5.dp)
        ) {
            ActionButton(
                text = "A",
                onClick = onAClick,
                enabled = enabled,
                modifier = Modifier.size(size)
            )
        }
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.width(size)
        ) {
            ActionButton(
                text = "B",
                onClick = onBClick,
                enabled = enabled,
                modifier = Modifier.size(size)
            )
        }
    }
}
