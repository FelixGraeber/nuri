@file:OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
)

package app.getnuri.feature.wellbeing

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.getnuri.theme.*
import app.getnuri.theme.components.AndroidifyTopAppBar
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WellbeingScreen(
    navigationPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    viewModel: WellbeingViewModel = hiltViewModel()
) {
    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    var customSymptoms by remember { mutableStateOf(listOf<String>()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AndroidifyTopAppBar(
                backEnabled = false,
                aboutEnabled = true,
                useNuriStyling = true,
                onAboutClicked = onBackPressed,
            )
        },
        containerColor = Primary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        val combinedPadding = PaddingValues(
            start = maxOf(paddingValues.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr), navigationPadding.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr)),
            top = maxOf(paddingValues.calculateTopPadding(), navigationPadding.calculateTopPadding()),
            end = maxOf(paddingValues.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr), navigationPadding.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr)),
            bottom = maxOf(paddingValues.calculateBottomPadding(), navigationPadding.calculateBottomPadding())
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(combinedPadding)
        ) {
            TrackNowContent(
                selectedSymptoms = selectedSymptoms,
                customSymptoms = customSymptoms,
                onSymptomsChanged = { selectedSymptoms = it },
                onCustomSymptomsChanged = { customSymptoms = it },
                onSubmit = { moodIdx, energyIdx, symptoms, notes, cb ->
                    viewModel.submitFeedback(
                        moodScore = moodIdx.takeIf { it >= 0 }?.plus(1),
                        energyScore = energyIdx.takeIf { it >= 0 }?.plus(1),
                        symptoms = symptoms.toList(),
                        notes = notes
                    ) { success ->
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (success) "Entry saved 🎉" else "Please log a meal first"
                            )
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TrackNowContent(
    selectedSymptoms: Set<String>,
    customSymptoms: List<String>,
    onSymptomsChanged: (Set<String>) -> Unit,
    onCustomSymptomsChanged: (List<String>) -> Unit,
    onSubmit: (
        moodIdx: Int,
        energyIdx: Int,
        symptoms: Set<String>,
        notes: String,
        cb: (Boolean) -> Unit
    ) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp), // Use contentPadding instead
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "How are you feeling",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnPrimary
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    "after your meal?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnPrimary
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        item {
            MoodTracker()
        }

        item {
            EnergyTracker()
        }

        item {
            SymptomTracker(
                selectedSymptoms = selectedSymptoms,
                customSymptoms = customSymptoms,
                onSymptomsChanged = onSymptomsChanged,
                onCustomSymptomsChanged = onCustomSymptomsChanged
            )
        }

        item {
            NotesSection()
        }

        item {
            // Enhanced Submit Button
            Button(
                onClick = {
                    onSubmit(-1, -1, selectedSymptoms, "", { _ -> })
                    onSymptomsChanged(emptySet())
                    onCustomSymptomsChanged(emptyList())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedSymptoms.isNotEmpty()) Secondary else SurfaceContainerHigh,
                    contentColor = if (selectedSymptoms.isNotEmpty()) OnSecondary else OnSurfaceVariant
                ),
                shape = RoundedCornerShape(36.dp),
                enabled = selectedSymptoms.isNotEmpty(),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (selectedSymptoms.isNotEmpty()) 8.dp else 2.dp
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Filled.Save,
                        contentDescription = "Save",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        if (selectedSymptoms.isNotEmpty()) "Submit Entry" else "Select symptoms to continue",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodTracker() {
    val moodEmojis = listOf("😔", "😞", "😐", "😊", "😄")
    val moodLabels = listOf("Poor", "Low", "Okay", "Good", "Great")
    var selectedMood by remember { mutableIntStateOf(-1) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceBright,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Mood",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                moodEmojis.forEachIndexed { index, emoji ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FilterChip(
                            onClick = { selectedMood = index },
                            label = { 
                                Text(
                                    emoji,
                                    fontSize = 28.sp
                                )
                            },
                            selected = selectedMood == index,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Secondary,
                                selectedLabelColor = OnSecondary,
                                containerColor = SurfaceContainerLow,
                                labelColor = OnSurface
                            ),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            moodLabels[index],
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedMood == index) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedMood == index) 
                                Secondary 
                            else OnSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnergyTracker() {
    val energyIcons = listOf(
        Icons.Filled.BatteryAlert,
        Icons.Filled.Battery1Bar,
        Icons.Filled.Battery3Bar,
        Icons.Filled.Battery6Bar,
        Icons.Filled.BatteryFull
    )
    val energyLabels = listOf("Drained", "Low", "Okay", "Good", "Energized")
    var selectedEnergy by remember { mutableIntStateOf(-1) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceBright,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Energy Level",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                energyIcons.forEachIndexed { index, icon ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FilterChip(
                            onClick = { selectedEnergy = index },
                            label = { 
                                Icon(
                                    icon,
                                    contentDescription = energyLabels[index],
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            selected = selectedEnergy == index,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Tertiary,
                                selectedLabelColor = OnTertiary,
                                containerColor = SurfaceContainerLow,
                                labelColor = OnSurface
                            ),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            energyLabels[index],
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedEnergy == index) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedEnergy == index) 
                                Tertiary 
                            else OnSurface
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SymptomTracker(
    selectedSymptoms: Set<String>,
    customSymptoms: List<String>,
    onSymptomsChanged: (Set<String>) -> Unit,
    onCustomSymptomsChanged: (List<String>) -> Unit
) {
    val predefinedSymptoms = listOf(
        "Bloating", "Headache/Migraine", "Nausea", "Skin Issues",
        "Fatigue/Brain Fog", "Stomach Pain/Cramps", "Diarrhea"
    )
    val allSymptoms = predefinedSymptoms + customSymptoms
    
    var customSymptomText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceBright,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                "Symptoms",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Primary
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            
            // Material 3 Button Group Layout - closer together, larger buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Group symptoms into rows of 2
                allSymptoms.chunked(2).forEach { rowSymptoms ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowSymptoms.forEach { symptom ->
                            val isSelected = selectedSymptoms.contains(symptom)
                            Button(
                                onClick = { 
                                    onSymptomsChanged(
                                        if (isSelected) {
                                            selectedSymptoms - symptom
                                        } else {
                                            selectedSymptoms + symptom
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Secondary else SurfaceContainerLow,
                                    contentColor = if (isSelected) OnSecondary else OnSurface
                                ),
                                shape = if (isSelected) RoundedCornerShape(8.dp) else RoundedCornerShape(28.dp),
                                border = if (!isSelected) BorderStroke(1.dp, Outline) else null
                            ) {
                                Text(
                                    symptom,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        
                        // Fill empty space if odd number of symptoms in last row
                        if (rowSymptoms.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Custom symptom input
            OutlinedTextField(
                value = customSymptomText,
                onValueChange = { customSymptomText = it },
                placeholder = { Text("Add custom symptom...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (customSymptomText.isNotBlank() && customSymptomText !in allSymptoms) {
                            onCustomSymptomsChanged(customSymptoms + customSymptomText.trim())
                            onSymptomsChanged(selectedSymptoms + customSymptomText.trim())
                            customSymptomText = ""
                            keyboardController?.hide()
                        }
                    }
                ),
                trailingIcon = {
                    if (customSymptomText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                if (customSymptomText.isNotBlank() && customSymptomText !in allSymptoms) {
                                    onCustomSymptomsChanged(customSymptoms + customSymptomText.trim())
                                    onSymptomsChanged(selectedSymptoms + customSymptomText.trim())
                                    customSymptomText = ""
                                    keyboardController?.hide()
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add symptom")
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun NotesSection() {
    var notes by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceBright,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                "Additional Notes",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text("How are you feeling? Any other symptoms or observations?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun WellbeingScreenPreview() {
    NuriTheme {
        WellbeingScreen()
    }
} 