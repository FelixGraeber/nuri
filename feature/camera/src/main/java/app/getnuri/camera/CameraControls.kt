 
package app.getnuri.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.getnuri.theme.AndroidifyTheme

@Composable
internal fun CameraControls(
    captureImageClicked: () -> Unit,
    canFlipCamera: Boolean,
    flipCameraDirectionClicked: () -> Unit,
    detectedPose: Boolean,
    defaultZoomOptions: List<Float>,
    zoomLevel: () -> Float,
    onZoomLevelSelected: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ZoomToolbar(
            defaultZoomOptions = defaultZoomOptions,
            zoomLevel = zoomLevel,
            onZoomLevelSelected = onZoomLevelSelected
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (canFlipCamera) {
                    CameraDirectionButton(
                        flipCameraDirection = flipCameraDirectionClicked,
                    )
                }
            }
            CameraCaptureButton(
                captureImageClicked = captureImageClicked,
                enabled = detectedPose,
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
private fun CameraControlsPreview() {
    AndroidifyTheme {
        CameraControls(
            captureImageClicked = { },
            canFlipCamera = true,
            flipCameraDirectionClicked = { },
            detectedPose = true,
            zoomLevel = {0.4f},
            onZoomLevelSelected = {},
            defaultZoomOptions = listOf(.6f, 1f),
        )
    }
}
