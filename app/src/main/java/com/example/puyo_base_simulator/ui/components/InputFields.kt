package com.example.puyo_base_simulator.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@ExperimentalComposeUiApi
@Composable
fun PatternInputField(
    size: Dp,
    onClick: (String) -> Unit,
    textLabel: String
) {
    val (seedError, setSeedError) = remember { mutableStateOf(false) }
    TextFieldWithButton(
        size = size,
        isError = seedError,
        trailingIcon = {
            Icon(
                Icons.Filled.Info,
                contentDescription = "pattern"
            )
        },
        textLabel = textLabel,
        keyboardType = KeyboardType.Ascii,
        onClick = {
            if (isValidPattern(it.text)) {
                setSeedError(false)
                onClick(it.text)
            } else {
                setSeedError(true)
            }
        }
    )
}

@ExperimentalComposeUiApi
@Composable
fun SeedInputField(
    size: Dp,
    onClick: (Int) -> Unit,
    textLabel: String
) {
    val (seedError, setSeedError) = remember { mutableStateOf(false) }
    TextFieldWithButton(
        size = size,
        isError = seedError,
        trailingIcon = {
            Icon(
                Icons.Filled.Info,
                contentDescription = "seed"
            )
        },
        textLabel = textLabel,
        keyboardType = KeyboardType.Number,
        onClick = {
            val seed = getSeed((it.text))
            if (seed != null) {
                setSeedError(false)
                onClick(seed)
            } else {
                setSeedError(true)
            }
        }
    )
}


private fun getSeed(str: String) : Int?{
    return try {
        val seed = str.toInt()
        if (seed in 0..65536) seed
        else throw java.lang.NumberFormatException()
    } catch (e: java.lang.NumberFormatException) {
        null
    }
}

private fun isValidPattern(str: String) : Boolean{
    return str.map {it in "abcde"}.reduce {acc, it -> acc && it}
}

@ExperimentalComposeUiApi
@Composable
fun TextFieldWithButton(
    size: Dp,
    isError: Boolean,
    trailingIcon: @Composable (() -> Unit)? = null,
    textLabel: String,
    keyboardType: KeyboardType,
    onClick: (TextFieldValue) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val state = remember { mutableStateOf(TextFieldValue()) }
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            isError = isError,
            trailingIcon = trailingIcon,
            value = state.value,
            onValueChange = { state.value = it },
            label = { Text(textLabel, fontSize = 10.sp) },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onClick(state.value)
                    keyboardController?.hide()
                }
            ),
            singleLine = true,
            modifier = Modifier.size((10*textLabel.length).dp, size)
        )
    }
}
