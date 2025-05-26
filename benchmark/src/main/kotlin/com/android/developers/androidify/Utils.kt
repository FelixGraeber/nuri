 
package app.getnuri

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.TraceSectionMetric

@OptIn(ExperimentalMetricApi::class)
val jitCompilationMetric = TraceSectionMetric("JIT Compiling %", label = "JIT compilation")

/**
 * A [TraceSectionMetric] that tracks the time spent in class initialization.
 *
 * This number should go down when a baseline profile is applied properly.
 */
@OptIn(ExperimentalMetricApi::class)
val classInitMetric = TraceSectionMetric("L%/%;", label = "ClassInit")
