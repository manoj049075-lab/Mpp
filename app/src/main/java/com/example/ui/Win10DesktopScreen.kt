package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WinSoftwareEntity
import com.example.ui.components.VirtualMouseOverlay
import com.example.ui.components.VirtualPcKeyboard
import com.example.ui.windows.CustomExeRunnerWindow
import com.example.ui.windows.PermissionsAndHardwareWindow
import com.example.ui.windows.WinCalculatorWindow
import com.example.ui.windows.WinCmdWindow
import com.example.ui.windows.WinExplorerWindow
import com.example.ui.windows.WinNotepadWindow
import com.example.ui.windows.WinPaintWindow
import com.example.ui.windows.WinSoftwareInstallerWindow
import com.example.ui.windows.WinTaskManagerWindow
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Win10DesktopScreen(
    viewModel: WinBoxViewModel,
    onUpdateFullScreenSystemBars: (Boolean) -> Unit
) {
    val allSoftware by viewModel.allSoftware.collectAsStateWithLifecycle()
    val openWindows by viewModel.openWindows.collectAsStateWithLifecycle()
    val focusedWindowId by viewModel.focusedWindowId.collectAsStateWithLifecycle()
    val hardwareSpecs by viewModel.hardwareSpecs.collectAsStateWithLifecycle()
    val isImmersiveFullScreen by viewModel.isImmersiveFullScreen.collectAsStateWithLifecycle()
    val isKeyboardVisible by viewModel.isKeyboardVisible.collectAsStateWithLifecycle()
    val isMouseEnabled by viewModel.isMouseEnabled.collectAsStateWithLifecycle()
    val mousePosition by viewModel.mousePosition.collectAsStateWithLifecycle()
    val isRightClickMenuVisible by viewModel.isRightClickMenuVisible.collectAsStateWithLifecycle()
    val isStartMenuOpen by viewModel.isStartMenuOpen.collectAsStateWithLifecycle()
    val hasAudioPermission by viewModel.hasAudioPermission.collectAsStateWithLifecycle()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsStateWithLifecycle()
    val isBackgroundRunning by viewModel.isBackgroundServiceRunning.collectAsStateWithLifecycle()
    val backgroundUptimeSec by viewModel.backgroundUptimeSeconds.collectAsStateWithLifecycle()
    val statusBannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    val isVoiceEnabled by viewModel.voiceEngine.voiceEnabled.collectAsStateWithLifecycle()
    val preferHindiVoice by viewModel.voiceEngine.preferHindiVoice.collectAsStateWithLifecycle()
    val isListeningVoice by viewModel.voiceEngine.isListening.collectAsStateWithLifecycle()
    val lastSpokenMessage by viewModel.voiceEngine.lastSpokenMessage.collectAsStateWithLifecycle()

    LaunchedEffect(isImmersiveFullScreen) {
        onUpdateFullScreenSystemBars(isImmersiveFullScreen)
    }

    // Document Picker for uploading ANY Windows 10 Software (.exe, .msi, .bat, .cmd, .zip, etc.)
    val uploadExeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.uploadAndRunSoftwareFromUri(uri)
        }
    }

    // Multi-permission launcher ("phone ke Jo saari permission De dega")
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.onPermissionsResult()
    }

    val requestAllPhonePermissions = {
        val perms = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionsLauncher.launch(perms.toTypedArray())
    }

    val activeWindow = openWindows.find { it.instanceId == focusedWindowId && !it.isMinimized }
        ?: openWindows.lastOrNull { !it.isMinimized }

    BackHandler(enabled = isStartMenuOpen || isKeyboardVisible || activeWindow != null) {
        when {
            isStartMenuOpen -> viewModel.toggleStartMenu()
            isKeyboardVisible -> viewModel.toggleKeyboardVisibility()
            activeWindow != null -> viewModel.closeWindow(activeWindow.instanceId)
        }
    }

    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF06101E))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxHeightPx = with(density) { maxHeight.toPx() }

        // 1. Custom Windows 10 Fluent Perspective Wallpaper Canvas
        Win10FluentWallpaperCanvas(modifier = Modifier.fillMaxSize())

        // 2. Main Desktop & Full-Screen Window Column
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Control Deck (Quick Upload .EXE, Full Screen, Keyboard, Mouse, Voice, Permissions)
            Surface(
                color = Color(0xE6091426),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, Color(0xFF1F3658))
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Primary CTA: Upload Any Windows 10 Software (.exe / .msi / .bat)
                        Button(
                            onClick = { uploadExeLauncher.launch(arrayOf("*/*")) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D7)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("upload_exe_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = "Upload Win10 Software",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Upload Win10 .EXE / App",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Hardware & Quick Controls Strip
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            QuickTopChip(
                                icon = if (isImmersiveFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                label = "Full Screen",
                                isActive = isImmersiveFullScreen,
                                testTag = "fullscreen_toggle_button",
                                onClick = { viewModel.toggleImmersiveFullScreen() }
                            )
                            QuickTopChip(
                                icon = Icons.Default.Keyboard,
                                label = "Keyboard",
                                isActive = isKeyboardVisible,
                                testTag = "keyboard_toggle_button",
                                onClick = { viewModel.toggleKeyboardVisibility() }
                            )
                            QuickTopChip(
                                icon = Icons.Default.Mouse,
                                label = "Mouse",
                                isActive = isMouseEnabled,
                                testTag = "mouse_toggle_button",
                                onClick = { viewModel.toggleMouseEnabled() }
                            )
                            QuickTopChip(
                                icon = Icons.Default.Security,
                                label = if (hasAudioPermission && hasNotificationPermission) "Perms OK" else "All Perms",
                                isActive = hasAudioPermission && hasNotificationPermission && isBackgroundRunning,
                                testTag = "permissions_button",
                                onClick = {
                                    requestAllPhonePermissions()
                                    allSoftware.find { it.appType == "PERMISSIONS" }?.let {
                                        viewModel.launchSoftwareWindow(it, forceFullScreen = true)
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Live Voice Talk & Phone Hardware Telemetry Ticker
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0E1D34))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "Toggle Voice Talk",
                                tint = if (isVoiceEnabled) Color(0xFF10B981) else Color(0xFF94A3B8),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { viewModel.voiceEngine.toggleVoiceEnabled() }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🔊 $lastSpokenMessage",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFF193254),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.clickable {
                                    viewModel.voiceEngine.toggleLanguagePreference()
                                }
                            ) {
                                Text(
                                    text = if (preferHindiVoice) "HI/EN Voice" else "EN Voice",
                                    fontSize = 10.sp,
                                    color = Color(0xFF00D2FF),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${hardwareSpecs.availableRamMb}MB Free • ${hardwareSpecs.cpuCores} Cores",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF10B981),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // 3. Center Workspace: Desktop Icons + Active Full-Screen Windows 10 Software Window
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Desktop Icon Grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 96.dp),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("win10_desktop_grid")
                ) {
                    items(allSoftware, key = { it.id }) { software ->
                        DesktopShortcutTile(
                            software = software,
                            onClick = {
                                viewModel.launchSoftwareWindow(software, forceFullScreen = true)
                            }
                        )
                    }
                }

                // Active Windows 10 Software Window (Full Screen by Default as Requested!)
                if (activeWindow != null) {
                    val windowModifier = if (activeWindow.isMaximized) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    }

                    Surface(
                        modifier = windowModifier
                            .border(
                                width = if (activeWindow.isMaximized) 0.dp else 1.5.dp,
                                color = Color(0xFF00D2FF),
                                shape = RoundedCornerShape(if (activeWindow.isMaximized) 0.dp else 10.dp)
                            )
                            .testTag("active_win10_window"),
                        color = Color(0xFF0E1726),
                        shape = RoundedCornerShape(if (activeWindow.isMaximized) 0.dp else 10.dp),
                        tonalElevation = 16.dp
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Authentic Windows 10 Window Title Bar
                            Win10WindowTitleBar(
                                window = activeWindow,
                                onSpeakWindowStatus = {
                                    viewModel.talkToActiveSoftware(
                                        activeWindow.instanceId,
                                        "Apna status aur kaam voice mein batao"
                                    )
                                },
                                onMinimize = { viewModel.minimizeWindow(activeWindow.instanceId) },
                                onToggleMaximize = { viewModel.toggleMaximizeWindow(activeWindow.instanceId) },
                                onClose = { viewModel.closeWindow(activeWindow.instanceId) }
                            )

                            // Window Content Body
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                when (activeWindow.software.appType) {
                                    "INSTALLER" -> WinSoftwareInstallerWindow(
                                        prebuiltPackages = viewModel.downloadableWin10Packages,
                                        installedSoftware = allSoftware,
                                        hardwareSpecs = hardwareSpecs,
                                        onPickFileFromPhone = {
                                            uploadExeLauncher.launch(arrayOf("*/*"))
                                        },
                                        onInstallPrebuilt = { pkg ->
                                            viewModel.installAndRunPrebuiltWin10Package(pkg)
                                        },
                                        onLaunchSoftware = { sw ->
                                            viewModel.launchSoftwareWindow(sw, forceFullScreen = true)
                                        },
                                        onDeleteSoftware = { id ->
                                            viewModel.deleteUploadedSoftware(id)
                                        }
                                    )
                                    "PERMISSIONS" -> PermissionsAndHardwareWindow(
                                        hardwareSpecs = hardwareSpecs,
                                        hasAudioPermission = hasAudioPermission,
                                        hasNotificationPermission = hasNotificationPermission,
                                        isBackgroundRunning = isBackgroundRunning,
                                        backgroundUptimeSec = backgroundUptimeSec,
                                        onGrantAllPermissions = requestAllPhonePermissions,
                                        onToggleBackgroundService = { viewModel.toggleBackgroundService() },
                                        onSpeakHardwareReport = {
                                            viewModel.voiceEngine.speak(
                                                hindiText = "Aapka phone ${hardwareSpecs.deviceModel} hai. Ismein ${hardwareSpecs.totalRamMb} MB total RAM, ${hardwareSpecs.availableRamMb} MB free RAM, aur ${hardwareSpecs.cpuCores} CPU cores hain. Sabhi compatible Windows 10 software full screen mein chal sakte hain.",
                                                englishText = "Phone ${hardwareSpecs.deviceModel} has ${hardwareSpecs.availableRamMb} MB free RAM and ${hardwareSpecs.cpuCores} CPU cores."
                                            )
                                        }
                                    )
                                    "CMD" -> WinCmdWindow(
                                        window = activeWindow,
                                        onUpdateBuffer = {
                                            viewModel.updateWindowTextBuffer(activeWindow.instanceId, it)
                                        },
                                        onExecuteEnter = { viewModel.sendKeyToActiveWindow("ENTER") },
                                        onRunQuickCmd = { cmd ->
                                            val base = activeWindow.textBuffer.substringBeforeLast("C:\\Users\\Administrator>")
                                            viewModel.updateWindowTextBuffer(
                                                activeWindow.instanceId,
                                                "${base}C:\\Users\\Administrator>$cmd"
                                            )
                                            viewModel.sendKeyToActiveWindow("ENTER")
                                        }
                                    )
                                    "NOTEPAD" -> WinNotepadWindow(
                                        window = activeWindow,
                                        onUpdateBuffer = {
                                            viewModel.updateWindowTextBuffer(activeWindow.instanceId, it)
                                        },
                                        onReadAloud = { text ->
                                            viewModel.voiceEngine.speak(text, text)
                                        }
                                    )
                                    "PAINT" -> WinPaintWindow()
                                    "TASKMGR" -> WinTaskManagerWindow(
                                        openWindows = openWindows,
                                        hardwareSpecs = hardwareSpecs,
                                        isBackgroundRunning = isBackgroundRunning,
                                        backgroundUptimeSec = backgroundUptimeSec,
                                        onCloseProcess = { viewModel.closeWindow(it) }
                                    )
                                    "EXPLORER" -> WinExplorerWindow(
                                        allSoftware = allSoftware,
                                        onLaunchSoftware = {
                                            viewModel.launchSoftwareWindow(it, forceFullScreen = true)
                                        },
                                        onUploadExe = {
                                            uploadExeLauncher.launch(arrayOf("*/*"))
                                        }
                                    )
                                    "CALC" -> WinCalculatorWindow(
                                        onSpeakResult = { msg ->
                                            viewModel.voiceEngine.speak(msg, msg)
                                        }
                                    )
                                    else -> CustomExeRunnerWindow(
                                        window = activeWindow,
                                        hardwareSpecs = hardwareSpecs,
                                        isListeningVoice = isListeningVoice,
                                        onTalkToSoftware = { msg ->
                                            viewModel.talkToActiveSoftware(activeWindow.instanceId, msg)
                                        },
                                        onStartVoiceMic = {
                                            if (!hasAudioPermission) {
                                                requestAllPhonePermissions()
                                            }
                                            viewModel.voiceEngine.startListening()
                                        },
                                        onExecuteAction = { act ->
                                            viewModel.executeSoftwareAction(activeWindow.instanceId, act)
                                        },
                                        onUpdateTextBuffer = {
                                            viewModel.updateWindowTextBuffer(activeWindow.instanceId, it)
                                        },
                                        onToggleKeyboard = { viewModel.toggleKeyboardVisibility() },
                                        onToggleMouse = { viewModel.toggleMouseEnabled() },
                                        onUploadAnotherExe = {
                                            uploadExeLauncher.launch(arrayOf("*/*"))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Windows 10 Start Menu Overlay
                androidx.compose.animation.AnimatedVisibility(
                    visible = isStartMenuOpen,
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Win10StartMenu(
                        allSoftware = allSoftware,
                        hardwareSpecs = hardwareSpecs,
                        isBackgroundRunning = isBackgroundRunning,
                        onLaunchSoftware = {
                            viewModel.launchSoftwareWindow(it, forceFullScreen = true)
                        },
                        onUploadExe = {
                            viewModel.toggleStartMenu()
                            uploadExeLauncher.launch(arrayOf("*/*"))
                        },
                        onGrantAllPermissions = {
                            viewModel.toggleStartMenu()
                            requestAllPhonePermissions()
                        }
                    )
                }
            }

            // 4. Docked Windows 10 Virtual PC Keyboard (when toggled on)
            AnimatedVisibility(visible = isKeyboardVisible) {
                VirtualPcKeyboard(
                    onKeyPress = { key -> viewModel.sendKeyToActiveWindow(key) },
                    onCloseKeyboard = { viewModel.toggleKeyboardVisibility() }
                )
            }

            // 5. Authentic Windows 10 Taskbar at the Bottom
            Win10Taskbar(
                openWindows = openWindows,
                focusedWindowId = focusedWindowId,
                isStartMenuOpen = isStartMenuOpen,
                isKeyboardVisible = isKeyboardVisible,
                isMouseEnabled = isMouseEnabled,
                isListeningVoice = isListeningVoice,
                isBackgroundRunning = isBackgroundRunning,
                onToggleStartMenu = { viewModel.toggleStartMenu() },
                onStartVoiceMic = {
                    if (!hasAudioPermission) {
                        requestAllPhonePermissions()
                    }
                    viewModel.voiceEngine.startListening()
                },
                onWindowPillClick = { instanceId -> viewModel.focusWindow(instanceId) },
                onToggleKeyboard = { viewModel.toggleKeyboardVisibility() },
                onToggleMouse = { viewModel.toggleMouseEnabled() }
            )
        }

        // 6. Virtual Mouse Cursor & Trackpad Overlay on Top of Everything
        if (isMouseEnabled) {
            VirtualMouseOverlay(
                mousePosition = mousePosition,
                isRightClickMenuVisible = isRightClickMenuVisible,
                onMoveMouseDelta = { delta ->
                    viewModel.moveMouseBy(delta, maxWidthPx, maxHeightPx)
                },
                onLeftClick = { viewModel.triggerMouseLeftClick() },
                onRightClick = { viewModel.triggerMouseRightClick() },
                onDismissRightClickMenu = { viewModel.dismissRightClickMenu() },
                onUploadExeClick = { uploadExeLauncher.launch(arrayOf("*/*")) },
                onToggleFullScreen = { viewModel.toggleImmersiveFullScreen() },
                onToggleKeyboard = { viewModel.toggleKeyboardVisibility() },
                onSpeakStatusClick = {
                    viewModel.voiceEngine.speak(
                        hindiText = statusBannerMessage,
                        englishText = statusBannerMessage
                    )
                },
                onHideMouse = { viewModel.toggleMouseEnabled() }
            )
        }
    }
}

@Composable
private fun QuickTopChip(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        color = if (isActive) Color(0xFF0078D7) else Color(0xFF172740),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .height(34.dp)
            .border(
                1.dp,
                if (isActive) Color(0xFF00D2FF) else Color(0xFF284166),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else Color(0xFF00D2FF),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DesktopShortcutTile(
    software: WinSoftwareEntity,
    onClick: () -> Unit
) {
    val icon = remember(software.appType) { getSoftwareIcon(software.appType) }
    val iconTint = remember(software.appType, software.isUserUploaded) {
        when {
            software.isUserUploaded -> Color(0xFF10B981)
            software.appType == "INSTALLER" -> Color(0xFF00D2FF)
            software.appType == "PERMISSIONS" -> Color(0xFFF59E0B)
            else -> Color(0xFF38BDF8)
        }
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x550A1628))
            .border(
                1.dp,
                if (software.isUserUploaded) Color(0xFF10B981).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f),
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .testTag("desktop_icon_${software.exeFileName}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF132540)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = software.name,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = software.name,
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = software.exeFileName,
            fontSize = 9.sp,
            color = Color(0xFF94A3B8),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Win10WindowTitleBar(
    window: ActiveWinWindow,
    onSpeakWindowStatus: () -> Unit,
    onMinimize: () -> Unit,
    onToggleMaximize: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFF081120))
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = getSoftwareIcon(window.software.appType),
                contentDescription = null,
                tint = Color(0xFF00D2FF),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${window.software.name} — ${window.software.exeFileName} (Full Screen Win10)",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onSpeakWindowStatus,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speak Software Status",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(16.dp)
                )
            }
            IconButton(
                onClick = onMinimize,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Minimize,
                    contentDescription = "Minimize Window",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            IconButton(
                onClick = onToggleMaximize,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CropSquare,
                    contentDescription = "Maximize / Full Screen Window",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("close_window_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Window",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun Win10StartMenu(
    allSoftware: List<WinSoftwareEntity>,
    hardwareSpecs: com.example.util.PhoneHardwareSpecs,
    isBackgroundRunning: Boolean,
    onLaunchSoftware: (WinSoftwareEntity) -> Unit,
    onUploadExe: () -> Unit,
    onGrantAllPermissions: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(320.dp)
            .height(430.dp)
            .padding(start = 6.dp, bottom = 4.dp)
            .border(1.dp, Color(0xFF00D2FF), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .testTag("win10_start_menu"),
        color = Color(0xF50B1526),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        tonalElevation = 16.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Windows 10 Pro (WinBox)",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${hardwareSpecs.deviceModel} • BG Engine: ${if (isBackgroundRunning) "ON" else "OFF"}",
                        fontSize = 11.sp,
                        color = Color(0xFF00D2FF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onUploadExe,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D7)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Upload .EXE", fontSize = 11.sp)
                }
                Button(
                    onClick = onGrantAllPermissions,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("All Permissions", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color(0xFF1E3250)
            )

            androidx.compose.foundation.lazy.LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(allSoftware.size) { index ->
                    val sw = allSoftware[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onLaunchSoftware(sw) }
                            .padding(horizontal = 8.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getSoftwareIcon(sw.appType),
                            contentDescription = sw.name,
                            tint = Color(0xFF00D2FF),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sw.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(sw.exeFileName, color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Win10Taskbar(
    openWindows: List<ActiveWinWindow>,
    focusedWindowId: Long?,
    isStartMenuOpen: Boolean,
    isKeyboardVisible: Boolean,
    isMouseEnabled: Boolean,
    isListeningVoice: Boolean,
    isBackgroundRunning: Boolean,
    onToggleStartMenu: () -> Unit,
    onStartVoiceMic: () -> Unit,
    onWindowPillClick: (Long) -> Unit,
    onToggleKeyboard: () -> Unit,
    onToggleMouse: () -> Unit
) {
    var currentTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("hh:mm a\ndd/MM/yyyy", Locale.getDefault())
        while (true) {
            currentTime = sdf.format(Date())
            delay(10_000L)
        }
    }

    Surface(
        color = Color(0xF2070F1C),
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(48.dp)
            .border(0.5.dp, Color(0xFF1E3250))
            .testTag("win10_taskbar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Windows 10 Start Button + Voice Mic + Open Window Taskbar Pills
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Start Button
                IconButton(
                    onClick = onToggleStartMenu,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isStartMenuOpen) Color(0xFF0078D7) else Color(0xFF122138))
                        .testTag("win10_start_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Window,
                        contentDescription = "Windows 10 Start Menu",
                        tint = Color(0xFF00D2FF)
                    )
                }

                // Cortana / WinVoice Talk Mic Button
                IconButton(
                    onClick = onStartVoiceMic,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isListeningVoice) Color(0xFFEF4444) else Color(0xFF122138))
                        .testTag("taskbar_voice_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Command Mic",
                        tint = if (isListeningVoice) Color.White else Color(0xFF10B981)
                    )
                }

                // Running Software Pills
                openWindows.take(3).forEach { win ->
                    val isFocused = win.instanceId == focusedWindowId && !win.isMinimized
                    Surface(
                        color = if (isFocused) Color(0xFF1E3658) else Color(0xFF101C30),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .border(
                                width = 1.dp,
                                color = if (isFocused) Color(0xFF00D2FF) else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { onWindowPillClick(win.instanceId) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = getSoftwareIcon(win.software.appType),
                                contentDescription = null,
                                tint = Color(0xFF00D2FF),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = win.software.name.take(10),
                                fontSize = 11.sp,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Right System Tray: Background Status + Keyboard + Mouse + Clock
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isBackgroundRunning) Color(0xFF10B981) else Color(0xFFF59E0B))
                )
                IconButton(
                    onClick = onToggleKeyboard,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Toggle PC Keyboard",
                        tint = if (isKeyboardVisible) Color(0xFF00D2FF) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onToggleMouse,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mouse,
                        contentDescription = "Toggle Virtual Mouse",
                        tint = if (isMouseEnabled) Color(0xFF10B981) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = currentTime,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    color = Color.White,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun Win10FluentWallpaperCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF040B16),
                    Color(0xFF0A1D3B),
                    Color(0xFF061224)
                ),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            )
        )

        // Glowing cyan radial aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x3300D2FF),
                    Color(0x110078D7),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.72f, size.height * 0.45f),
                radius = size.minDimension * 0.65f
            ),
            center = Offset(size.width * 0.72f, size.height * 0.45f),
            radius = size.minDimension * 0.65f
        )

        // Subtle Windows 10 four-pane perspective watermark on right side
        val cx = size.width * 0.72f
        val cy = size.height * 0.45f
        val w = size.minDimension * 0.22f
        val paneColor = Color(0x1800D2FF)

        val topLeftPane = Path().apply {
            moveTo(cx - w, cy - w * 0.65f)
            lineTo(cx - w * 0.08f, cy - w * 0.82f)
            lineTo(cx - w * 0.08f, cy - w * 0.05f)
            lineTo(cx - w, cy - w * 0.05f)
            close()
        }
        val topRightPane = Path().apply {
            moveTo(cx + w * 0.05f, cy - w * 0.85f)
            lineTo(cx + w, cy - w * 1.02f)
            lineTo(cx + w, cy - w * 0.05f)
            lineTo(cx + w * 0.05f, cy - w * 0.05f)
            close()
        }
        val bottomLeftPane = Path().apply {
            moveTo(cx - w, cy + w * 0.05f)
            lineTo(cx - w * 0.08f, cy + w * 0.05f)
            lineTo(cx - w * 0.08f, cy + w * 0.82f)
            lineTo(cx - w, cy + w * 0.65f)
            close()
        }
        val bottomRightPane = Path().apply {
            moveTo(cx + w * 0.05f, cy + w * 0.05f)
            lineTo(cx + w, cy + w * 0.05f)
            lineTo(cx + w, cy + w * 1.02f)
            lineTo(cx + w * 0.05f, cy + w * 0.85f)
            close()
        }

        drawPath(topLeftPane, paneColor)
        drawPath(topRightPane, paneColor)
        drawPath(bottomLeftPane, paneColor)
        drawPath(bottomRightPane, paneColor)
    }
}

private fun getSoftwareIcon(appType: String): ImageVector {
    return when (appType) {
        "INSTALLER" -> Icons.Default.UploadFile
        "PERMISSIONS" -> Icons.Default.Security
        "EXPLORER" -> Icons.Default.Folder
        "CMD" -> Icons.Default.Terminal
        "NOTEPAD" -> Icons.Default.Description
        "PAINT" -> Icons.Default.Brush
        "TASKMGR" -> Icons.Default.Memory
        "CALC" -> Icons.Default.Calculate
        else -> Icons.Default.Apps
    }
}
