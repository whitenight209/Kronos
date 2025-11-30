package com.chpark.kronos.data.repository

import com.chpark.kronos.data.entity.JobEntity
import com.chpark.kronos.data.dao.JobDao
import jakarta.inject.Inject

class JobRepository @Inject constructor(
    private val dao: JobDao
) {
    suspend fun findById(id: Long) = dao.getById(id)
    fun getAllFlow() = dao.getAllFlow()

    fun getByIdFlow(id: Long) = dao.getByIdFlow(id)

    suspend fun getAll(): List<JobEntity> = dao.getAll()

    suspend fun insert(entity: JobEntity): Long = dao.insert(entity)

    suspend fun update(entity: JobEntity) = dao.update(entity)

    suspend fun delete(entity: JobEntity) = dao.delete(entity)
}