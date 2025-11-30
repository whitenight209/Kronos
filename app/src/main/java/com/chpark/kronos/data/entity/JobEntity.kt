package com.chpark.kronos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var name: String = "",
    var description: String = "",
    val useCron: Boolean,          // ★ cron 활성/비활성 명시
    val cronExpression: String,     // 빈 문자열 허용, null 없음
    var nextTriggerTime: Long = 0L,
    var command: String = "",
    var enableScreenshot: Boolean = false,
    val useSu: Boolean,                 // su 권한 여부
)