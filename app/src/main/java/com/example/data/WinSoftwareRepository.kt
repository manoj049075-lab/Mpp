package com.example.data

import kotlinx.coroutines.flow.Flow

class WinSoftwareRepository(private val dao: WinSoftwareDao) {
    val allSoftware: Flow<List<WinSoftwareEntity>> = dao.getAllSoftware()

    suspend fun insert(software: WinSoftwareEntity): Long = dao.insertSoftware(software)

    suspend fun update(software: WinSoftwareEntity) = dao.updateSoftware(software)

    suspend fun deleteById(id: Long) = dao.deleteSoftwareById(id)

    suspend fun seedBuiltInSoftwareIfNeeded() {
        if (dao.getSoftwareCount() > 0) return
        val defaults = listOf(
            WinSoftwareEntity(
                name = "Upload Win10 Software",
                exeFileName = "win_installer.exe",
                installPath = "C:\\Windows\\System32\\win_installer.exe",
                architecture = "x64 (PE32+)",
                subsystem = "WINDOWS_GUI",
                fileSizeBytes = 4_250_000L,
                requiredRamMb = 128,
                peHeaderSummary = "PE32+ Executable (GUI) x86-64 | Universal Package Loader",
                extractedStrings = "Upload any .exe, .msi, .bat, .cmd, .zip or script from phone storage and run in full screen with Voice Talk.",
                appType = "INSTALLER",
                isUserUploaded = false
            ),
            WinSoftwareEntity(
                name = "Permissions & Hardware",
                exeFileName = "perm_mgr.exe",
                installPath = "C:\\Windows\\System32\\perm_mgr.exe",
                architecture = "ARM64 / x64 Bridge",
                subsystem = "WINDOWS_GUI",
                fileSizeBytes = 2_180_000L,
                requiredRamMb = 64,
                peHeaderSummary = "PE32+ System Control Panel | Phone Permission & Hardware Bridge",
                extractedStrings = "Grant all phone permissions (Microphone, Notifications, Background Service, Storage) and check phone RAM/CPU capability.",
                appType = "PERMISSIONS",
                isUserUploaded = false
            ),
            WinSoftwareEntity(
                name = "This PC (Explorer)",
                exeFileName = "explorer.exe",
                installPath = "C:\\Windows\\explorer.exe",
                architecture = "x64 (PE32+)",
                subsystem = "WINDOWS_GUI",
                fileSizeBytes = 5_840_000L,
                requiredRamMb = 192,
                peHeaderSummary = "PE32+ Executable (GUI) x86-64 | Windows 10 File Explorer",
                extractedStrings = "C:\\Windows\\System32 | C:\\Program Files | C:\\Users\\Administrator\\Desktop",
                appType = "EXPLORER",
                isUserUploaded = false
            ),
            WinSoftwareEntity(
                name = "Command Prompt",
                exeFileName = "cmd.exe",
                installPath = "C:\\Windows\\System32\\cmd.exe",
                architecture = "x64 (PE32+)",
                subsystem = "WINDOWS_CUI",
                fileSizeBytes = 1_420_000L,
                requiredRamMb = 64,
                peHeaderSummary = "PE32+ Console (CUI) x86-64 | Microsoft Windows [Version 10.0.19045]",
                extractedStrings = "Microsoft Windows [Version 10.0.19045.4894]\n(c) Microsoft Corporation. All rights reserved.",
                appType = "CMD",
                isUserUploaded = false
            ),
            WinSoftwareEntity(
                name = "Notepad",
                exeFileName = "notepad.exe",
                installPath = "C:\\Windows\\System32\\notepad.exe",
                architecture = "x64 (PE32+)",
                subsystem = "WINDOWS_GUI",
                fileSizeBytes = 980_000L,
                requiredRamMb = 48,
                peHeaderSummary = "PE32+ Executable (GUI) x86-64 | Windows 10 Text Editor + Voice Reader",
                extractedStrings = "Namaste! Welcome to WinBox 10.\nUpload any Windows 10 software (.exe, .msi, .bat) and it will run in Full Screen and talk to you in voice!",
                appType = "NOTEPAD",
                isUserUploaded = false
            ),
            WinSoftwareEntity(
                name = "Paint Studio",
                exeFileName = "mspaint.exe",
                installPath = "C:\\Windows\\System32\\mspaint.exe",
                architecture = "x64 (PE32+)",
                subsystem = "WINDOWS_GUI",
                fileSizeBytes = 3_620_000L,
                requiredRamMb = 150,
                peHeaderSummary = "PE32+ Executable (GUI) x86-64 | GDI32.dll + USER32.dll Canvas Engine",
                extractedStrings = "Interactive Windows 10 Paint Canvas with Mouse Cursor & Touch drawing support.",
                appType = "PAINT",
                isUserUploaded = false
            ),
            WinSoftwareEntity(
                name = "Task Manager",
                exeFileName = "taskmgr.exe",
                installPath = "C:\\Windows\\System32\\taskmgr.exe",
                architecture = "x64 (PE32+)",
                subsystem = "WINDOWS_GUI",
                fileSizeBytes = 2_450_000L,
                requiredRamMb = 96,
                peHeaderSummary = "PE32+ System Telemetry | Real Phone CPU, RAM & Background Process Monitor",
                extractedStrings = "Real-time CPU cores, RAM allocation, and Background Service monitor.",
                appType = "TASKMGR",
                isUserUploaded = false
            ),
            WinSoftwareEntity(
                name = "Calculator",
                exeFileName = "calc.exe",
                installPath = "C:\\Windows\\System32\\calc.exe",
                architecture = "x64 (PE32+)",
                subsystem = "WINDOWS_GUI",
                fileSizeBytes = 1_150_000L,
                requiredRamMb = 48,
                peHeaderSummary = "PE32+ Executable (GUI) x86-64 | Windows 10 Standard Calculator",
                extractedStrings = "Standard & Voice-Enabled Windows 10 Calculator",
                appType = "CALC",
                isUserUploaded = false
            )
        )
        defaults.forEach { dao.insertSoftware(it) }
    }
}
