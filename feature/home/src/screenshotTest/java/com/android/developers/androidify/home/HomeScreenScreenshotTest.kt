 
package app.getnuri.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.getnuri.theme.AndroidifyTheme
import app.getnuri.theme.SharedElementContextPreview
import app.getnuri.util.AdaptivePreview
import app.getnuri.util.isAtLeastMedium

class HomeScreenScreenshotTest {

    @AdaptivePreview
    @Preview(showBackground = true)
    @Composable
    fun HomeScreenScreenshot() {
        AndroidifyTheme {
            SharedElementContextPreview {
                HomeScreenContents(
                    isMediumWindowSize = isAtLeastMedium(),
                    onClickLetsGo = { },
                    onAboutClicked = {},
                    videoLink = "",
                    dancingBotLink = "",
                )
            }
        }
    }
}
