 
package app.getnuri.startup

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.initialize

/**
 * Initialize [FirebaseApp] using the App Startup Library.
 */
class FirebaseAppInitializer : Initializer<FirebaseApp> {
    override fun create(context: Context): FirebaseApp {
        return Firebase.initialize(context)!!
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        // No dependencies
        return emptyList()
    }
}
