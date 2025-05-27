package com.nuri.feature.camera

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealCaptureScreen(
    onNavigateToWellbeing: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MealCaptureViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.onImageCaptured()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onImageSelected(it) }
    }

    LaunchedEffect(uiState.navigationEvent) {
        when (uiState.navigationEvent) {
            is MealCaptureNavigationEvent.NavigateToWellbeing -> {
                onNavigateToWellbeing()
                viewModel.onNavigationEventHandled()
            }
            null -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Capture Meal") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        when (val state = uiState.screenState) {
            is MealCaptureScreenState.Initial -> {
                InitialCaptureContent(
                    onTakePhoto = {
                        val uri = viewModel.createImageUri(context)
                        cameraLauncher.launch(uri)
                    },
                    onSelectFromGallery = {
                        galleryLauncher.launch("image/*")
                    }
                )
            }
            is MealCaptureScreenState.ImageSelected -> {
                ImageSelectedContent(
                    imageUri = state.imageUri,
                    onExtractIngredients = viewModel::extractIngredients,
                    onRetakePhoto = {
                        val uri = viewModel.createImageUri(context)
                        cameraLauncher.launch(uri)
                    }
                )
            }
            is MealCaptureScreenState.ExtractingIngredients -> {
                ExtractingIngredientsContent(imageUri = state.imageUri)
            }
            is MealCaptureScreenState.IngredientsExtracted -> {
                IngredientsExtractedContent(
                    imageUri = state.imageUri,
                    ingredients = state.ingredients,
                    onSaveMeal = viewModel::saveMeal,
                    onRetry = viewModel::extractIngredients
                )
            }
            is MealCaptureScreenState.SavingMeal -> {
                SavingMealContent()
            }
            is MealCaptureScreenState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = {
                        when {
                            uiState.selectedImageUri != null -> viewModel.extractIngredients()
                            else -> viewModel.resetToInitial()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun InitialCaptureContent(
    onTakePhoto: () -> Unit,
    onSelectFromGallery: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Capture your meal to track ingredients",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onTakePhoto,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Take Photo")
        }

        OutlinedButton(
            onClick = onSelectFromGallery,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Select from Gallery")
        }
    }
}

@Composable
private fun ImageSelectedContent(
    imageUri: Uri,
    onExtractIngredients: () -> Unit,
    onRetakePhoto: () -> Unit
) {
    Column {
        AsyncImage(
            model = imageUri,
            contentDescription = "Captured meal",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onExtractIngredients,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Extract Ingredients")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onRetakePhoto,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Retake Photo")
        }
    }
}

@Composable
private fun ExtractingIngredientsContent(imageUri: Uri) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Captured meal",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(32.dp))

        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(16.dp))

        Text("Analyzing ingredients...")
    }
}

@Composable
private fun IngredientsExtractedContent(
    imageUri: Uri,
    ingredients: List<com.nuri.core.model.Ingredient>,
    onSaveMeal: () -> Unit,
    onRetry: () -> Unit
) {
    Column {
        AsyncImage(
            model = imageUri,
            contentDescription = "Captured meal",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Detected Ingredients:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(ingredients) { ingredient ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = ingredient.name,
                            style = MaterialTheme.typography.titleSmall
                        )
                        ingredient.quantity?.let { quantity ->
                            Text(
                                text = quantity,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSaveMeal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Meal")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Re-analyze")
        }
    }
}

@Composable
private fun SavingMealContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Saving meal...")
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $message",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
} 