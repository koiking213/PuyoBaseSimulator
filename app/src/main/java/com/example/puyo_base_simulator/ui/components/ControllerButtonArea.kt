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
) {
    Row {
        CursorKeys(onLeftClick, onRightClick, onDownClick, size)
        RotationKeys(onAClick, onBClick, size)
    }
}

@Composable
fun CursorKeys(
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onDownClick: () -> Unit,
    size: Dp,
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
                modifier = Modifier.size(size)
            )
            ActionButton(
                text = "→",
                onClick = onRightClick,
                modifier = Modifier.size(size)
            )
        }
        ActionButton(
            text = "↓",
            onClick = onDownClick,
            modifier = Modifier.size(size)
        )
    }
}

@Composable
fun RotationKeys(
    onAClick: () -> Unit,
    onBClick: () -> Unit,
    size: Dp,
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
                modifier = Modifier.size(size)
            )
        }
    }
}
