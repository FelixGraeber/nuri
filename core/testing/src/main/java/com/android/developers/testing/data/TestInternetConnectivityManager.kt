 
package com.android.developers.testing.data

import app.getnuri.data.InternetConnectivityManager

/**
 * Test implementation of [InternetConnectivityManager].
 *
 * @property internetAvailable Whether internet is available.
 */
class TestInternetConnectivityManager(var internetAvailable: Boolean) : InternetConnectivityManager {

    override fun isInternetAvailable(): Boolean {
        return internetAvailable
    }
}
