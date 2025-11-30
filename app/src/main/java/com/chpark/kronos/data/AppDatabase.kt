package com.chpark.kronos.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chpark.kronos.data.dao.JobDao
import com.chpark.kronos.data.dao.JobExecutionHistoryDao
import com.chpark.kronos.data.entity.JobEntity
import com.chpark.kronos.data.entity.ExecutionHistoryEntity

@Database(
    entities = [
        JobEntity::class,
        ExecutionHistoryEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): JobDao
    abstract fun historyDao(): JobExecutionHistoryDao
}