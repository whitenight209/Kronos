package com.chpark.kronos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var name: String = "",
    var cronExpression: String = "",
    var nextTriggerTime: Long = 0L,
    var command: String = "",
    var enableScreenshot: Boolean = false
)