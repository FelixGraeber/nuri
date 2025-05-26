 
package app.getnuri.startup

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import app.getnuri.network.R
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

/**
 * Initialize [FirebaseRemoteConfig] using the App Startup Library.
 */
class FirebaseRemoteConfigInitializer : Initializer<FirebaseRemoteConfig> {
    override fun create(context: Context): FirebaseRemoteConfig {
        return Firebase.remoteConfig.apply {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 600
            }
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
            fetchAndActivate()
                .addOnSuccessListener {
                    Log.d("FirebaseRemoteConfig", "Config params updated: $it")
                }
                .addOnFailureListener {
                    Log.d("FirebaseRemoteConfig", "Config params failed: $it")
                }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(FirebaseAppInitializer::class.java)
    }
}
