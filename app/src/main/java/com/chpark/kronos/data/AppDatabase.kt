package com.chpark.kronos.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chpark.kronos.data.dao.AlarmDao
import com.chpark.kronos.data.dao.ExecutionHistoryDao
import com.chpark.kronos.data.entity.AlarmEntity
import com.chpark.kronos.data.entity.ExecutionHistoryEntity

@Database(
    entities = [
        AlarmEntity::class,
        ExecutionHistoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun historyDao(): ExecutionHistoryDao
}