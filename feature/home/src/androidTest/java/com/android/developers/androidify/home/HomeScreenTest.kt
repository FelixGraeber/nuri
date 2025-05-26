 
package app.getnuri.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.IntOffset
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.getnuri.theme.SharedElementContextPreview
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun clickingLetsGo_invokesCallback() {
        var wasClicked = false
        val letsGoButtonLabel = composeTestRule.activity.getString(R.string.home_button_label)

        composeTestRule.setContent {
            // Directly render HomeScreenContents, passing a lambda to track the click

            SharedElementContextPreview {
                HomeScreenContents(
                    isMediumWindowSize = false, // Provide a default or mock value
                    onClickLetsGo = { offset: IntOffset -> // Match the lambda signature
                        wasClicked = true
                    },
                    onAboutClicked = {}, // Provide a default or mock value,
                    videoLink = "",
                    dancingBotLink = "",
                )
            }
        }

        // Find the "Lets Go" button and click it
        composeTestRule.onNodeWithText(letsGoButtonLabel).performClick()

        // Assert that the lambda was invoked
        assertTrue("The onClickLetsGo lambda should have been called", wasClicked)
    }

    @Test
    fun transcriptVisibilityAfterSwipe() {
        val transcriptContentDesc = composeTestRule.activity.getString(R.string.instruction_video_transcript)
        val firstPageText = composeTestRule.activity.getString(R.string.customize_your_own)

        composeTestRule.setContent {
            SharedElementContextPreview {
                HomeScreenContents(
                    isMediumWindowSize = false, // Ensure compact mode for pager
                    onClickLetsGo = { },
                    onAboutClicked = {},
                    videoLink = "",
                    dancingBotLink = "",
                )
            }
        }

        // 1. Verify transcript is not visible initially
        composeTestRule.onNodeWithContentDescription(transcriptContentDesc).assertIsNotDisplayed()

        // 2. Swipe left on the pager (using text from the first page)
        composeTestRule.onNodeWithText(firstPageText, substring = true).performTouchInput { swipeLeft() }

        // 3. Verify transcript is now visible
        composeTestRule.onNodeWithContentDescription(transcriptContentDesc).assertIsDisplayed()
    }
}
