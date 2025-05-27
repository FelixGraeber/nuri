package com.nuri.feature.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nuri.core.model.HistoryItem
import com.nuri.core.model.MealHistoryItem
import com.nuri.core.model.WellbeingHistoryItem
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("History") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        when (uiState.screenState) {
            is HistoryScreenState.Loading -> {
                LoadingContent()
            }
            is HistoryScreenState.Content -> {
                HistoryContent(
                    historyItems = uiState.screenState.items
                )
            }
            is HistoryScreenState.Empty -> {
                EmptyContent()
            }
            is HistoryScreenState.Error -> {
                ErrorContent(
                    message = uiState.screenState.message,
                    onRetry = viewModel::loadHistory
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun HistoryContent(
    historyItems: List<HistoryItem>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = historyItems,
            key = { it.id }
        ) { item ->
            when (item) {
                is MealHistoryItem -> {
                    MealHistoryCard(item = item)
                }
                is WellbeingHistoryItem -> {
                    WellbeingHistoryCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun MealHistoryCard(
    item: MealHistoryItem
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Meal",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = item.timestamp.format(
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                AsyncImage(
                    model = item.meal.imagePath,
                    contentDescription = "Meal image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Ingredients:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val ingredientText = item.meal.ingredients
                        .take(3)
                        .joinToString(", ") { it.name }
                    
                    val displayText = if (item.meal.ingredients.size > 3) {
                        "$ingredientText +${item.meal.ingredients.size - 3} more"
                    } else {
                        ingredientText
                    }
                    
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun WellbeingHistoryCard(
    item: WellbeingHistoryItem
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Wellbeing",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = item.timestamp.format(
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WellbeingMetric(
                    label = "Mood",
                    value = item.wellbeing.moodScore,
                    icon = getMoodEmoji(item.wellbeing.moodScore)
                )
                
                WellbeingMetric(
                    label = "Energy",
                    value = item.wellbeing.energyLevel,
                    icon = getEnergyEmoji(item.wellbeing.energyLevel)
                )
            }

            item.wellbeing.note?.let { note ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun WellbeingMetric(
    label: String,
    value: Int,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "$value/10",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No history yet",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start by capturing a meal!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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

private fun getMoodEmoji(score: Int): String {
    return when (score) {
        in 1..2 -> "😢"
        in 3..4 -> "😕"
        in 5..6 -> "😐"
        in 7..8 -> "🙂"
        in 9..10 -> "😊"
        else -> "😐"
    }
}

private fun getEnergyEmoji(level: Int): String {
    return when (level) {
        in 1..3 -> "😴"
        in 4..6 -> "😌"
        in 7..8 -> "😊"
        in 9..10 -> "⚡"
        else -> "😌"
    }
} 