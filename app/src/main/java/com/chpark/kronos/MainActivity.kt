package com.chpark.kronos

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager

import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.chpark.kronos.ui.theme.KronosTheme
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.chpark.kronos.ui.components.KronosApp
import com.chpark.kronos.util.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    // 알림 권한 런처
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                checkExactAlarmPermission()
            } else {
                Toast.makeText(this, "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        checkNotificationPermission()

        // Alarm 재등록
        lifecycleScope.launch {
            alarmScheduler.registerAll(
                context = this@MainActivity
            )
        }
    }

    // --------------------------
    // 권한 체크 처리
    // --------------------------

    private fun checkNotificationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        checkExactAlarmPermission()
    }

    private fun checkExactAlarmPermission() {
        val alarmManager = getSystemService(AlarmManager::class.java)

        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(this, "정확한 알람 권한이 필요합니다.", Toast.LENGTH_SHORT).show()

            // 정확한 알람 권한 요청 화면 이동
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = "package:$packageName".toUri()
            }
            startActivity(intent)
            return
        }

        // 모든 권한 허용 → App 실행
        startAppUI()
    }

    private fun startAppUI() {
        setContent {
            KronosTheme {
                KronosApp(activity = this)   // 기존 Compose UI 실행
            }
        }
    }
}

