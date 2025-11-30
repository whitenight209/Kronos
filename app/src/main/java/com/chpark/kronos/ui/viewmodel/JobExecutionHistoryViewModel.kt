package com.chpark.kronos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chpark.kronos.data.entity.ExecutionHistoryEntity
import com.chpark.kronos.data.repository.JobExecutionHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = true,
    val data: List<ExecutionHistoryEntity> = emptyList()
)

@HiltViewModel
class ExecutionHistoryViewModel @Inject constructor(
    private val repository: JobExecutionHistoryRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> =
        repository.getAllFlow()
            .map { list ->
                HistoryUiState(
                    isLoading = false,
                    data = list
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                HistoryUiState()
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