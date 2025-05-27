package com.nuri.feature.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuri.core.model.Meal
import com.nuri.core.model.Ingredient
import com.nuri.core.usecase.ExtractIngredientsUseCase
import com.nuri.core.usecase.SaveMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MealCaptureViewModel @Inject constructor(
    private val extractIngredientsUseCase: ExtractIngredientsUseCase,
    private val saveMealUseCase: SaveMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealCaptureUiState())
    val uiState: StateFlow<MealCaptureUiState> = _uiState.asStateFlow()

    private var currentImageUri: Uri? = null

    fun createImageUri(context: Context): Uri {
        val imageFile = File(context.cacheDir, "meal_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        currentImageUri = uri
        return uri
    }

    fun onImageCaptured() {
        currentImageUri?.let { uri ->
            onImageSelected(uri)
        }
    }

    fun onImageSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            selectedImageUri = uri,
            screenState = MealCaptureScreenState.ImageSelected(uri)
        )
    }

    fun extractIngredients() {
        val imageUri = _uiState.value.selectedImageUri ?: return

        _uiState.value = _uiState.value.copy(
            screenState = MealCaptureScreenState.ExtractingIngredients(imageUri)
        )

        viewModelScope.launch {
            extractIngredientsUseCase(imageUri)
                .onSuccess { ingredients ->
                    _uiState.value = _uiState.value.copy(
                        extractedIngredients = ingredients,
                        screenState = MealCaptureScreenState.IngredientsExtracted(
                            imageUri = imageUri,
                            ingredients = ingredients
                        )
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        screenState = MealCaptureScreenState.Error(
                            error.message ?: "Failed to extract ingredients"
                        )
                    )
                }
        }
    }

    fun saveMeal() {
        val imageUri = _uiState.value.selectedImageUri ?: return
        val ingredients = _uiState.value.extractedIngredients ?: return

        _uiState.value = _uiState.value.copy(
            screenState = MealCaptureScreenState.SavingMeal
        )

        val meal = Meal(
            id = UUID.randomUUID().toString(),
            timestamp = Instant.now(),
            imagePath = imageUri.toString(),
            ingredients = ingredients
        )

        viewModelScope.launch {
            saveMealUseCase(meal)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        navigationEvent = MealCaptureNavigationEvent.NavigateToWellbeing
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        screenState = MealCaptureScreenState.Error(
                            error.message ?: "Failed to save meal"
                        )
                    )
                }
        }
    }

    fun resetToInitial() {
        _uiState.value = MealCaptureUiState()
        currentImageUri = null
    }

    fun onNavigationEventHandled() {
        _uiState.value = _uiState.value.copy(navigationEvent = null)
    }
}

data class MealCaptureUiState(
    val selectedImageUri: Uri? = null,
    val extractedIngredients: List<Ingredient>? = null,
    val screenState: MealCaptureScreenState = MealCaptureScreenState.Initial,
    val navigationEvent: MealCaptureNavigationEvent? = null
)

sealed interface MealCaptureScreenState {
    object Initial : MealCaptureScreenState
    data class ImageSelected(val imageUri: Uri) : MealCaptureScreenState
    data class ExtractingIngredients(val imageUri: Uri) : MealCaptureScreenState
    data class IngredientsExtracted(
        val imageUri: Uri,
        val ingredients: List<Ingredient>
    ) : MealCaptureScreenState
    object SavingMeal : MealCaptureScreenState
    data class Error(val message: String) : MealCaptureScreenState
}

sealed interface MealCaptureNavigationEvent {
    object NavigateToWellbeing : MealCaptureNavigationEvent
} 