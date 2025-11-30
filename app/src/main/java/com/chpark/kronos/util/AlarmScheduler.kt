package com.chpark.kronos.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.chpark.kronos.data.entity.JobEntity
import com.chpark.kronos.data.repository.JobRepository
import com.chpark.kronos.data.repository.JobExecutionHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.Executors

@Singleton
class AlarmScheduler @Inject constructor(
    private val jobRepository: JobRepository,
    private val historyRepository: JobExecutionHistoryRepository
) {

    @SuppressLint("ScheduleExactAlarm")
    fun schedule(context: Context, alarm: JobEntity) {

        val alarmManager = context.getSystemService(AlarmManager::class.java)

        // 기존 취소
        cancel(context, alarm)

        // PendingIntent 구성
        val intent = Intent(context, ScheduleEventReceiver::class.java).apply {
            putExtra("id", alarm.id)
            putExtra("name", alarm.name)
            putExtra("cronExpr", alarm.cronExpression)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextTrigger = CronUtilsHelper.getNextExecution(alarm.cronExpression)
        alarm.nextTriggerTime = nextTrigger

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextTrigger,
            pendingIntent
        )
    }


    fun cancel(context: Context, alarm: JobEntity) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        val intent = Intent(context, ScheduleEventReceiver::class.java).apply {
            putExtra("id", alarm.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }


    /** 즉시 실행 */
    fun runNow(context: Context, alarm: JobEntity) {

        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            val exec = ScriptExecutor(
                context = context,
                alarm = alarm,
                isScheduled = false,
                historyRepo = historyRepository
            )
            exec.execute()
        }
    }


    /** Startup 시 전체 알람 재등록 */
    suspend fun registerAll(context: Context) {

        val alarms = jobRepository.getAll()
        val now = System.currentTimeMillis()

        for (a in alarms) {

            val intent = Intent(context, ScheduleEventReceiver::class.java).apply {
                putExtra("id", a.id)
            }

            val existing = PendingIntent.getBroadcast(
                context,
                a.id.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )

            if (existing != null) continue

            if (a.nextTriggerTime > now) {
                schedule(context, a)
                continue
            }

            if (a.cronExpression != null) {

                val next = CronUtilsHelper.getNextExecution(a.cronExpression)
                if (next > 0) {
                    a.nextTriggerTime = next
                    jobRepository.update(a)
                    schedule(context, a)
                }
            }
        }
    }

    fun isScheduled(context: Context, alarm: JobEntity): Boolean {
        val intent = Intent(context, ScheduleEventReceiver::class.java).apply {
            putExtra("id", alarm.id)
        }

        val pi = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        return pi != null
    }
}