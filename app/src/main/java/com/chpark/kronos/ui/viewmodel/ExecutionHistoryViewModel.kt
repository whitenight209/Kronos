package com.chpark.kronos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chpark.kronos.data.entity.ExecutionHistoryEntity
import com.chpark.kronos.data.repository.ExecutionHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExecutionHistoryViewModel @Inject constructor(
    private val repository: ExecutionHistoryRepository
) : ViewModel() {

    val histories: StateFlow<List<ExecutionHistoryEntity>> =
        repository.getAllFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    suspend fun getById(id: Long): ExecutionHistoryEntity? =
        repository.getById(id)

    fun addHistory(entity: ExecutionHistoryEntity) {
        viewModelScope.launch {
            repository.insert(entity)
        }
    }
    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}