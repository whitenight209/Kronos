package com.chpark.kronos.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.chpark.kronos.data.entity.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    @Insert
    suspend fun insert(alarm: JobEntity): Long

    @Update
    suspend fun update(alarm: JobEntity)

    @Delete
    suspend fun delete(alarm: JobEntity)

    @Query("SELECT * FROM jobs ORDER BY id DESC")
    suspend fun getAll(): List<JobEntity>

    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getById(id: Long): JobEntity?

    @Query("SELECT * FROM jobs ORDER BY id DESC")
    fun getAllFlow(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    fun getByIdFlow(id: Long): Flow<JobEntity?>
}