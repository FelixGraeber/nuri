 
package app.getnuri.camera

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.area.WindowAreaCapability
import androidx.window.area.WindowAreaController
import androidx.window.area.WindowAreaInfo
import androidx.window.area.WindowAreaSession
import androidx.window.area.WindowAreaSessionCallback
import androidx.window.core.ExperimentalWindowApi
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject

@OptIn(ExperimentalWindowApi::class)
@ViewModelScoped
class RearCameraUseCase @Inject constructor(@ApplicationContext context: Context) :
    WindowAreaSessionCallback {

    private val windowAreaController: WindowAreaController = WindowAreaController.getOrCreate()
    private val displayExecutor: Executor = ContextCompat.getMainExecutor(context)
    private var windowAreaSession: WindowAreaSession? = null
    private var windowAreaInfo: WindowAreaInfo? = null
    private var capabilityStatus: WindowAreaCapability.Status =
        WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED
    private val rearDisplayOperation =
        WindowAreaCapability.Operation.OPERATION_TRANSFER_ACTIVITY_TO_AREA

    private var isSessionActive = false

    fun init(activity: ComponentActivity) {
        activity.lifecycleScope.launch(Dispatchers.Main) {
            activity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowAreaController.windowAreaInfos
                    .map { info -> info.firstOrNull { it.type == WindowAreaInfo.Type.TYPE_REAR_FACING } }
                    .onEach { info -> windowAreaInfo = info }
                    .map {
                        it?.getCapability(rearDisplayOperation)?.status
                            ?: WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED
                    }.distinctUntilChanged()
                    .collect {
                        capabilityStatus = it
                    }
            }
        }
    }

    fun shouldDisplayRearCameraButton(): Boolean {
        return capabilityStatus == WindowAreaCapability.Status.WINDOW_AREA_STATUS_AVAILABLE ||
            capabilityStatus == WindowAreaCapability.Status.WINDOW_AREA_STATUS_ACTIVE
    }

    fun isRearCameraActive(): Boolean = isSessionActive

    fun toggleRearCameraDisplay(activity: ComponentActivity) {
        if (capabilityStatus == WindowAreaCapability.Status.WINDOW_AREA_STATUS_ACTIVE) {
            if (windowAreaSession == null) {
                windowAreaSession = windowAreaInfo?.getActiveSession(
                    rearDisplayOperation,
                )
            }
            windowAreaSession?.close()
            isSessionActive = false
        } else {
            windowAreaInfo?.token?.let { token ->
                windowAreaController.transferActivityToWindowArea(
                    token = token,
                    activity = activity,
                    executor = displayExecutor,
                    windowAreaSessionCallback = this,
                )
                isSessionActive = true
            }
        }
    }

    override fun onSessionEnded(t: Throwable?) = Unit

    override fun onSessionStarted(session: WindowAreaSession) {
        windowAreaSession = session
    }
}
