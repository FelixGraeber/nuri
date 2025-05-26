 
package app.getnuri.baselineprofile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.textAsString
import androidx.test.uiautomator.uiAutomator
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
@RequiresApi(Build.VERSION_CODES.P)
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.collect(
            packageName = "app.getnuri",
            // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
            stableIterations = 3,
            includeInStartupProfile = true,
        ) {
            uiAutomator {
                startApp(packageName = packageName)
                onView { textAsString() == "Let's Go" }.click()
                onView { textAsString() == "Prompt" }.click()
                onView { isEditable }.apply {
                    click()
                    text =
                        "wearing brown sneakers, a red t-shirt, " +
                        "big sunglasses and sports a purple mohawk."
                }
            }
        }
    }
}
