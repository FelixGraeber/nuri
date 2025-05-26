 
package app.getnuri.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Sets the [ImageAnalysis.Analyzer] on image analysis until the calling coroutine is cancelled.
 *
 * Each time a new [ImageProxy] is available, it will be sent to [block]. The block will be called
 * within the [kotlin.coroutines.CoroutineContext] of the calling coroutine. The provided image
 * proxy will be automatically closed when [block] completes.
 */
@OptIn(DelicateCoroutinesApi::class)
suspend fun ImageAnalysis.analyze(block: suspend (ImageProxy) -> Unit) {
    coroutineScope {
        try {
            suspendCancellableCoroutine<Unit> { cont ->
                setAnalyzer(Runnable::run) { imageProxy ->
                    // Launch ATOMIC to ensure we close the ImageProxy
                    launch(start = CoroutineStart.ATOMIC) {
                        imageProxy.use { if (cont.isActive) block(it) }
                    }
                }
            }
        } finally {
            clearAnalyzer()
        }
    }
}
