 @file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package app.getnuri.camera

import android.Manifest
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.camera.core.SurfaceRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.rectangle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.layout.FoldingFeature
import app.getnuri.theme.LocalSharedTransitionScope
import app.getnuri.theme.SharedElementKey
import app.getnuri.theme.sharedBoundsRevealWithShapeMorph
import app.getnuri.theme.sharedBoundsWithDefaults
import app.getnuri.util.calculateCorrectAspectRatio
import app.getnuri.util.isTableTopPosture
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    onImageCaptured: (Uri) -> Unit,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    with(sharedTransitionScope) {
        Box(
            modifier
                .fillMaxSize()
                .sharedBoundsRevealWithShapeMorph(
                    rememberSharedContentState(SharedElementKey.CameraButtonToFullScreenCamera),
                    targetShape = MaterialShapes.Cookie9Sided,
                    restingShape = RoundedPolygon.rectangle().normalized(),
                    targetValueByState = {
                        when (it) {
                            EnterExitState.PreEnter -> 1f
                            EnterExitState.Visible -> 0f
                            EnterExitState.PostExit -> 1f
                        }
                    },
                )
                .sharedBoundsWithDefaults(rememberSharedContentState(SharedElementKey.CaptureImageToDetails)),
        ) {
            val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
            if (cameraPermissionState.status.isGranted) {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val activity = LocalActivity.current as ComponentActivity
                val foldingFeature by viewModel.foldingFeature.collectAsState()

                LaunchedEffect(uiState.imageUri) {
                    // this is used to navigate to the next screen, when an image is captured, it signals to NavController that we should do something with the URI,
                    // once the signal has been sent, the value is set to null to not trigger navigation over and over again.
                    val uri = uiState.imageUri
                    if (uri != null) {
                        onImageCaptured(uri)
                        viewModel.setCapturedImage(null)
                    }
                }
                val scope = rememberCoroutineScope()
                LifecycleStartEffect(viewModel) {
                    val job = scope.launch { viewModel.bindToCamera() }
                    onStopOrDispose { job.cancel() }
                }

                LaunchedEffect(Unit) {
                    viewModel.calculateFoldingFeature(activity)
                    viewModel.initRearDisplayFeature(activity)
                }

                uiState.surfaceRequest?.let { surface ->
                    CameraPreviewContent(
                        modifier = Modifier.fillMaxSize(),
                        surfaceRequest = surface,
                        autofocusUiState = uiState.autofocusUiState,
                        tapToFocus = viewModel::tapToFocus,
                        defaultZoomOptions = uiState.zoomOptions,
                        requestFlipCamera = viewModel::flipCameraDirection,
                        canFlipCamera = uiState.canFlipCamera,
                        requestCaptureImage = viewModel::captureImage,
                        zoomRange = uiState.zoomMinRatio..uiState.zoomMaxRatio,
                        zoomLevel = { uiState.zoomLevel },
                        onChangeZoomLevel = viewModel::setZoomLevel,
                        shouldShowRearCameraFeature = viewModel::shouldShowRearDisplayFeature,
                        toggleRearCameraFeature = { viewModel.toggleRearDisplayFeature(activity) },
                        isRearCameraEnabled = uiState.isRearCameraActive,
                        cameraSessionId = uiState.cameraSessionId
                    )
                }
            } else {
                CameraPermissionGrant(
                    launchPermissionRequest = {
                        cameraPermissionState.launchPermissionRequest()
                    },
                    showRationale = cameraPermissionState.status.shouldShowRationale,
                )
            }
        }
    }
}

@Composable
private fun CameraPermissionGrant(
    launchPermissionRequest: () -> Unit,
    showRationale: Boolean,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(showRationale) {
        if (!showRationale) {
            launchPermissionRequest()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .widthIn(max = 480.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.camera_permission_rationale), textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { launchPermissionRequest() }) {
            Text(stringResource(R.string.camera_permission_grant_button))
        }
    }
}

/**
 * A simplified version of the Camera Preview Content without guidance UI.
 * It accepts a generic viewfinder composable slot and provides basic camera controls.
 */
@Composable
fun StatelessCameraPreviewContent(
    viewfinder: @Composable (Modifier) -> Unit,
    canFlipCamera: Boolean,
    requestFlipCamera: () -> Unit,
    defaultZoomOptions: List<Float>,
    zoomLevel: () -> Float,
    onAnimateZoom: (Float) -> Unit,
    requestCaptureImage: () -> Unit,
    modifier: Modifier = Modifier,
    shouldShowRearCameraFeature: () -> Boolean = { false },
    toggleRearCameraFeature: () -> Unit = {},
    isRearCameraEnabled: Boolean = false,
) {
    var aspectRatio by remember { mutableFloatStateOf(9f / 16f) }
    val emptyComposable: @Composable (Modifier) -> Unit = {}
    
    val rearCameraButton: @Composable (Modifier) -> Unit = { rearModifier ->
        RearCameraButton(
            isRearCameraEnabled = isRearCameraEnabled,
            toggleRearCamera = toggleRearCameraFeature,
            modifier = rearModifier,
        )
    }

    CameraLayout(
        viewfinder = viewfinder,
        captureButton = { captureModifier ->
            CameraCaptureButton(
                modifier = captureModifier,
                enabled = true, // Always enable capture button
                captureImageClicked = requestCaptureImage,
            )
        },
        flipCameraButton = { flipModifier ->
            if (canFlipCamera) {
                CameraDirectionButton(
                    flipCameraDirection = requestFlipCamera,
                    modifier = flipModifier,
                )
            } else {
                emptyComposable(flipModifier)
            }
        },
        zoomButton = { zoomModifier ->
            // Create a proper composable function for the zoom toolbar
            @Composable
            fun ZoomToolbarContent() {
                ZoomToolbar(
                    defaultZoomOptions = defaultZoomOptions,
                    onZoomLevelSelected = { zoom -> onAnimateZoom(zoom) },
                    modifier = zoomModifier,
                    zoomLevel = zoomLevel,
                )
            }
            ZoomToolbarContent()
        },
        rearCameraButton = if (shouldShowRearCameraFeature()) rearCameraButton else emptyComposable,
        guide = { /* Default empty guide */ },
        guideText = { /* Default empty guide text */ },
        supportsTabletop = false,
        isTabletop = false,
        modifier = modifier.onSizeChanged { size ->
            if (size.height > 0) {
                aspectRatio = calculateCorrectAspectRatio(size.height, size.width, aspectRatio)
            }
        }
    )
}

/**
 * Displays the camera preview and controls. This version is stateful and interacts
 * directly with CameraX components like SurfaceRequest. It now delegates the layout
 * to StatelessCameraPreviewContent.
 */
@Composable
private fun CameraPreviewContent(
    surfaceRequest: SurfaceRequest,
    autofocusUiState: AutofocusUiState,
    tapToFocus: (Offset) -> Unit,
    cameraSessionId: Int,
    canFlipCamera: Boolean,
    requestFlipCamera: () -> Unit,
    defaultZoomOptions: List<Float>,
    zoomRange: ClosedFloatingPointRange<Float>,
    zoomLevel: () -> Float,
    onChangeZoomLevel: (zoomLevel: Float) -> Unit,
    requestCaptureImage: () -> Unit,
    modifier: Modifier = Modifier,
    shouldShowRearCameraFeature: () -> Boolean = { false },
    toggleRearCameraFeature: () -> Unit = {},
    isRearCameraEnabled: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val zoomState = remember(cameraSessionId) {
        ZoomState(
            initialZoomLevel = zoomLevel(),
            onChangeZoomLevel = onChangeZoomLevel,
            zoomRange = zoomRange,
        )
    }
    
    // Delegate the layout to the stateless version
    StatelessCameraPreviewContent(
        viewfinder = { viewfinderModifier ->
            // Provide the actual CameraViewfinder implementation for the slot
            var aspectRatio by remember { mutableFloatStateOf(9f / 16f) }
            CameraViewfinder(
                surfaceRequest = surfaceRequest,
                autofocusUiState = autofocusUiState,
                tapToFocus = tapToFocus,
                onScaleZoom = { scope.launch { zoomState.scaleZoom(it) } },
                modifier = viewfinderModifier.onSizeChanged { size ->
                    if (size.height > 0) {
                        aspectRatio = calculateCorrectAspectRatio(size.height, size.width, aspectRatio)
                    }
                },
            )
        },
        // Pass down all other state and callbacks
        canFlipCamera = canFlipCamera,
        requestFlipCamera = requestFlipCamera,
        defaultZoomOptions = defaultZoomOptions,
        zoomLevel = zoomLevel,
        onAnimateZoom = { scope.launch { zoomState.animatedZoom(it) } },
        requestCaptureImage = requestCaptureImage,
        shouldShowRearCameraFeature = shouldShowRearCameraFeature,
        toggleRearCameraFeature = toggleRearCameraFeature,
        isRearCameraEnabled = isRearCameraEnabled,
        modifier = modifier,
    )
}
