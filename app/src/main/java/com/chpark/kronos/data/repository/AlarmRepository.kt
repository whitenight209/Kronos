package com.chpark.kronos.data.repository

import com.chpark.kronos.data.entity.AlarmEntity
import com.chpark.kronos.data.dao.AlarmDao
import jakarta.inject.Inject

class AlarmRepository @Inject constructor(
    private val dao: AlarmDao
) {
    suspend fun findById(id: Long) = dao.getById(id)
    fun getAllFlow() = dao.getAllFlow()

    fun getByIdFlow(id: Long) = dao.getByIdFlow(id)

    suspend fun getAll(): List<AlarmEntity> = dao.getAll()

    suspend fun insert(entity: AlarmEntity): Long = dao.insert(entity)

    suspend fun update(entity: AlarmEntity) = dao.update(entity)

    suspend fun delete(entity: AlarmEntity) = dao.delete(entity)
}