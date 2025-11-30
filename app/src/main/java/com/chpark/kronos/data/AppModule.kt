package com.chpark.kronos.data

import android.content.Context
import androidx.room.Room
import com.chpark.kronos.data.dao.JobDao
import com.chpark.kronos.data.dao.JobExecutionHistoryDao
import com.chpark.kronos.data.repository.JobRepository
import com.chpark.kronos.data.repository.JobExecutionHistoryRepository
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
    fun provideAlarmRepository(dao: JobDao) = JobRepository(dao)

    @Provides @Singleton
    fun provideExecutionHistoryRepository(dao: JobExecutionHistoryDao) =
        JobExecutionHistoryRepository(dao)
}