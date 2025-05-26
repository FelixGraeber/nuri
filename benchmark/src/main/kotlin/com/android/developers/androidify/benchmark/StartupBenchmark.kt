 
package app.getnuri.benchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.PowerMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.uiAutomator
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@RequiresApi(Build.VERSION_CODES.Q)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupNoPrecompilation() = startup(CompilationMode.None())

    @Test
    fun startupBaselineProfile() = startup(CompilationMode.DEFAULT)

    @Test
    fun startupFullCompilation() = startup(CompilationMode.Full())

    @OptIn(ExperimentalMetricApi::class)
    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "app.getnuri",
        metrics = listOf(
            StartupTimingMetric(),
            FrameTimingMetric(),
            MemoryUsageMetric(MemoryUsageMetric.Mode.Max),
            PowerMetric(PowerMetric.Type.Power()),
        ),
        iterations = 10,
        compilationMode = compilationMode,
        startupMode = StartupMode.COLD,
    ) {
        uiAutomator {
            startApp(packageName = packageName)
        }
    }
}
