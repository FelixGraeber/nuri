 
package app.getnuri.camera

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.material3.toPath
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.getnuri.theme.AndroidifyTheme
import app.getnuri.theme.Primary
import app.getnuri.theme.PrimaryContainer
import app.getnuri.theme.PrimaryFixed
import app.getnuri.theme.Secondary
import app.getnuri.theme.components.ScaleIndicationNodeFactory

@Preview
@Composable
private fun CameraCaptureButtonPreview() {
    AndroidifyTheme {
        Row {
            CameraCaptureButton(enabled = false)
            CameraCaptureButton()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CameraCaptureButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    captureImageClicked: () -> Unit = {},
) {
    val size = 100.dp
    val sizePx = with(LocalDensity.current) { size.toPx() }
    val outerSizePx = with(LocalDensity.current) { 96.dp.toPx() }
    val innerSizePx = with(LocalDensity.current) { 82.dp.toPx() }
    val borderWidth = with(LocalDensity.current) { 4.dp.toPx() }
    val path = MaterialShapes.Cookie9Sided.toPath()

    val disabledColor = MaterialTheme.colorScheme.surfaceContainer
    val interactionSource = remember { MutableInteractionSource() }
    val animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    Spacer(
        modifier
            .indication(interactionSource, ScaleIndicationNodeFactory(animationSpec))
            .clip(MaterialShapes.Cookie9Sided.toShape())
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White),
                enabled = enabled,
                onClick = captureImageClicked,
                role = Role.Button,
                onClickLabel = stringResource(R.string.cd_capture_button),
            )
            .size(size)
            .drawWithCache {
                val outerPath = path.copy().apply {
                    transform(
                        Matrix().apply {
                            resetToPivotedTransform(
                                pivotX = 0.5f,
                                pivotY = 0.5f,
                                translationX = sizePx / 2f,
                                translationY = sizePx / 2f,
                                scaleX = outerSizePx,
                                scaleY = outerSizePx,
                            )
                        },
                    )
                }
                val innerPath = path.copy().apply {
                    transform(
                        Matrix().apply {
                            resetToPivotedTransform(
                                pivotX = 0.5f,
                                pivotY = 0.5f,
                                translationX = sizePx / 2f,
                                translationY = sizePx / 2f,
                                scaleX = innerSizePx,
                                scaleY = innerSizePx,
                            )
                        },
                    )
                }
                val outerBrush = Brush.linearGradient(listOf(Secondary, PrimaryContainer, Primary))
                val innerBrush = Brush.linearGradient(listOf(Secondary, PrimaryFixed, Secondary))
                onDrawBehind {
                    if (enabled) {
                        drawPath(outerPath, outerBrush, style = Stroke(borderWidth))
                        drawPath(innerPath, innerBrush)
                    } else {
                        drawPath(outerPath, disabledColor, style = Stroke(borderWidth))
                        drawPath(innerPath, disabledColor)
                    }
                }
            },
    )
}
