package com.chpark.kronos.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chpark.kronos.data.entity.AlarmEntity
import com.chpark.kronos.data.repository.AlarmRepository
import com.chpark.kronos.data.repository.ExecutionHistoryRepository
import com.chpark.kronos.util.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val repository: AlarmRepository
) : ViewModel() {

    // StateFlow for UI
    val alarms: StateFlow<List<AlarmEntity>> =
        repository.getAllFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    /**
     * Insert or Update Alarm
     */
    fun saveAlarm(
        id: Long? = null,
        name: String,
        cronExpression: String,
        command: String,
        enableScreenshot: Boolean,
        onDone: (AlarmEntity) -> Unit
    ) {
        viewModelScope.launch {
            val alarm = AlarmEntity(
                id = id ?: 0L,
                name = name,
                cronExpression = cronExpression,
                nextTriggerTime = System.currentTimeMillis(),
                command = command,
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

    fun deleteAlarm(entity: AlarmEntity) {
        viewModelScope.launch {
            repository.delete(entity)
        }
    }

    fun getAlarmFlow(id: Long): Flow<AlarmEntity?> {
        return repository.getByIdFlow(id)
    }

    fun runNow(context: Context, alarm: AlarmEntity) {
        alarmScheduler.runNow(context, alarm)
    }

    fun schedule(context: Context, alarm: AlarmEntity) {
        alarmScheduler.schedule(context, alarm)
    }

}