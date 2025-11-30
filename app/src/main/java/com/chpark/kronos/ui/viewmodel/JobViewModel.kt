package com.chpark.kronos.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chpark.kronos.data.entity.JobEntity
import com.chpark.kronos.data.repository.JobRepository
import com.chpark.kronos.ui.dto.JobUiModel
import com.chpark.kronos.util.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class JobViewModel @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val repository: JobRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    val uiJobs: StateFlow<List<JobUiModel>> =
        repository.getAllFlow()
            .map { list ->
                list.map { alarm ->
                    JobUiModel(
                        entity = alarm,
                        isScheduled = alarmScheduler.isScheduled(appContext, alarm)
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    /**
     * Insert or Update Alarm
     */
    fun saveAlarm(
        id: Long? = null,
        name: String,
        description: String,
        useCron: Boolean,
        cronExpression: String,
        command: String,
        useSu: Boolean,
        enableScreenshot: Boolean,
        onDone: (JobEntity) -> Unit
    ) {
        viewModelScope.launch {
            val alarm = JobEntity(
                id = id ?: 0L,
                name = name,
                useCron = useCron,
                description = description,
                cronExpression = cronExpression,
                nextTriggerTime = System.currentTimeMillis(),
                command = command,
                useSu = useSu,
                enableScreenshot = enableScreenshot
            )

            if (id == null || id == 0L) {
                repository.insert(alarm)
            } else {
                repository.update(alarm)
            }

            onDone(alarm)
        }
    }

    fun deleteAlarm(entity: JobEntity) {
        viewModelScope.launch {
            repository.delete(entity)
        }
    }

    fun getAlarmFlow(id: Long): Flow<JobEntity?> {
        return repository.getByIdFlow(id)
    }

    fun runNow(context: Context, alarm: JobEntity) {
        alarmScheduler.runNow(context, alarm)
    }

    fun scheduleIfCronExpressionNotNull(context: Context, alarm: JobEntity) {
        if (alarm.useCron) {
            alarmScheduler.schedule(context, alarm)
        }
    }
    fun isScheduled(context: Context, alarm: JobEntity): Boolean {
        return alarmScheduler.isScheduled(context, alarm)
    }
}