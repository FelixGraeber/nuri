 
package app.getnuri.camera

import android.graphics.Rect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.layout.FoldingFeature
import app.getnuri.theme.NuriTheme

class CameraScreenScreenshotTest {

    // Helper mock for FoldingFeature
    private class MockFoldingFeature(
        override val state: FoldingFeature.State,
        override val orientation: FoldingFeature.Orientation,
        override val occlusionType: FoldingFeature.OcclusionType = FoldingFeature.OcclusionType.NONE,
        override val isSeparating: Boolean = true,
        override val bounds: Rect = Rect(),
    ) : FoldingFeature

    @Preview(showBackground = true, name = "Default State")
    @Composable
    fun CameraScreenScreenshot() {
        NuriTheme {
            StatelessCameraPreviewContent(
                viewfinder = { modifier ->
                    Box(
                        modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Red,
                                    Color.Green,
                                    Color.Blue,
                                ),
                            ),
                        ),
                    )
                },
                detectedPose = true,
                canFlipCamera = true,
                requestFlipCamera = {},
                defaultZoomOptions = listOf(1f),
                zoomLevel = { 1f },
                onAnimateZoom = {},
                requestCaptureImage = {},
            )
        }
    }

    @Preview(showBackground = true, name = "Pose Not Detected")
    @Composable
    fun CameraScreenPoseNotDetectedScreenshot() {
        NuriTheme {
            StatelessCameraPreviewContent(
                viewfinder = { modifier -> ViewfinderPlaceholder(modifier) },
                detectedPose = false, // Changed
                canFlipCamera = true,
                requestFlipCamera = {},
                defaultZoomOptions = listOf(1f),
                zoomLevel = { 1f },
                onAnimateZoom = {},
                requestCaptureImage = {},
            )
        }
    }

    @Preview(showBackground = true, name = "Cannot Flip Camera")
    @Composable
    fun CameraScreenCannotFlipScreenshot() {
        NuriTheme {
            StatelessCameraPreviewContent(
                viewfinder = { modifier -> ViewfinderPlaceholder(modifier) },
                detectedPose = true,
                canFlipCamera = false, // Changed
                requestFlipCamera = {},
                defaultZoomOptions = listOf(1f),
                zoomLevel = { 1f },
                onAnimateZoom = {},
                requestCaptureImage = {},
            )
        }
    }

    @Preview(showBackground = true, name = "Rear Camera Button (Disabled)")
    @Composable
    fun CameraScreenRearCamDisabledScreenshot() {
        NuriTheme {
            StatelessCameraPreviewContent(
                viewfinder = { modifier -> ViewfinderPlaceholder(modifier) },
                detectedPose = true,
                canFlipCamera = true,
                requestFlipCamera = {},
                defaultZoomOptions = listOf(1f),
                zoomLevel = { 1f },
                onAnimateZoom = {},
                requestCaptureImage = {},
                shouldShowRearCameraFeature = { true }, // Changed
                isRearCameraEnabled = false, // Changed
                toggleRearCameraFeature = {},
            )
        }
    }

    @Preview(showBackground = true, name = "Rear Camera Button (Enabled)")
    @Composable
    fun CameraScreenRearCamEnabledScreenshot() {
        NuriTheme {
            StatelessCameraPreviewContent(
                viewfinder = { modifier -> ViewfinderPlaceholder(modifier) },
                detectedPose = true,
                canFlipCamera = true,
                requestFlipCamera = {},
                defaultZoomOptions = listOf(1f),
                zoomLevel = { 1f },
                onAnimateZoom = {},
                requestCaptureImage = {},
                shouldShowRearCameraFeature = { true }, // Changed
                isRearCameraEnabled = true, // Changed
                toggleRearCameraFeature = {},
            )
        }
    }

    @Preview(showBackground = true, name = "Tabletop Mode", widthDp = 800, heightDp = 800)
    @Composable
    fun CameraScreenTabletopScreenshot() {
        val tabletopFoldingFeature = MockFoldingFeature(
            state = FoldingFeature.State.HALF_OPENED,
            orientation = FoldingFeature.Orientation.HORIZONTAL,
        )
        NuriTheme {
            StatelessCameraPreviewContent(
                viewfinder = { modifier -> ViewfinderPlaceholder(modifier) },
                detectedPose = true,
                canFlipCamera = true,
                requestFlipCamera = {},
                defaultZoomOptions = listOf(1f),
                zoomLevel = { 1f },
                onAnimateZoom = {},
                requestCaptureImage = {},
                foldingFeature = tabletopFoldingFeature, // Changed
            )
        }
    }

    @Preview(showBackground = true, name = "Medium Horizontal", widthDp = 840, heightDp = 480)
    @Composable
    fun CameraScreenMediumHorizontalScreenshot() {
        NuriTheme {
            StatelessCameraPreviewContent(
                viewfinder = { modifier -> ViewfinderPlaceholder(modifier) },
                detectedPose = true,
                canFlipCamera = true,
                requestFlipCamera = {},
                defaultZoomOptions = listOf(1f),
                zoomLevel = { 1f },
                onAnimateZoom = {},
                requestCaptureImage = {},
            )
        }
    }

    @Preview(showBackground = true, name = "Subcompact Horizontal", widthDp = 600, heightDp = 400)
    @Composable
    fun CameraScreenSubcompactHorizontalScreenshot() {
        NuriTheme {
            StatelessCameraPreviewContent(
                viewfinder = { modifier -> ViewfinderPlaceholder(modifier) },
                detectedPose = true,
                canFlipCamera = true,
                requestFlipCamera = {},
                defaultZoomOptions = listOf(1f),
                zoomLevel = { 1f },
                onAnimateZoom = {},
                requestCaptureImage = {},
            )
        }
    }

    // Helper composable for placeholder viewfinder
    @Composable
    private fun ViewfinderPlaceholder(modifier: Modifier = Modifier) {
        Box(
            modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF0000),
                        Color(0xFF7D299B),
                        Color(0xFF1854CC),
                    ),
                ),
            ),
        )
    }
}
