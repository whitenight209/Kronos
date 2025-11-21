package com.chpark.kronos.data.repository

import com.chpark.kronos.data.dao.ExecutionHistoryDao
import com.chpark.kronos.data.entity.ExecutionHistoryEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow


class ExecutionHistoryRepository @Inject constructor(
    private val dao: ExecutionHistoryDao
) {
    fun getAllFlow() = dao.getAllFlow()

    suspend fun clearAll() = dao.clearAll()
    fun insert(entity: ExecutionHistoryEntity): Long {
        return dao.insert(entity)
    }

    fun update(entity: ExecutionHistoryEntity) {
        dao.update(entity)
    }

    fun findById(id: Long): ExecutionHistoryEntity? {
        return dao.findById(id)
    }
    fun getHistoryByIdSync(id: Long): ExecutionHistoryEntity {
        return dao.getHistoryById(id)
    }
    suspend fun getById(id: Long): ExecutionHistoryEntity? = dao.getById(id)

    fun getAllHistories(): Flow<List<ExecutionHistoryEntity>> =
        dao.getAllFlow()

    suspend fun getHistoryById(id: Long): ExecutionHistoryEntity? =
        dao.getById(id)
}