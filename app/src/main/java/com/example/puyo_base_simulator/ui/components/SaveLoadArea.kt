package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.puyo_base_simulator.R


@Composable
fun SaveLoadArea(
    onSaveClick: () -> Unit,
    onLoadClick: () -> Unit,
    onStockClick: () -> Unit,
    onPopClick: () -> Unit,
    enabled: Boolean = true,
) {
    Column (
        modifier = Modifier.padding(5.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_sentiment_very_satisfied_24),
                contentDescription = "",
            )
            ActionButton(
                text = "SAVE",
                onClick = onSaveClick,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                text = "LOAD",
                onClick = onLoadClick,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_sentiment_very_dissatisfied_24),
                contentDescription = "",
            )
            ActionButton(
                text = "STOCK",
                onClick = onStockClick,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                text = "POP",
                onClick = onPopClick,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
        }
    }
}