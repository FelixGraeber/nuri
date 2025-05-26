package app.getnuri.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.getnuri.theme.NuriTheme // Assuming your theme is NuriTheme

class PermissionsRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NuriTheme { // Replace with your actual theme if different
                Text(
                    text = "This activity explains why Nuri needs access to your health data. " +
                           "We use this data to help you identify patterns between your nutrition, " +
                           "symptoms, and overall wellbeing. Your data is processed locally and " +
                           "you have full control over it.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        // In a real app, you would finish the activity or provide more specific UI
        // For now, it just displays text.
    }
}
