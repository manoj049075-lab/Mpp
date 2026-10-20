package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun VirtualMouseOverlay(
    mousePosition: Offset,
    isRightClickMenuVisible: Boolean,
    onMoveMouseDelta: (Offset) -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onDismissRightClickMenu: () -> Unit,
    onUploadExeClick: () -> Unit,
    onToggleFullScreen: () -> Unit,
    onToggleKeyboard: () -> Unit,
    onSpeakStatusClick: () -> Unit,
    onHideMouse: () -> Unit,
    modifier: Modifier = Modifier
) {
    var trackpadOffset by remember { mutableStateOf(Offset(24f, 120f)) }
    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Rendered Windows 10 Mouse Pointer Arrow
        Canvas(
            modifier = Modifier
                .offset {
                    IntOffset(
                        mousePosition.x.roundToInt(),
                        mousePosition.y.roundToInt()
                    )
                }
                .size(28.dp)
        ) {
            val cursorPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, size.height * 0.82f)
                lineTo(size.width * 0.28f, size.height * 0.62f)
                lineTo(size.width * 0.46f, size.height * 0.95f)
                lineTo(size.width * 0.60f, size.height * 0.88f)
                lineTo(size.width * 0.42f, size.height * 0.55f)
                lineTo(size.width * 0.72f, size.height * 0.55f)
                close()
            }
            drawPath(path = cursorPath, color = Color.White)
            drawPath(
                path = cursorPath,
                color = Color(0xFF0078D7),
                style = Stroke(width = 2.dp.toPx())
            )
            // Precision cyan tip glow
            drawCircle(
                color = Color(0xFF00D2FF),
                radius = 3.dp.toPx(),
                center = Offset(0f, 0f)
            )
        }

        // 2. Windows 10 Right-Click Context Menu at Cursor Position
        if (isRightClickMenuVisible) {
            Surface(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (mousePosition.x + 18f).roundToInt(),
                            (mousePosition.y + 18f).roundToInt()
                        )
                    }
                    .width(230.dp)
                    .border(1.dp, Color(0xFF00D2FF), RoundedCornerShape(8.dp))
                    .testTag("win10_right_click_menu"),
                color = Color(0xF20D1829),
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 12.dp
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    ContextMenuItem(
                        icon = Icons.Default.UploadFile,
                        label = "Upload & Run Win10 .exe",
                        onClick = {
                            onDismissRightClickMenu()
                            onUploadExeClick()
                        }
                    )
                    ContextMenuItem(
                        icon = Icons.Default.Fullscreen,
                        label = "Toggle Full Screen (F11)",
                        onClick = {
                            onDismissRightClickMenu()
                            onToggleFullScreen()
                        }
                    )
                    ContextMenuItem(
                        icon = Icons.Default.Keyboard,
                        label = "Show/Hide PC Keyboard",
                        onClick = {
                            onDismissRightClickMenu()
                            onToggleKeyboard()
                        }
                    )
                    ContextMenuItem(
                        icon = Icons.Default.VolumeUp,
                        label = "Speak System Status (Voice)",
                        onClick = {
                            onDismissRightClickMenu()
                            onSpeakStatusClick()
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color(0xFF223654)
                    )
                    ContextMenuItem(
                        icon = Icons.Default.Refresh,
                        label = "Refresh Win10 Desktop",
                        onClick = onDismissRightClickMenu
                    )
                }
            }
        }

        // 3. Floating Draggable Virtual Mouse Trackpad Widget (Bottom-End Default)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 64.dp)
                .offset {
                    IntOffset(
                        trackpadOffset.x.roundToInt() - 24,
                        trackpadOffset.y.roundToInt() - 120
                    )
                }
                .width(184.dp)
                .border(1.dp, Color(0xFF00D2FF).copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                .testTag("virtual_mouse_trackpad"),
            color = Color(0xDD0A1424),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 10.dp
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Header bar (draggable to reposition the trackpad)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                trackpadOffset += dragAmount
                            }
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mouse,
                            contentDescription = "Mouse Trackpad",
                            tint = Color(0xFF00D2FF),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PC Mouse Pad",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(
                        onClick = onHideMouse,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hide Mouse Trackpad",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Touchpad surface area: drag moves cursor, tap triggers left-click
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF11213A))
                        .border(1.dp, Color(0xFF233B61), RoundedCornerShape(8.dp))
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                onMoveMouseDelta(dragAmount * 1.7f)
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onLeftClick() },
                                onLongPress = { onRightClick() }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Slide Finger to Move Cursor",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "X: ${mousePosition.x.toInt()}  Y: ${mousePosition.y.toInt()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF00D2FF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Left & Right Mouse Click Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0078D7))
                            .clickable { onLeftClick() }
                            .testTag("mouse_left_click_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "L-Click",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E3250))
                            .border(1.dp, Color(0xFF00D2FF), RoundedCornerShape(6.dp))
                            .clickable { onRightClick() }
                            .testTag("mouse_right_click_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "R-Click",
                            color = Color(0xFF00D2FF),
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
private fun ContextMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF00D2FF),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}
