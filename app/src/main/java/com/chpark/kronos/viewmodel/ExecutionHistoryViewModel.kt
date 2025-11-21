package com.chpark.kronos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chpark.kronos.data.entity.ExecutionHistoryEntity
import com.chpark.kronos.data.repository.ExecutionHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class ExecutionHistoryViewModel @Inject constructor(
    private val repository: ExecutionHistoryRepository
) : ViewModel() {

    /** 전체 히스토리 Flow */
    val histories: StateFlow<List<ExecutionHistoryEntity>> =
        repository.getAllHistories()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    /** ID 조회 (suspend) */
    suspend fun getById(id: Long): ExecutionHistoryEntity? {
        return repository.getHistoryById(id)
    }

    /** 싹 초기화 */
    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}