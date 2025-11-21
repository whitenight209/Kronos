package com.chpark.kronos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "execution_history")
class ExecutionHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var alarmId: Int = 0
    var label: String? = null
    var executedAt: Long = 0
    var exitCode: Int = 0
    var output: String? = null
    var success: Boolean = false
    var commands: String? = null // 여러 줄 저장
    var screenshotPath: String? = null // 파일 경로 or null
}
