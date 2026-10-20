package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.ExeBinaryAnalyzer
import com.example.util.PhoneHardwareMonitor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name and verify PE32Plus executable analyzer and phone hardware monitor`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WinBox 10", appName)

        val bytes = ExeBinaryAnalyzer.buildRealPe32PlusExecutableBytes(
            appName = "TestStudioWin10",
            uiManifestStrings = listOf("Voice Module Ready", "Full Screen GUI")
        )
        val parsed = ExeBinaryAnalyzer.parseBytesIntoSoftwareEntity(
            fileName = "TestStudioWin10.exe",
            fileSizeBytes = 24 * 1024 * 1024L,
            headerBytes = bytes
        )
        assertTrue(parsed.peHeaderSummary.contains("Valid MZ/PE Header"))
        assertTrue(parsed.architecture.contains("x64"))

        val specs = PhoneHardwareMonitor.readHardwareSpecs(context)
        assertTrue(specs.cpuCores >= 1)
        assertTrue(specs.totalRamMb > 0)
    }
}
