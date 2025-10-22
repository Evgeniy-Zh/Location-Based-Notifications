package com.sap.codelab

import android.app.Application
import com.sap.codelab.notification.NotificationHelper

/**
 * Extension of the Android Application class.
 */
internal class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DI.initialize(this)
        NotificationHelper.initialize(this)

    }
}