 
package app.getnuri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import app.getnuri.navigation.MainNavigation
import app.getnuri.theme.AndroidifyTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterial3ExpressiveApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidifyTheme {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.light(
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb(),
                    ),
                    navigationBarStyle = SystemBarStyle.light(
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb(),
                    ),
                )
                MainNavigation()
            }
        }
    }
}
