 

package app.getnuri.camera

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.Stable

@Stable
class ZoomState(
    initialZoomLevel: Float,
    val zoomRange: ClosedFloatingPointRange<Float>,
    val onChangeZoomLevel: (Float) -> Unit,
) {
    private var functionalZoom = initialZoomLevel

    private val mutatorMutex = MutatorMutex()

    /**
     * Immediately set the current zoom level to [targetZoomLevel].
     */
    suspend fun absoluteZoom(targetZoomLevel: Float) {
        mutatorMutex.mutate {
            functionalZoom = targetZoomLevel.coerceIn(zoomRange)
            onChangeZoomLevel(functionalZoom)
        }
    }

    /**
     * Scale the current zoom level.
     */
    suspend fun scaleZoom(scalingFactor: Float) {
        absoluteZoom(scalingFactor * functionalZoom)
    }

    /**
     * Ease towards a specific zoom level
     *
     * @param animationSpec [AnimationSpec] used for the animation, default to tween over 500ms
     */
    suspend fun animatedZoom(
        targetZoomLevel: Float,
        animationSpec: AnimationSpec<Float> = tween(durationMillis = 500),
    ) {
        mutatorMutex.mutate {
            Animatable(initialValue = functionalZoom).animateTo(
                targetValue = targetZoomLevel,
                animationSpec = animationSpec,
            ) {
                // this is called every animation frame
                functionalZoom = value.coerceIn(zoomRange)
                onChangeZoomLevel(functionalZoom)
            }
        }
    }
}