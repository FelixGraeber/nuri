 
package app.getnuri.data

import app.getnuri.RemoteConfigDataSource
import javax.inject.Singleton

@Singleton
class ConfigProvider(val remoteConfigDataSource: RemoteConfigDataSource) {

    fun isAppInactive(): Boolean {
        return remoteConfigDataSource.isAppInactive()
    }

    fun getPromoVideoLink(): String {
        return remoteConfigDataSource.getPromoVideoLink()
    }

    fun getDancingDroidLink(): String {
        return remoteConfigDataSource.getDancingDroidLink()
    }
}
