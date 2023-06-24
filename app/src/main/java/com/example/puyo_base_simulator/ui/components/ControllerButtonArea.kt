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

    Row (Modifier.fillMaxWidth()){
        CursorKeys(onLeftClick, onRightClick, onDownClick, size, enabled, Modifier.align(Alignment.CenterVertically))
        Spacer(modifier = Modifier.weight(1f)) // Add a spacer with weight to push the components to the sides
        RotationKeys(onAClick, onBClick, size, enabled, Modifier.align(Alignment.CenterVertically))
    }
}

@Composable
fun CursorKeys(
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onDownClick: () -> Unit,
    size: Dp,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {

    val windowSize = LocalConfiguration.current.screenWidthDp.dp
    val iconSize = min(size, windowSize / 6)  // Adjust the icon size based on the window size

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(5.dp)
    ) {
        Row(
            horizontalArrangement= Arrangement.SpaceBetween,
            modifier = modifier.padding(5.dp)
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
    modifier: Modifier = Modifier,
) {
    val windowSize = LocalConfiguration.current.screenWidthDp.dp
    val iconSize = min(size, windowSize / 6)  // Adjust the icon size based on the window size
    Box(
        modifier = modifier
            .padding(5.dp)
            .size(iconSize * 2)  // Set the size of the Box to be twice the icon size
    ) {
        Box(modifier = Modifier.align(Alignment.TopEnd)) {
            ActionIcon(icon = painterResource(id = R.drawable.ic_baseline_rotate_right_24), size = iconSize, enabled = enabled, onClick = onAClick)
        }
        Box(modifier = Modifier.align(Alignment.BottomStart)) {
            ActionIcon(icon = painterResource(id = R.drawable.ic_baseline_rotate_left_24), size = iconSize, enabled = enabled, onClick = onBClick)
        }
    }
}
