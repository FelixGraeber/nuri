package com.nuri.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuri.core.model.HistoryItem
import com.nuri.core.usecase.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        _uiState.value = _uiState.value.copy(
            screenState = HistoryScreenState.Loading
        )

        viewModelScope.launch {
            getHistoryUseCase()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        screenState = HistoryScreenState.Error(
                            error.message ?: "Failed to load history"
                        )
                    )
                }
                .collect { historyItems ->
                    _uiState.value = _uiState.value.copy(
                        screenState = if (historyItems.isEmpty()) {
                            HistoryScreenState.Empty
                        } else {
                            HistoryScreenState.Content(historyItems)
                        }
                    )
                }
        }
    }
}

data class HistoryUiState(
    val screenState: HistoryScreenState = HistoryScreenState.Loading
)

sealed interface HistoryScreenState {
    object Loading : HistoryScreenState
    data class Content(val items: List<HistoryItem>) : HistoryScreenState
    object Empty : HistoryScreenState
    data class Error(val message: String) : HistoryScreenState
} 