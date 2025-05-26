package app.getnuri.results

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun ResultsScreenContents(
    contentPadding: PaddingValues,
    state: State<ResultState>,
    verboseLayout: Boolean = allowsFullContent(),
    downloadClicked: () -> Unit,
    shareClicked: () -> Unit,
) {
    val showResult = state.value.resultImageBitmap != null
    var selectedResultOption by remember {
        mutableStateOf(ResultOption.WellbeingCharts)
    }
    val wasPromptUsed = state.value.originalImageUrl == null

    // Display only the WellbeingCharts section and remove other sections.
    val mainContent = @Composable { modifier: Modifier ->
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(300, delayMillis = 500)) + slideInVertically(
                tween(1000, easing = EaseOutBack, delayMillis = 500),
                initialOffsetY = { fullHeight -> fullHeight },
            ),
        ) {
            WellbeingChartsSection(
                wellbeingData = state.value.wellbeingData,
                modifier = modifier
            )
        }
    }

    // Remove or comment out sections related to promptToolbar, buttonRow, and backgroundQuotes

    // Draw the actual content
    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        mainContent(Modifier)
    }
}
