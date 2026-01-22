package com.islamzada.usbchargeattack.data.source

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChargingDataSource(private val context: Context) {

    private var receiver: BroadcastReceiver? = null

    fun observeChargingState(): Flow<Boolean> = callbackFlow {
        val chargingReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_POWER_CONNECTED -> trySend(true)
                    Intent.ACTION_POWER_DISCONNECTED -> trySend(false)
                }
            }
        }

        receiver = chargingReceiver

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }

        context.registerReceiver(chargingReceiver, filter)

        val isCurrentlyCharging = isCharging()
        trySend(isCurrentlyCharging)

        awaitClose {
            context.unregisterReceiver(chargingReceiver)
            receiver = null
        }
    }

    private fun isCharging(): Boolean {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.isCharging
    }
}