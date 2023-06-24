package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.example.puyo_base_simulator.R

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

    val windowSize = LocalConfiguration.current.screenWidthDp.dp
    val iconSize = min(size, windowSize / 6)  // Adjust the icon size based on the window size

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp)
    ) {
        Row(
            horizontalArrangement= Arrangement.SpaceBetween,
            modifier = Modifier.padding(5.dp)
        ) {
            ActionIcon(icon = rememberVectorPainter(Icons.Filled.ArrowBack), size = iconSize, enabled = enabled, onClick = onLeftClick)
            ActionIcon(icon = rememberVectorPainter(Icons.Filled.ArrowForward), size = iconSize, enabled = enabled, onClick = onRightClick)
        }
        ActionIcon(icon = painterResource(R.drawable.ic_baseline_arrow_downward_24), size = iconSize, enabled = enabled, onClick = onDownClick)
    }
}

@Composable
fun RotationKeys(
    onAClick: () -> Unit,
    onBClick: () -> Unit,
    size: Dp,
    enabled: Boolean,
) {
    val windowSize = LocalConfiguration.current.screenWidthDp.dp
    val iconSize = min(size, windowSize / 6)  // Adjust the icon size based on the window size
    Column(
        modifier = Modifier.padding(5.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .width(iconSize * 2)
                .padding(5.dp)
        ) {
            ActionIcon(icon = painterResource(id = R.drawable.ic_baseline_rotate_right_24), size = iconSize, enabled = enabled, onClick = onAClick)
        }
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.width(iconSize)
        ) {
            ActionIcon(icon = painterResource(id = R.drawable.ic_baseline_rotate_left_24), size = iconSize, enabled = enabled, onClick = onBClick)
        }
    }
}
