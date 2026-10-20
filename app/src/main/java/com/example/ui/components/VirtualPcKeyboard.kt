package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VirtualPcKeyboard(
    onKeyPress: (String) -> Unit,
    onCloseKeyboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isShiftOn by remember { mutableStateOf(false) }
    var isCapsOn by remember { mutableStateOf(false) }

    val fnRow = listOf("ESC", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "DEL")
    val numRow = if (isShiftOn) {
        listOf("~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "BACKSPACE")
    } else {
        listOf("`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "BACKSPACE")
    }
    val rowQ = listOf("TAB", "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "[", "]")
    val rowA = listOf("CAPS", "a", "s", "d", "f", "g", "h", "j", "k", "l", ";", "'", "ENTER")
    val rowZ = listOf("SHIFT", "z", "x", "c", "v", "b", "n", "m", ",", ".", "/", "↑", "CLR")
    val bottomRow = listOf("CTRL", "WIN", "ALT", "SPACE", "←", "↓", "→")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("virtual_pc_keyboard"),
        color = Color(0xF508101E),
        tonalElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WINDOWS 10 VIRTUAL PC KEYBOARD (QWERTY + FN + ARROWS)",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF00D2FF)
                )
                IconButton(
                    onClick = onCloseKeyboard,
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("hide_keyboard_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardHide,
                        contentDescription = "Hide PC Keyboard",
                        tint = Color.White
                    )
                }
            }

            KeyboardRow(keys = fnRow, isUpper = false, highlightKeys = setOf("ESC", "F11", "DEL")) { key ->
                onKeyPress(key)
            }
            KeyboardRow(keys = numRow, isUpper = false, highlightKeys = setOf("BACKSPACE")) { key ->
                onKeyPress(key)
                if (isShiftOn) isShiftOn = false
            }
            KeyboardRow(keys = rowQ, isUpper = isShiftOn xor isCapsOn, highlightKeys = setOf("TAB")) { key ->
                onKeyPress(key)
                if (isShiftOn && key.length == 1) isShiftOn = false
            }
            KeyboardRow(
                keys = rowA,
                isUpper = isShiftOn xor isCapsOn,
                highlightKeys = setOf("CAPS", "ENTER"),
                activeToggleKeys = if (isCapsOn) setOf("CAPS") else emptySet()
            ) { key ->
                if (key == "CAPS") {
                    isCapsOn = !isCapsOn
                } else {
                    onKeyPress(key)
                    if (isShiftOn && key.length == 1) isShiftOn = false
                }
            }
            KeyboardRow(
                keys = rowZ,
                isUpper = isShiftOn xor isCapsOn,
                highlightKeys = setOf("SHIFT", "↑", "CLR"),
                activeToggleKeys = if (isShiftOn) setOf("SHIFT") else emptySet()
            ) { key ->
                if (key == "SHIFT") {
                    isShiftOn = !isShiftOn
                } else {
                    onKeyPress(key)
                    if (isShiftOn && key.length == 1) isShiftOn = false
                }
            }

            // Bottom Space & Arrow Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                bottomRow.forEach { key ->
                    val weight = when (key) {
                        "SPACE" -> 4.2f
                        "WIN", "CTRL", "ALT" -> 1.2f
                        else -> 1f
                    }
                    val isWinKey = key == "WIN"
                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .height(38.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                if (isWinKey) Color(0xFF0078D7) else Color(0xFF17263C)
                            )
                            .border(
                                1.dp,
                                if (isWinKey) Color(0xFF00D2FF) else Color(0xFF2B4263),
                                RoundedCornerShape(5.dp)
                            )
                            .clickable { onKeyPress(key) }
                            .testTag("pc_key_${key.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (key == "SPACE") "SPACEBAR" else key,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyboardRow(
    keys: List<String>,
    isUpper: Boolean,
    highlightKeys: Set<String> = emptySet(),
    activeToggleKeys: Set<String> = emptySet(),
    onKeyClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        keys.forEach { rawKey ->
            val displayKey = if (rawKey.length == 1 && rawKey[0].isLetter()) {
                if (isUpper) rawKey.uppercase() else rawKey.lowercase()
            } else {
                rawKey
            }
            val weight = when (rawKey) {
                "BACKSPACE", "ENTER", "SHIFT", "CAPS" -> 1.65f
                "TAB" -> 1.35f
                else -> 1f
            }
            val isHighlighted = rawKey in highlightKeys
            val isActive = rawKey in activeToggleKeys
            val bg = when {
                isActive -> Color(0xFF0078D7)
                isHighlighted -> Color(0xFF1E3250)
                else -> Color(0xFF132035)
            }
            Box(
                modifier = Modifier
                    .weight(weight)
                    .height(34.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(bg)
                    .border(
                        1.dp,
                        if (isActive) Color(0xFF00D2FF) else Color(0xFF263B59),
                        RoundedCornerShape(4.dp)
                    )
                    .clickable {
                        onKeyClick(
                            when (rawKey) {
                                "BACKSPACE" -> "BACKSPACE"
                                "ENTER" -> "ENTER"
                                "TAB" -> "TAB"
                                "ESC" -> "ESC"
                                "F11" -> "F11"
                                "CLR" -> "CLR"
                                else -> displayKey
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (rawKey) {
                        "BACKSPACE" -> "⌫"
                        "ENTER" -> "↵"
                        else -> displayKey
                    },
                    color = if (isHighlighted) Color(0xFF00D2FF) else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
