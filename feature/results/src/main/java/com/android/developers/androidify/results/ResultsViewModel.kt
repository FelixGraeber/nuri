package app.getnuri.results

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor() : ViewModel() {
    
    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state.asStateFlow()
    
    private val _snackbarHostState = MutableStateFlow(SnackbarHostState())
    val snackbarHostState: StateFlow<SnackbarHostState> = _snackbarHostState.asStateFlow()
    
    fun setArguments(resultImage: Bitmap, originalImageUri: Uri?, promptText: String?) {
        _state.value = _state.value.copy(
            resultImageBitmap = resultImage,
            originalImageUrl = originalImageUri?.toString(),
            promptText = promptText,
            wellbeingData = MockWellbeingDataGenerator.generateMockData()
        )
    }
    
    fun downloadClicked() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                
                // Simulate download process
                // In a real app, you would save the image to device storage here
                
                _snackbarHostState.value.showSnackbar("Image downloaded successfully")
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to download image: ${e.message}"
                )
                _snackbarHostState.value.showSnackbar("Failed to download image")
            }
        }
    }
    
    fun shareClicked() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                
                // Simulate creating a shareable URI
                // In a real app, you would create a temporary file and get its URI
                val mockUri = Uri.parse("content://mock/image.jpg")
                
                _state.value = _state.value.copy(
                    savedUri = mockUri,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to prepare image for sharing: ${e.message}"
                )
                _snackbarHostState.value.showSnackbar("Failed to share image")
            }
        }
    }
    
    fun clearSavedUri() {
        _state.value = _state.value.copy(savedUri = null)
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
} 