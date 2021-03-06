package io.vortex.android.utils

import android.app.Application
import io.vortex.android.errors.VortexFirebaseConfigurationException
import io.vortex.android.models.VortexPrefsDetails

/**
 * Created By : Yazan Tarifi
 * Date : 10/10/2019
 * Time : 9:52 PM
 */

interface VortexConfigurationImpl<L, I> {

    fun registerApplicationClass(app: Application): VortexConfiguration

    suspend fun registerApplicationLogger(logger: L): VortexConfiguration

    suspend fun registerImageLoader(imageLoader: I): VortexConfiguration

    suspend fun registerLeakCanaryConfiguration(): VortexConfiguration

    suspend fun registerApplicationState(state: Boolean): VortexConfiguration

    @Throws(VortexFirebaseConfigurationException::class)
    suspend fun registerFirebaseConfiguration(moduleName: String): VortexConfiguration

    suspend fun registerStrictMode(): VortexConfiguration

    suspend fun registerExceptionHandler(handler: Thread.UncaughtExceptionHandler): VortexConfiguration

    suspend fun registerCompatVector(): VortexConfiguration

    suspend fun registerFabric(): VortexConfiguration

    suspend fun checkPlatformRequiredConfiguration(): VortexConfiguration

    fun registerVortexPrefsConfiguration(details: VortexPrefsDetails , application: Application): VortexConfiguration

    suspend fun registerUserStoryConfiguration(): VortexConfiguration

    suspend fun registerVortexPermissionsSettings(): VortexConfiguration

}
