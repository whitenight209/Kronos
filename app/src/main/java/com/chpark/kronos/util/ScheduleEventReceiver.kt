package com.chpark.kronos.util


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.chpark.kronos.R
import com.chpark.kronos.data.repository.JobExecutionHistoryRepository
import com.chpark.kronos.data.repository.JobRepository
import com.chpark.kronos.util.CronUtilsHelper.getNextExecution
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleEventReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var jobRepository: JobRepository
    @Inject lateinit var historyRepository: JobExecutionHistoryRepository

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Alarm received!")

        val id = intent.getLongExtra("id", 0L)
        val name = intent.getStringExtra("name") ?: "(이름 없음)"
        val cronExpr = intent.getStringExtra("cronExpr")

        // 즉시 알림 표시
        sendNotification(context, name)

        CoroutineScope(Dispatchers.IO).launch {
            // ① 알람 엔티티 로드
            val alarm = jobRepository.findById(id)
            if (alarm == null) {
                Log.e(TAG, "Alarm not found id=$id")
                return@launch
            }
            // ② 다음 실행시간 계산 + 재등록
            if (!cronExpr.isNullOrEmpty()) {
                val next = getNextExecution(cronExpr)
                alarm.nextTriggerTime = next
                jobRepository.update(alarm)

                // Hilt 주입된 Scheduler 사용
                alarmScheduler.schedule(context, alarm)
            }
            // ③ 스크립트 실행
            try {
                val executor = ScriptExecutor(
                    context = context,
                    alarm = alarm,
                    isScheduled = true,
                    historyRepo = historyRepository
                )
                executor.execute()
            } catch (e: Exception) {
                Log.e(TAG, "ScriptExecutor exception", e)
            }
        }
    }

    private fun sendNotification(context: Context, alertName: String?) {
        val manager = context.getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Kronos Alarm Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_automation_24)
            .setContentTitle("예약된 알람 실행")
            .setContentText("'$alertName' 알람이 실행되었습니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        private const val CHANNEL_ID = "KronosAlarmChannel"
        private const val TAG = "ScheduleEventReceiver"
    }
}