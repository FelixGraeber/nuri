 
package app.getnuri.results

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.getnuri.data.ImageGenerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ResultsViewModel @Inject constructor(
    val imageGenerationRepository: ImageGenerationRepository,
    @Named("IO")
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state = _state.asStateFlow()

    private var _snackbarHostState = MutableStateFlow(SnackbarHostState())

    val snackbarHostState: StateFlow<SnackbarHostState>
        get() = _snackbarHostState

    fun setArguments(
        resultImageUrl: Bitmap,
        originalImageUrl: Uri?,
        promptText: String?,
    ) {
        _state.update {
            ResultState(resultImageUrl, originalImageUrl, promptText = promptText)
        }
    }

    fun shareClicked() {
        viewModelScope.launch(ioDispatcher) {
            val resultUrl = state.value.resultImageBitmap
            if (resultUrl != null) {
                val imageFileUri = imageGenerationRepository.saveImage(resultUrl)

                _state.update {
                    it.copy(savedUri = imageFileUri)
                }
            }
        }
    }
    fun downloadClicked() {
        viewModelScope.launch(ioDispatcher) {
            val resultBitmap = state.value.resultImageBitmap
            val originalImage = state.value.originalImageUrl
            if (originalImage != null) {
                val savedOriginalUri = imageGenerationRepository.saveImageToExternalStorage(originalImage)
                _state.update {
                    it.copy(externalOriginalSavedUri = savedOriginalUri)
                }
            }
            if (resultBitmap != null) {
                val imageUri = imageGenerationRepository.saveImageToExternalStorage(resultBitmap)
                _state.update {
                    it.copy(externalSavedUri = imageUri)
                }
                snackbarHostState.value.showSnackbar("Download complete")
            }
        }
    }
}

data class ResultState(
    val resultImageBitmap: Bitmap? = null,
    val originalImageUrl: Uri? = null,
    val savedUri: Uri? = null,
    val externalSavedUri: Uri? = null,
    val externalOriginalSavedUri: Uri? = null,
    val promptText: String? = null,
    val wellbeingData: WellbeingData = MockWellbeingDataGenerator.generateMockData(),
)
