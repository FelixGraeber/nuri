package app.getnuri.results

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

// Extension function to convert ImageBitmap to Android Bitmap
fun ImageBitmap.toAndroidBitmap(): Bitmap = this.asAndroidBitmap()

// Extension function to convert Android Bitmap to ImageBitmap
fun Bitmap.toImageBitmap(): ImageBitmap = this.asImageBitmap()

// Function to check if layout allows full content
@Composable
fun allowsFullContent(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp > 600.dp
}

// Function to check if screen is at least medium size
@Composable
fun isAtLeastMedium(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp >= 600.dp
}

// Function to share an image
fun shareImage(context: Context, imageUri: Uri) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    
    val chooser = Intent.createChooser(shareIntent, "Share Image")
    context.startActivity(chooser)
}

// Placeholder drawable resource ID
object DrawableRes {
    val placeholderbot: Int = app.getnuri.results.R.drawable.placeholderbot
} 