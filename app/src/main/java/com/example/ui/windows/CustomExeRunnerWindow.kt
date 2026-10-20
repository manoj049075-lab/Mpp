package com.example.ui.windows

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Terminal
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.ActiveWinWindow
import com.example.util.PhoneHardwareMonitor
import com.example.util.PhoneHardwareSpecs

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomExeRunnerWindow(
    window: ActiveWinWindow,
    hardwareSpecs: PhoneHardwareSpecs,
    isListeningVoice: Boolean,
    onTalkToSoftware: (String) -> Unit,
    onStartVoiceMic: () -> Unit,
    onExecuteAction: (String) -> Unit,
    onUpdateTextBuffer: (String) -> Unit,
    onToggleKeyboard: () -> Unit,
    onToggleMouse: () -> Unit,
    onUploadAnotherExe: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var voiceInputText by remember { mutableStateOf("") }
    val sw = window.software
    val (isCompatible, compatSummary) = PhoneHardwareMonitor.evaluateSoftwareCompatibility(
        sw.requiredRamMb,
        hardwareSpecs
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1726))
    ) {
        // Windows 10 Classic Application Menu Strip (File | Edit | View | Run | Voice AI | Help)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF162238))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("File", "Execute", "Hardware", "Voice Talk", "Tools").forEach { menu ->
                    Text(
                        text = menu,
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        modifier = Modifier.clickable {
                            onExecuteAction("Menu -> $menu")
                        }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = Color(0xFF1E3250),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { onToggleKeyboard() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "PC Keyboard",
                            tint = Color(0xFF00D2FF),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Keyboard", fontSize = 11.sp, color = Color.White)
                    }
                }
                Surface(
                    color = Color(0xFF1E3250),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { onToggleMouse() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mouse,
                            contentDescription = "Virtual Mouse",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mouse", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }

        // Hardware Compatibility Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF091220))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Hardware Compatible",
                    tint = if (isCompatible) Color(0xFF10B981) else Color(0xFFF59E0B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${sw.architecture} • $compatSummary",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1
                )
            }
            Text(
                text = "CPU: ${window.cpuUsagePercent}% | RAM: ${window.allocatedRamMb} MB",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF00D2FF)
            )
        }

        // Sub-navigation tabs for the running Windows 10 Software
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF111C2D),
            contentColor = Color(0xFF00D2FF)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Live Win10 GUI & Voice", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("x64 Console Logs", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("PE32+ Binary Inspector", fontSize = 12.sp) }
            )
        }

        when (selectedTab) {
            0 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Live Interactive Windows 10 Software Workspace Card
                    item {
                        Surface(
                            color = Color(0xFF16243A),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF00D2FF).copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${sw.name} (${sw.exeFileName})",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color.White
                                        )
                                        Text(
                                            text = sw.installPath,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color(0xFF00D2FF)
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            onTalkToSoftware("Namaste! Apna status aur kaam voice mein batao")
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF0078D7)
                                        ),
                                        modifier = Modifier.testTag("software_voice_speak_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Speak Status",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Voice Talk", fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Interactive Software Action Controls
                                Text(
                                    text = "Interactive Windows 10 Software Controls (Click with Touch or Mouse):",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "Run Main Process",
                                        "Turbo CPU Boost (${hardwareSpecs.cpuCores} Cores)",
                                        "Verify DLL Modules",
                                        "Export Output to C:\\",
                                        "Keep Alive in Background"
                                    ).forEach { action ->
                                        OutlinedButton(
                                            onClick = { onExecuteAction(action) },
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                Color(0xFF00D2FF)
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = action,
                                                tint = Color(0xFF00D2FF),
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(action, fontSize = 12.sp, color = Color.White)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Live Workspace Input connected to Virtual PC Keyboard
                                OutlinedTextField(
                                    value = window.textBuffer,
                                    onValueChange = onUpdateTextBuffer,
                                    label = {
                                        Text("Live Software Workspace / Command Input (Type with PC Keyboard)")
                                    },
                                    placeholder = {
                                        Text("Open PC Keyboard or type here to interact with ${sw.exeFileName}...")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("exe_workspace_input"),
                                    minLines = 2,
                                    maxLines = 4,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF00D2FF),
                                        unfocusedBorderColor = Color(0xFF2D466B),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // 2. Voice Conversation Panel ("to voice mein baat karne lagega")
                    item {
                        Surface(
                            color = Color(0xFF132238),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.RecordVoiceOver,
                                            contentDescription = "Voice Talk",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Talk to ${sw.name} in Voice (Hindi / English)",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White
                                        )
                                    }

                                    Button(
                                        onClick = onStartVoiceMic,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isListeningVoice) Color(0xFFEF4444) else Color(0xFF10B981)
                                        ),
                                        modifier = Modifier.testTag("software_mic_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Speak with Mic",
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isListeningVoice) "Listening..." else "Bol Kar Baat Karein",
                                            color = Color.Black,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick Hindi/Hinglish Voice Talk chips
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        "Phone ke hisab se kshamta batao",
                                        "Full screen mein chalao",
                                        "Keyboard dikhao",
                                        "Mouse chalu karo",
                                        "Background mein kaam karo"
                                    ).forEach { prompt ->
                                        Surface(
                                            color = Color(0xFF1D3252),
                                            shape = CircleShape,
                                            modifier = Modifier.clickable {
                                                onTalkToSoftware(prompt)
                                            }
                                        ) {
                                            Text(
                                                text = "🔊 $prompt",
                                                fontSize = 11.sp,
                                                color = Color(0xFF00D2FF),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Voice messages list
                                window.voiceChatHistory.takeLast(4).forEach { (sender, msg) ->
                                    val isUser = sender == "You"
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isUser) Color(0xFF1E3A5F) else Color(0xFF0B192C)
                                            )
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = if (isUser) "You:" else "🔊 $sender (Speaking in Voice):",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUser) Color(0xFF00D2FF) else Color(0xFF10B981)
                                            )
                                            Text(
                                                text = msg,
                                                fontSize = 13.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = voiceInputText,
                                        onValueChange = { voiceInputText = it },
                                        placeholder = { Text("Ask ${sw.name} anything in Hindi or English...") },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("voice_chat_input"),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF10B981),
                                            unfocusedBorderColor = Color(0xFF2D466B),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            if (voiceInputText.isNotBlank()) {
                                                onTalkToSoftware(voiceInputText)
                                                voiceInputText = ""
                                            }
                                        },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                            .testTag("send_voice_chat_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Send & Speak",
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Upload Another Software Button
                    item {
                        OutlinedButton(
                            onClick = onUploadAnotherExe,
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00D2FF))
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = "Upload Another Software",
                                tint = Color(0xFF00D2FF)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upload Another Windows 10 Software (.exe / .msi / .bat)",
                                color = Color.White
                            )
                        }
                    }
                }
            }

            1 -> {
                // Wine x64 / Box64 Execution Console Log
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF050B14))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(window.consoleLogs) { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF10B981),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            2 -> {
                // Real PE32 / PE32+ Binary Header & Extracted Strings Inspector
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "PE32 / PE32+ Executable Header & Hardware Allocation",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF00D2FF)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sw.peHeaderSummary,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                    item {
                        Text(
                            text = "Extracted Binary Strings / Script Payload:",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF10B981)
                        )
                        Surface(
                            color = Color(0xFF07101E),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = sw.extractedStrings,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFCBD5E1),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
