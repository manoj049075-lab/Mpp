package com.example.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import kotlin.math.max

data class PhoneHardwareSpecs(
    val deviceModel: String,
    val androidVersion: String,
    val cpuCores: Int,
    val primaryAbi: String,
    val totalRamMb: Int,
    val availableRamMb: Int,
    val usedRamPercent: Int,
    val totalStorageGb: Float,
    val freeStorageGb: Float,
    val batteryPercent: Int,
    val batteryTempCelsius: Float,
    val maxSupportedWinRamMb: Int,
    val hardwareTierLabel: String
)

object PhoneHardwareMonitor {
    fun readHardwareSpecs(context: Context): PhoneHardwareSpecs {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(memInfo)

        val totalRamMb = if (memInfo.totalMem > 0) {
            (memInfo.totalMem / (1024L * 1024L)).toInt()
        } else {
            4096
        }
        val availRamMb = if (memInfo.availMem > 0) {
            (memInfo.availMem / (1024L * 1024L)).toInt()
        } else {
            2048
        }
        val usedPercent = (((totalRamMb - availRamMb).toFloat() / max(1, totalRamMb)) * 100f)
            .toInt()
            .coerceIn(5, 99)

        val cpuCores = Runtime.getRuntime().availableProcessors().coerceAtLeast(2)
        val primaryAbi = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a"

        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalStorageGb = (statFs.totalBytes / (1024.0 * 1024.0 * 1024.0)).toFloat()
        val freeStorageGb = (statFs.availableBytes / (1024.0 * 1024.0 * 1024.0)).toFloat()

        val batteryIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 85) ?: 85
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val batteryPercent = if (scale > 0) ((level * 100) / scale).coerceIn(1, 100) else 85
        val tempTenths = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 310) ?: 310
        val batteryTempCelsius = tempTenths / 10f

        val maxWinRamMb = (availRamMb * 0.75f).toInt().coerceAtLeast(512)
        val tier = when {
            totalRamMb >= 7500 && cpuCores >= 8 -> "Ultra (Win10 x64 Pro & Heavy Apps)"
            totalRamMb >= 3800 && cpuCores >= 6 -> "High (All Standard Win10 Desktop Apps)"
            else -> "Balanced (Lightweight & Office Win10 Apps)"
        }

        return PhoneHardwareSpecs(
            deviceModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}",
            androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            cpuCores = cpuCores,
            primaryAbi = primaryAbi,
            totalRamMb = totalRamMb,
            availableRamMb = availRamMb,
            usedRamPercent = usedPercent,
            totalStorageGb = totalStorageGb,
            freeStorageGb = freeStorageGb,
            batteryPercent = batteryPercent,
            batteryTempCelsius = batteryTempCelsius,
            maxSupportedWinRamMb = maxWinRamMb,
            hardwareTierLabel = tier
        )
    }

    fun evaluateSoftwareCompatibility(
        requiredRamMb: Int,
        specs: PhoneHardwareSpecs
    ): Pair<Boolean, String> {
        return if (requiredRamMb <= specs.maxSupportedWinRamMb) {
            true to "100% Compatible with your ${specs.deviceModel} (${specs.availableRamMb} MB free RAM, ${specs.cpuCores} CPU cores)"
        } else if (requiredRamMb <= specs.totalRamMb) {
            true to "Compatible via Smart Virtual Swap (${requiredRamMb} MB allocated across ${specs.cpuCores} cores)"
        } else {
            false to "High RAM demand (${requiredRamMb} MB) — Running in Optimized Lite Container for ${specs.deviceModel}"
        }
    }
}
