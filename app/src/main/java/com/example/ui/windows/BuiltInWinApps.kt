package com.example.ui.windows

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WinSoftwareEntity
import com.example.ui.ActiveWinWindow
import com.example.ui.PrebuiltWin10Package
import com.example.util.PhoneHardwareSpecs

/**
 * 1. Windows 10 Software Uploader & Installer Hub (`win_installer.exe`)
 */
@Composable
fun WinSoftwareInstallerWindow(
    prebuiltPackages: List<PrebuiltWin10Package>,
    installedSoftware: List<WinSoftwareEntity>,
    hardwareSpecs: PhoneHardwareSpecs,
    onPickFileFromPhone: () -> Unit,
    onInstallPrebuilt: (PrebuiltWin10Package) -> Unit,
    onLaunchSoftware: (WinSoftwareEntity) -> Unit,
    onDeleteSoftware: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1626))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                color = Color(0xFF14243D),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color(0xFF00D2FF), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Upload Any Windows 10 Software (.exe / .msi / .bat / .zip)",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Phone se koi bhi Windows 10 software ya app upload karein — yah aapke ${hardwareSpecs.deviceModel} (${hardwareSpecs.availableRamMb} MB free RAM, ${hardwareSpecs.cpuCores} CPU cores) ke hisab se Full Screen mein run karega aur aapse Voice mein baat karega!",
                        fontSize = 13.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onPickFileFromPhone,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D7)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("installer_upload_exe_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = "Upload Software File"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select & Upload Win10 Software from Phone",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // User Uploaded Software List
        val userUploaded = installedSoftware.filter { it.isUserUploaded }
        if (userUploaded.isNotEmpty()) {
            item {
                Text(
                    text = "Your Uploaded & Installed Windows 10 Software (${userUploaded.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF10B981)
                )
            }
            items(userUploaded, key = { it.id }) { sw ->
                Surface(
                    color = Color(0xFF152238),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = sw.name,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${sw.exeFileName} • ${sw.architecture} • Needs ${sw.requiredRamMb} MB RAM",
                                fontSize = 12.sp,
                                color = Color(0xFF00D2FF)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { onLaunchSoftware(sw) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Run",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Run Full Screen", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { onDeleteSoftware(sw.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Uninstall",
                                    tint = Color(0xFFEF4444)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1-Tap Ready-to-Run Windows 10 x64 Software Catalog
        item {
            Text(
                text = "Instant Windows 10 x64 Software Library (1-Tap Load & Run):",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF00D2FF)
            )
        }

        items(prebuiltPackages) { pkg ->
            Surface(
                color = Color(0xFF132036),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF23395B), RoundedCornerShape(10.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pkg.title,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${pkg.exeName} (${pkg.sizeMb} MB) • ${pkg.descriptionHindi}",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onInstallPrebuilt(pkg) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D7)),
                        modifier = Modifier.testTag("load_pkg_${pkg.exeName}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Load & Run",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Load & Run", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * 2. Phone Permissions & Hardware Compatibility Center (`perm_mgr.exe`)
 */
@Composable
fun PermissionsAndHardwareWindow(
    hardwareSpecs: PhoneHardwareSpecs,
    hasAudioPermission: Boolean,
    hasNotificationPermission: Boolean,
    isBackgroundRunning: Boolean,
    backgroundUptimeSec: Long,
    onGrantAllPermissions: () -> Unit,
    onToggleBackgroundService: () -> Unit,
    onSpeakHardwareReport: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C1524))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                color = Color(0xFF15253F),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Phone All-Permissions & Background Engine",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = "Grant all phone permissions so Windows 10 apps talk in voice and run continuously in the background.",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Permissions",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onGrantAllPermissions,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("grant_all_permissions_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Grant All Permissions",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Grant All Phone Permissions (Saari Permission De Dein)",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    PermissionStatusRow(
                        icon = Icons.Default.Mic,
                        title = "Microphone & Voice Talk Permission (RECORD_AUDIO)",
                        isGranted = hasAudioPermission
                    )
                    PermissionStatusRow(
                        icon = Icons.Default.NotificationsActive,
                        title = "Background Notification Permission (POST_NOTIFICATIONS)",
                        isGranted = hasNotificationPermission
                    )
                    PermissionStatusRow(
                        icon = Icons.Default.Storage,
                        title = "Windows 10 .EXE / .MSI Document Loader (SAF Storage)",
                        isGranted = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D182A))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Keep Running in Phone Background",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isBackgroundRunning) {
                                    "Active • Uptime: ${backgroundUptimeSec}s (WakeLock + Foreground Service)"
                                } else {
                                    "Paused • Tap switch to keep Win10 alive in background"
                                },
                                fontSize = 12.sp,
                                color = Color(0xFF00D2FF)
                            )
                        }
                        Switch(
                            checked = isBackgroundRunning,
                            onCheckedChange = { onToggleBackgroundService() },
                            modifier = Modifier.testTag("background_service_switch")
                        )
                    }
                }
            }
        }

        // Phone Hardware Capability Report ("Jo hamare phone ke hisab se chal sakte hain")
        item {
            Surface(
                color = Color(0xFF132036),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your Phone Hardware & Win10 Capability",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF00D2FF)
                        )
                        OutlinedButton(onClick = onSpeakHardwareReport) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Speak Hardware Specs",
                                tint = Color(0xFF00D2FF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Speak Specs", fontSize = 12.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Device: ${hardwareSpecs.deviceModel} • ${hardwareSpecs.androidVersion}",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Capability Tier: ${hardwareSpecs.hardwareTierLabel}",
                        color = Color(0xFF10B981),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    HardwareMetricBar(
                        label = "Physical RAM (${hardwareSpecs.availableRamMb} MB Free / ${hardwareSpecs.totalRamMb} MB Total)",
                        progress = hardwareSpecs.usedRamPercent / 100f
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HardwareMetricBar(
                        label = "CPU Cores: ${hardwareSpecs.cpuCores} Cores (${hardwareSpecs.primaryAbi}) -> x64 JIT Active",
                        progress = 0.35f
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HardwareMetricBar(
                        label = "Internal Storage (${"%.1f".format(hardwareSpecs.freeStorageGb)} GB Free / ${"%.1f".format(hardwareSpecs.totalStorageGb)} GB)",
                        progress = ((hardwareSpecs.totalStorageGb - hardwareSpecs.freeStorageGb) / hardwareSpecs.totalStorageGb.coerceAtLeast(1f)).coerceIn(0.05f, 0.95f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = "Battery",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Battery: ${hardwareSpecs.batteryPercent}% • Thermal: ${hardwareSpecs.batteryTempCelsius}°C • Max Allocatable Win10 RAM: ${hardwareSpecs.maxSupportedWinRamMb} MB",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionStatusRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isGranted: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isGranted) Color(0xFF10B981) else Color(0xFFF59E0B),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontSize = 12.sp, color = Color.White)
        }
        Text(
            text = if (isGranted) "GRANTED" else "TAP ABOVE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isGranted) Color(0xFF10B981) else Color(0xFFF59E0B)
        )
    }
}

@Composable
private fun HardwareMetricBar(label: String, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF00D2FF),
            trackColor = Color(0xFF1E3250)
        )
    }
}

/**
 * 3. Windows 10 Command Prompt (`cmd.exe`)
 */
@Composable
fun WinCmdWindow(
    window: ActiveWinWindow,
    onUpdateBuffer: (String) -> Unit,
    onExecuteEnter: () -> Unit,
    onRunQuickCmd: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05080E))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("systeminfo", "dir", "tasklist", "ver", "help", "cls").forEach { cmd ->
                Surface(
                    color = Color(0xFF16243A),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { onRunQuickCmd(cmd) }
                ) {
                    Text(
                        text = cmd,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF00D2FF),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = window.textBuffer,
            onValueChange = onUpdateBuffer,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("cmd_terminal_input"),
            textStyle = MaterialTheme.typography.labelMedium.copy(
                color = Color(0xFF10B981),
                fontSize = 13.sp,
                lineHeight = 19.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1E3A5F),
                unfocusedBorderColor = Color(0xFF132238),
                focusedContainerColor = Color(0xFF03060C),
                unfocusedContainerColor = Color(0xFF03060C)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onExecuteEnter,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D7)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Execute Command (Enter ↵)")
        }
    }
}

/**
 * 4. Windows 10 Notepad (`notepad.exe`) with Voice Read-Aloud
 */
@Composable
fun WinNotepadWindow(
    window: ActiveWinWindow,
    onUpdateBuffer: (String) -> Unit,
    onReadAloud: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1726))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "C:\\Users\\Administrator\\Documents\\notes.txt",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF94A3B8)
            )
            Button(
                onClick = { onReadAloud(window.textBuffer) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Read Aloud in Voice",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Voice Mein Padhein", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = window.textBuffer,
            onValueChange = onUpdateBuffer,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("notepad_editor_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00D2FF),
                unfocusedBorderColor = Color(0xFF23395B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

/**
 * 5. Windows 10 Paint Studio (`mspaint.exe`)
 */
private data class PaintStroke(
    val start: Offset,
    val end: Offset,
    val color: Color,
    val width: Float
)

@Composable
fun WinPaintWindow() {
    val strokes = remember { mutableStateListOf<PaintStroke>() }
    var selectedColor by remember { mutableStateOf(Color(0xFF00D2FF)) }
    var strokeWidth by remember { mutableFloatStateOf(8f) }

    val palette = listOf(
        Color(0xFF00D2FF),
        Color(0xFF10B981),
        Color(0xFFF59E0B),
        Color(0xFFEF4444),
        Color(0xFFA855F7),
        Color.White
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C1524))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                palette.forEach { c ->
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(
                                width = if (selectedColor == c) 3.dp else 1.dp,
                                color = if (selectedColor == c) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = c }
                    )
                }
            }
            OutlinedButton(onClick = { strokes.clear() }) {
                Text("Clear Canvas", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF070E1A))
                .border(1.dp, Color(0xFF23395B), RoundedCornerShape(10.dp))
                .pointerInput(selectedColor, strokeWidth) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        strokes.add(
                            PaintStroke(
                                start = change.position - dragAmount,
                                end = change.position,
                                color = selectedColor,
                                width = strokeWidth
                            )
                        )
                    }
                }
        ) {
            strokes.forEach { s ->
                drawLine(
                    color = s.color,
                    start = s.start,
                    end = s.end,
                    strokeWidth = s.width,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * 6. Windows 10 Task Manager (`taskmgr.exe`) & Explorer (`explorer.exe`) & Calculator (`calc.exe`)
 */
@Composable
fun WinTaskManagerWindow(
    openWindows: List<ActiveWinWindow>,
    hardwareSpecs: PhoneHardwareSpecs,
    isBackgroundRunning: Boolean,
    backgroundUptimeSec: Long,
    onCloseProcess: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1422))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Windows 10 Task Manager • Host: ${hardwareSpecs.deviceModel}",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF00D2FF)
            )
            Text(
                text = "CPU: ${hardwareSpecs.cpuCores} Logical Processors | RAM: ${hardwareSpecs.availableRamMb} MB Free / ${hardwareSpecs.totalRamMb} MB | Background Service: ${if (isBackgroundRunning) "ACTIVE (${backgroundUptimeSec}s)" else "IDLE"}",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        items(openWindows, key = { it.instanceId }) { win ->
            Surface(
                color = Color(0xFF142238),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${win.software.name} (${win.software.exeFileName})",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "CPU: ${win.cpuUsagePercent}% • RAM: ${win.allocatedRamMb} MB • Arch: ${win.software.architecture}",
                            fontSize = 12.sp,
                            color = Color(0xFF10B981)
                        )
                    }
                    OutlinedButton(onClick = { onCloseProcess(win.instanceId) }) {
                        Text("End Task", color = Color(0xFFEF4444), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun WinExplorerWindow(
    allSoftware: List<WinSoftwareEntity>,
    onLaunchSoftware: (WinSoftwareEntity) -> Unit,
    onUploadExe: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1626))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "This PC > Local Disk (C:) > Program Files",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF00D2FF)
                )
                Button(
                    onClick = onUploadExe,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D7))
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = "Upload .exe", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload .EXE", fontSize = 12.sp)
                }
            }
        }

        items(allSoftware, key = { it.id }) { sw ->
            Surface(
                color = Color(0xFF142238),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLaunchSoftware(sw) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(sw.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "${sw.installPath} • ${sw.architecture}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                    Text("Run", color = Color(0xFF00D2FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WinCalculatorWindow(onSpeakResult: (String) -> Unit) {
    var display by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableStateOf<Double?>(null) }
    var pendingOp by remember { mutableStateOf<String?>(null) }

    val buttons = listOf(
        "7", "8", "9", "÷",
        "4", "5", "6", "×",
        "1", "2", "3", "-",
        "C", "0", "=", "+"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1626))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = Color(0xFF060C17),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = display,
                style = MaterialTheme.typography.displayLarge,
                color = Color(0xFF00D2FF),
                modifier = Modifier.padding(18.dp)
            )
        }

        FlowRow(
            maxItemsInEachRow = 4,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            buttons.forEach { btn ->
                Button(
                    onClick = {
                        when (btn) {
                            "C" -> {
                                display = "0"
                                firstOperand = null
                                pendingOp = null
                            }
                            "+", "-", "×", "÷" -> {
                                firstOperand = display.toDoubleOrNull() ?: 0.0
                                pendingOp = btn
                                display = "0"
                            }
                            "=" -> {
                                val a = firstOperand ?: 0.0
                                val b = display.toDoubleOrNull() ?: 0.0
                                val res = when (pendingOp) {
                                    "+" -> a + b
                                    "-" -> a - b
                                    "×" -> a * b
                                    "÷" -> if (b != 0.0) a / b else 0.0
                                    else -> b
                                }
                                val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else "%.2f".format(res)
                                display = formatted
                                onSpeakResult("Calculator answer is $formatted")
                                pendingOp = null
                            }
                            else -> {
                                display = if (display == "0") btn else display + btn
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (btn in listOf("+", "-", "×", "÷", "=")) Color(0xFF0078D7) else Color(0xFF182842)
                    )
                ) {
                    Text(btn, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
