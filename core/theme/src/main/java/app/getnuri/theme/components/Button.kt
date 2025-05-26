 
package app.getnuri.theme.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface),
        modifier = modifier
            .heightIn(min = 64.dp)
            .animateContentSize(),
        onClick = onClick,
    ) {
        Row {
            if (!loading) {
                if (leadingIcon != null) {
                    leadingIcon()
                }
                if (buttonText != null) {
                    Text(buttonText, fontSize = 18.sp)
                }
                if (trailingIcon != null) {
                    trailingIcon()
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Composable
fun SecondaryOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    OutlinedButton(
        onClick,
        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.secondary),
        modifier = modifier.heightIn(min = 56.dp),
        border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.outline),
    ) {
        Row {
            if (leadingIcon != null) {
                leadingIcon()
            }
            if (buttonText != null) {
                Text(
                    buttonText,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }
}
