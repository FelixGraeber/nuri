package com.nuri.feature.wellbeing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuri.core.model.Wellbeing
import com.nuri.core.usecase.SaveWellbeingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WellbeingEntryViewModel @Inject constructor(
    private val saveWellbeingUseCase: SaveWellbeingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WellbeingEntryUiState())
    val uiState: StateFlow<WellbeingEntryUiState> = _uiState.asStateFlow()

    fun updateMoodScore(score: Int) {
        _uiState.value = _uiState.value.copy(moodScore = score)
    }

    fun updateEnergyLevel(level: Int) {
        _uiState.value = _uiState.value.copy(energyLevel = level)
    }

    fun updateNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun saveWellbeing() {
        _uiState.value = _uiState.value.copy(
            screenState = WellbeingScreenState.Saving
        )

        val wellbeing = Wellbeing(
            id = UUID.randomUUID().toString(),
            timestamp = Instant.now(),
            moodScore = _uiState.value.moodScore,
            energyLevel = _uiState.value.energyLevel,
            note = _uiState.value.note.takeIf { it.isNotBlank() }
        )

        viewModelScope.launch {
            saveWellbeingUseCase(wellbeing)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        navigationEvent = WellbeingNavigationEvent.NavigateToHistory
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        screenState = WellbeingScreenState.Error(
                            error.message ?: "Failed to save wellbeing entry"
                        )
                    )
                }
        }
    }

    fun onNavigationEventHandled() {
        _uiState.value = _uiState.value.copy(navigationEvent = null)
    }
}

data class WellbeingEntryUiState(
    val moodScore: Int = 5,
    val energyLevel: Int = 5,
    val note: String = "",
    val screenState: WellbeingScreenState = WellbeingScreenState.Entry,
    val navigationEvent: WellbeingNavigationEvent? = null
)

sealed interface WellbeingScreenState {
    object Entry : WellbeingScreenState
    object Saving : WellbeingScreenState
    data class Error(val message: String) : WellbeingScreenState
}

sealed interface WellbeingNavigationEvent {
    object NavigateToHistory : WellbeingNavigationEvent
} 