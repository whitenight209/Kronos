package com.chpark.kronos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.chpark.kronos.data.entity.ExecutionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExecutionHistoryDao {

    @Query("SELECT * FROM execution_history ORDER BY executedAt DESC")
    fun getAllFlow(): Flow<List<ExecutionHistoryEntity>>

    @Query("DELETE FROM execution_history")
    suspend fun clearAll()

    // ① 실행 기록 신규 등록
    @Insert
    fun insert(entity: ExecutionHistoryEntity): Long
    // suspend 사용도 가능함 (Repository 설계 방식에 따라 선택)

    // ② 실행 기록 업데이트
    @Update
    fun update(entity: ExecutionHistoryEntity)

    // ③ 특정 id 조회
    @Query("SELECT * FROM execution_history WHERE id = :id LIMIT 1")
    fun findById(id: Long): ExecutionHistoryEntity?

    // ④ 전체 히스토리 조회 (리스트 화면용)
    @Query("SELECT * FROM execution_history ORDER BY executedAt DESC")
    fun getAll(): List<ExecutionHistoryEntity>

    @Query("SELECT * FROM execution_history WHERE id = :id")
    fun getHistoryById(id: Long): ExecutionHistoryEntity


    @Query("SELECT * FROM execution_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ExecutionHistoryEntity?
}