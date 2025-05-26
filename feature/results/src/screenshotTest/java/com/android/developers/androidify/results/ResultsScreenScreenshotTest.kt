 
package app.getnuri.results

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.getnuri.theme.AndroidifyTheme
import app.getnuri.util.AdaptivePreview
import app.getnuri.util.SmallPhonePreview

class ResultsScreenScreenshotTest {

    @AdaptivePreview
    @Preview(showBackground = true)
    @Composable
    fun ResultsScreen_AdaptivePreview() {
        val mockBitmap = createMockBitmap()
        val state = remember {
            mutableStateOf(
                ResultState(
                    resultImageBitmap = mockBitmap,
                    promptText = "wearing a hat with straw hair",
                ),
            )
        }
        CompositionLocalProvider(value = LocalInspectionMode provides true) {
            AndroidifyTheme {
                ResultsScreenContents(
                    contentPadding = PaddingValues(0.dp),
                    state = state,
                    verboseLayout = true, // Replicates ResultsScreenPreview
                    downloadClicked = {},
                    shareClicked = {},
                )
            }
        }
    }

    @SmallPhonePreview
    @Preview(showBackground = true)
    @Composable
    fun ResultsScreen_SmallPreview() {
        val mockBitmap = createMockBitmap()
        val state = remember {
            mutableStateOf(
                ResultState(
                    resultImageBitmap = mockBitmap,
                    promptText = "wearing a hat with straw hair",
                ),
            )
        }
        CompositionLocalProvider(value = LocalInspectionMode provides true) {
            AndroidifyTheme {
                ResultsScreenContents(
                    contentPadding = PaddingValues(0.dp),
                    state = state,
                    verboseLayout = false, // Replicates ResultsScreenPreviewSmall
                    downloadClicked = {},
                    shareClicked = {},
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ResultsScreen_OriginalInputPreview() {
        val mockBitmap = createMockBitmap()
        val state = remember {
            mutableStateOf(
                ResultState(
                    resultImageBitmap = mockBitmap,
                    promptText = "wearing a hat with straw hair",
                ),
            )
        }
        CompositionLocalProvider(value = LocalInspectionMode provides true) {
            AndroidifyTheme {
                ResultsScreenContents(
                    contentPadding = PaddingValues(0.dp),
                    state = state,
                    verboseLayout = true,
                    downloadClicked = {},
                    shareClicked = {},
                    defaultSelectedResult = ResultOption.OriginalInput, // Set the non-default option
                )
            }
        }
    }

    // Helper function to create a consistent mock bitmap
    private fun createMockBitmap(): Bitmap {
        val width = 200
        val height = 200
        val mockBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mockBitmap)
        val paint = Paint()
        val gradient = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            Color.RED,
            Color.BLUE,
            Shader.TileMode.CLAMP,
        )
        paint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return mockBitmap
    }
}
