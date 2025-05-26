 
package app.getnuri.camera

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import app.getnuri.theme.AndroidifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CameraDirectionButton(
    flipCameraDirection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val actionLabel = stringResource(R.string.flip_camera_direction)
    FilledTonalIconButton(
        onClick = flipCameraDirection,
        modifier = modifier
            .semantics {
                onClick(
                    label = actionLabel,
                    action = {
                        flipCameraDirection()
                        true
                    },
                )
            }
            .size(
                IconButtonDefaults.mediumContainerSize(
                    IconButtonDefaults.IconButtonWidthOption.Narrow,
                ),
            ),
        shape = IconButtonDefaults.filledShape,

    ) {
        Icon(painterResource(R.drawable.outline_cameraswitch_24), contentDescription = null)
    }
}

@Preview
@Composable
fun CameraDirectionButtonPreview() {
    AndroidifyTheme {
        CameraDirectionButton(
            flipCameraDirection = {},
        )
    }
}
