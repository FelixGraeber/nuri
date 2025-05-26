 
package app.getnuri.camera

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.getnuri.theme.PrimaryContainer

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RearCameraButton(
    isRearCameraEnabled: Boolean = false,
    toggleRearCamera: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val actionLabel = stringResource(R.string.rear_camera_description)
    val colors = if (isRearCameraEnabled) {
        IconButtonDefaults.filledTonalIconButtonColors().copy(
            containerColor = PrimaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    } else {
        IconButtonDefaults.filledTonalIconButtonColors()
    }
    FilledTonalIconButton(
        onClick = toggleRearCamera,
        modifier = modifier
            .semantics {
                onClick(
                    label = actionLabel,
                    action = {
                        toggleRearCamera()
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
        colors = colors,
    ) {
        Icon(
            painterResource(R.drawable.outline_rear_camera),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}
