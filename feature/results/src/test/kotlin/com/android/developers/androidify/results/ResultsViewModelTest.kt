 
@file:OptIn(ExperimentalCoroutinesApi::class)

package app.getnuri.results

import android.graphics.Bitmap
import android.net.Uri
import com.android.developers.testing.repository.FakeImageGenerationRepository
import com.android.developers.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ResultsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ResultsViewModel

    private val fakeBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    private val fakePromptText = "Pink Hair, plaid shirt, jeans"
    private val originalFakeUri = Uri.parse("content://com.example.app/images/original.jpg")

    @Before
    fun setup() {
        viewModel = ResultsViewModel(
            FakeImageGenerationRepository(),
            UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun stateInitialEmpty() = runTest {
        assertEquals(
            ResultState(),
            viewModel.state.value,
        )
    }

    @Test
    fun setArgumentsWithOriginalImage() = runTest {
        viewModel.setArguments(
            fakeBitmap,
            originalFakeUri,
            promptText = null,
        )
        assertEquals(
            ResultState(
                resultImageBitmap = fakeBitmap,
                originalImageUrl = originalFakeUri,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun setArgumentsWithPrompt() = runTest {
        viewModel.setArguments(
            fakeBitmap,
            null,
            promptText = fakePromptText,
        )
        assertEquals(
            ResultState(
                resultImageBitmap = fakeBitmap,
                originalImageUrl = null,
                promptText = fakePromptText,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun downloadClicked() = runTest {
        val values = mutableListOf<ResultState>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.state.collect {
                values.add(it)
            }
        }

        viewModel.setArguments(
            fakeBitmap,
            originalFakeUri,
            promptText = null,
        )

        viewModel.downloadClicked()
        assertNotNull(values.last().externalOriginalSavedUri)
        assertEquals(
            originalFakeUri,
            values.last().externalOriginalSavedUri,
        )
    }

    @Test
    fun shareClicked() = runTest {
        val values = mutableListOf<ResultState>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.state.collect {
                values.add(it)
            }
        }
        viewModel.setArguments(
            fakeBitmap,
            originalFakeUri,
            promptText = null,
        )

        viewModel.shareClicked()
        assertNotNull(values.last().savedUri)
    }
}
