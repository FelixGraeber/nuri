 
package app.getnuri.camera

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import app.getnuri.theme.Error
import app.getnuri.theme.Secondary
import app.getnuri.theme.Surface

private val TAP_TO_FOCUS_INDICATOR_SIZE = 48.dp

@Composable
internal fun CameraViewfinder(
  surfaceRequest: SurfaceRequest,
  autofocusUiState: AutofocusUiState,
  tapToFocus: (tapCoords: Offset) -> Unit,
  onScaleZoom: (zoomScaleFactor: Float) -> Unit,
  modifier: Modifier = Modifier,
) {
    val onScaleCurrentZoom by rememberUpdatedState(onScaleZoom)
    val currentTapToFocus by rememberUpdatedState(tapToFocus)
    val coordinateTransformer = remember { MutableCoordinateTransformer() }
    CameraXViewfinder(
        surfaceRequest = surfaceRequest,
        coordinateTransformer = coordinateTransformer,
        modifier = modifier
            .pointerInput(coordinateTransformer) {
                detectTapGestures { tapCoords ->
                    with(coordinateTransformer) {
                        currentTapToFocus(tapCoords.transform())
                    }
                }
            }
            .transformable(
                rememberTransformableState(
                    onTransformation = { zoomChange, _, _ ->
                        onScaleCurrentZoom(zoomChange)
                    },
                ),
            ),
    )

    if (autofocusUiState is AutofocusUiState.Specified) {
        // Show the autofocus indicator while the autofocus routine is running
        val showAutofocusIndicator = autofocusUiState.status == AutofocusUiState.Status.RUNNING
        // Map coordinates from surface coordinates back to screen coordinates
        val tapCoords =
            remember(coordinateTransformer.transformMatrix, autofocusUiState.surfaceCoordinates) {
                Matrix().run {
                    setFrom(coordinateTransformer.transformMatrix)
                    invert()
                    map(autofocusUiState.surfaceCoordinates)
                }
            }
        AnimatedVisibility(
            visible = showAutofocusIndicator,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .offset { tapCoords.round() }
                .offset(-TAP_TO_FOCUS_INDICATOR_SIZE / 2, -TAP_TO_FOCUS_INDICATOR_SIZE / 2),
        ) {
            Spacer(
                Modifier
                    .border(
                        2.dp,
                        when (autofocusUiState.status) {
                            AutofocusUiState.Status.SUCCESS -> Secondary
                            AutofocusUiState.Status.FAILURE -> Error
                            else -> Surface
                        },
                        CircleShape,
                    )
                    .size(TAP_TO_FOCUS_INDICATOR_SIZE),
            )
        }
    }
}
