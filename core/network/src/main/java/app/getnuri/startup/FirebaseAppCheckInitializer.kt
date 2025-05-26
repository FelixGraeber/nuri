 
package app.getnuri.startup

import android.content.Context
import androidx.startup.Initializer
import app.getnuri.network.BuildConfig
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.ktx.Firebase

/**
 * Initialize [FirebaseAppCheck] using the App Startup Library.
 */
class FirebaseAppCheckInitializer : Initializer<FirebaseAppCheck> {
    override fun create(context: Context): FirebaseAppCheck {
        return Firebase.appCheck.apply {
            // Use debug provider for debug builds, Play Integrity for release builds
            if (BuildConfig.DEBUG) {
                installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance(),
                )
            } else {
                installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance(),
                )
            }
            
            // TEMPORARY: Uncomment the line below to disable App Check for testing
            // Note: This should NEVER be used in production!
            // setTokenAutoRefreshEnabled(false)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(FirebaseAppInitializer::class.java)
    }
}
