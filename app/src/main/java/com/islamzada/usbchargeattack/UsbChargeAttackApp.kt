package com.islamzada.usbchargeattack

import android.app.Application
import com.islamzada.usbchargeattack.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class UsbChargeAttackApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@UsbChargeAttackApp)
            modules(appModules)
        }
    }
}