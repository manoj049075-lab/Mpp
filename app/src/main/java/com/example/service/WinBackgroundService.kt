package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WinBackgroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var heartbeatJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP) {
            stopBackgroundEngine()
            stopSelf()
            return START_NOT_STICKY
        }

        val activeAppsCount = intent?.getIntExtra(EXTRA_ACTIVE_APPS, 1) ?: 1
        val latestAppName = intent?.getStringExtra(EXTRA_LATEST_APP_NAME) ?: "Windows 10 Desktop"

        startForegroundSafely(activeAppsCount, latestAppName)
        acquireWakeLockSafely()
        _isRunning.value = true

        if (heartbeatJob?.isActive != true) {
            heartbeatJob = serviceScope.launch {
                while (isActive) {
                    delay(1000L)
                    _uptimeSeconds.value += 1L
                }
            }
        }

        return START_STICKY
    }

    private fun startForegroundSafely(activeAppsCount: Int, latestAppName: String) {
        val notification = buildNotification(activeAppsCount, latestAppName)
        try {
            startForeground(NOTIFICATION_ID, notification)
        } catch (_: Exception) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun acquireWakeLockSafely() {
        try {
            if (wakeLock?.isHeld != true) {
                val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
                wakeLock = pm?.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "WinBox10::BackgroundExecutionWakeLock"
                )?.apply {
                    acquire(4 * 60 * 60 * 1000L) // Up to 4 hours continuous background execution
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun stopBackgroundEngine() {
        _isRunning.value = false
        heartbeatJob?.cancel()
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (_: Exception) {
        }
        try {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        stopBackgroundEngine()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "WinBox 10 Background Engine",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps uploaded Windows 10 software active in the background"
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(activeAppsCount: Int, latestAppName: String): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingOpen = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentTitle("WinBox 10 Active in Background ($activeAppsCount running)")
            .setContentText("Running: $latestAppName • Tap to return to Full Screen")
            .setOngoing(true)
            .setContentIntent(pendingOpen)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "winbox10_bg_channel"
        private const val NOTIFICATION_ID = 1010
        const val ACTION_START = "com.example.service.START_WIN_BG"
        const val ACTION_STOP = "com.example.service.STOP_WIN_BG"
        const val EXTRA_ACTIVE_APPS = "extra_active_apps"
        const val EXTRA_LATEST_APP_NAME = "extra_latest_app_name"

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        private val _uptimeSeconds = MutableStateFlow(0L)
        val uptimeSeconds: StateFlow<Long> = _uptimeSeconds.asStateFlow()

        fun startOrUpdate(context: Context, activeAppsCount: Int, latestAppName: String) {
            val intent = Intent(context, WinBackgroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ACTIVE_APPS, activeAppsCount)
                putExtra(EXTRA_LATEST_APP_NAME, latestAppName)
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {
                try {
                    context.startService(intent)
                } catch (_: Exception) {
                }
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, WinBackgroundService::class.java).apply {
                action = ACTION_STOP
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {
                _isRunning.value = false
            }
        }
    }
}
