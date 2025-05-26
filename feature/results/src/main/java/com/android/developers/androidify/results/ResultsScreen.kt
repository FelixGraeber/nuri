package app.getnuri.results

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.getnuri.theme.AndroidifyTheme

@Composable
fun ResultsScreen(
    resultImage: Bitmap,
    originalImageUri: Uri?,
    promptText: String?,
    modifier: Modifier = Modifier,
    verboseLayout: Boolean = allowsFullContent(),
    onBackPress: () -> Unit,
    onAboutPress: () -> Unit,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(resultImage, originalImageUri, promptText) {
        viewModel.setArguments(resultImage, originalImageUri, promptText)
    }
    val context = LocalContext.current
    LaunchedEffect(state.value.savedUri) {
        val savedImageUri = state.value.savedUri
        if (savedImageUri != null) {
            shareImage(context, savedImageUri)
            viewModel.clearSavedUri()
        }
    }
    val snackbarHostState by viewModel.snackbarHostState.collectAsStateWithLifecycle()
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(snackbarData, shape = SnackbarDefaults.shape)
                },
            )
        },
        topBar = {
            AndroidifyTopAppBar(
                backEnabled = false,
                aboutEnabled = true,
                isMediumWindowSize = isAtLeastMedium(),
                useNuriStyling = true,
                onAboutClicked = onAboutPress,
            )
        },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ) { contentPadding ->
        ResultsScreenContents(
            contentPadding,
            state,
            verboseLayout = verboseLayout,
            downloadClicked = {
                viewModel.downloadClicked()
            },
            shareClicked = {
                viewModel.shareClicked()
            },
        )
    }
}

@AdaptivePreview
@SmallPhonePreview
@Preview
@Composable
private fun ResultsScreenPreview() {
    AndroidifyTheme {
        val bitmap = ImageBitmap.imageResource(R.drawable.placeholderbot)
        val state = remember {
            mutableStateOf(
                ResultState(
                    resultImageBitmap = bitmap.asAndroidBitmap(),
                    promptText = "wearing a hat with straw hair",
                ),
            )
        }

        ResultsScreenContents(
            contentPadding = PaddingValues(0.dp),
            state = state,
            downloadClicked = {},
            shareClicked = {},
        )
    }
}

@SmallPhonePreview
@Composable
private fun ResultsScreenPreviewSmall() {
    AndroidifyTheme {
        val bitmap = ImageBitmap.imageResource(R.drawable.placeholderbot)
        val state = remember {
            mutableStateOf(
                ResultState(
                    resultImageBitmap = bitmap.asAndroidBitmap(),
                    promptText = "wearing a hat with straw hair",
                ),
            )
        }

        ResultsScreenContents(
            contentPadding = PaddingValues(0.dp),
            state = state,
            verboseLayout = false,
            downloadClicked = {},
            shareClicked = {},
        )
    }
}
