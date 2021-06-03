package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeAppBar(
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = { Text("ぷよ土台シミュレータ") },
        actions = {
            // TODO: IconButtonで置き換えられる？
            Icon(
                imageVector = Icons.Filled.Settings,
                modifier = Modifier
                    .clickable(onClick = onSettingsClick)
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = ""
            )
        },
    )
}

@Composable
fun SettingAppBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { Text("設定") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ChevronLeft, null)
            }
        }
    )
}
