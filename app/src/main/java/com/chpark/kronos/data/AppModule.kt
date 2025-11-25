package com.chpark.kronos.data

import android.content.Context
import androidx.room.Room
import com.chpark.kronos.data.dao.AlarmDao
import com.chpark.kronos.data.dao.ExecutionHistoryDao
import com.chpark.kronos.data.repository.AlarmRepository
import com.chpark.kronos.data.repository.ExecutionHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app.db"
        )
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides fun provideAlarmDao(db: AppDatabase) = db.alarmDao()
    @Provides fun provideHistoryDao(db: AppDatabase) = db.historyDao()

    @Provides @Singleton
    fun provideAlarmRepository(dao: AlarmDao) = AlarmRepository(dao)

    @Provides @Singleton
    fun provideExecutionHistoryRepository(dao: ExecutionHistoryDao) =
        ExecutionHistoryRepository(dao)
}