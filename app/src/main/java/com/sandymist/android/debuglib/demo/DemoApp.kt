package com.sandymist.android.debuglib.demo

import android.app.Application
import android.os.StrictMode
import com.sandymist.android.debuglib.DebugLib
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class DemoApp: Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        enableStrictMode()

        DebugLib.init(this)
    }
}

private fun enableStrictMode() {
    // Enable Thread Policy
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .penaltyDeath() // Crash on violation
            .build()
    )

    // Enable VM Policy
    StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .detectActivityLeaks()
            .penaltyLog()
            //.penaltyDeath() // Crash on violation // TODO: Check!!
            .build()
    )
}
