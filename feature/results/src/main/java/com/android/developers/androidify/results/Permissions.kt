 
@file:OptIn(ExperimentalPermissionsApi::class)

package app.getnuri.results

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@Composable
fun PermissionRationaleDialog(
    showRationaleDialog: Boolean,
    onDismiss: () -> Unit,
    externalStoragePermission: PermissionState,
) {
    if (showRationaleDialog) {
        AlertDialog(
            title = {
                Text(text = stringResource(R.string.grant_storage_permission))
            },
            text = {
                Text(text = stringResource(R.string.to_save_your_bot_to_the_device_you_need_to_grant_storage_permission))
            },
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        externalStoragePermission.launchPermissionRequest()
                    },
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    },
                ) {
                    Text(stringResource(R.string.dismiss))
                }
            },
        )
    }
}
