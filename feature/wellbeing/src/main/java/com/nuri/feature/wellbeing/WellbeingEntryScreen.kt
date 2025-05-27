package com.nuri.feature.wellbeing

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellbeingEntryScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: WellbeingEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigationEvent) {
        when (uiState.navigationEvent) {
            is WellbeingNavigationEvent.NavigateToHistory -> {
                onNavigateToHistory()
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
            title = { Text("How are you feeling?") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        when (uiState.screenState) {
            is WellbeingScreenState.Entry -> {
                WellbeingEntryContent(
                    moodScore = uiState.moodScore,
                    energyLevel = uiState.energyLevel,
                    note = uiState.note,
                    onMoodScoreChange = viewModel::updateMoodScore,
                    onEnergyLevelChange = viewModel::updateEnergyLevel,
                    onNoteChange = viewModel::updateNote,
                    onSave = viewModel::saveWellbeing
                )
            }
            is WellbeingScreenState.Saving -> {
                SavingContent()
            }
            is WellbeingScreenState.Error -> {
                ErrorContent(
                    message = uiState.screenState.message,
                    onRetry = viewModel::saveWellbeing
                )
            }
        }
    }
}

@Composable
private fun WellbeingEntryContent(
    moodScore: Int,
    energyLevel: Int,
    note: String,
    onMoodScoreChange: (Int) -> Unit,
    onEnergyLevelChange: (Int) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Mood Score Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "How is your mood?",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Score: $moodScore/10",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Slider(
                    value = moodScore.toFloat(),
                    onValueChange = { onMoodScoreChange(it.toInt()) },
                    valueRange = 1f..10f,
                    steps = 8,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("😢 Poor", style = MaterialTheme.typography.bodySmall)
                    Text("😊 Great", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Energy Level Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "How is your energy level?",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Level: $energyLevel/10",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Slider(
                    value = energyLevel.toFloat(),
                    onValueChange = { onEnergyLevelChange(it.toInt()) },
                    valueRange = 1f..10f,
                    steps = 8,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("😴 Low", style = MaterialTheme.typography.bodySmall)
                    Text("⚡ High", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Notes Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Additional notes (optional)",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    placeholder = { Text("How are you feeling? Any symptoms?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 4
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Wellbeing Entry")
        }
    }
}

@Composable
private fun SavingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Saving wellbeing entry...")
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