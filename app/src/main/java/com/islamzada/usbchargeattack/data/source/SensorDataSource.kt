package com.islamzada.usbchargeattack.data.source

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.islamzada.usbchargeattack.domain.model.Position
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SensorDataSource(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private var listener: SensorEventListener? = null

    fun observeGyroscope(): Flow<Position> = callbackFlow {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
                    // Convert gyroscope rotation rates to position offsets
                    // event.values[0]: rotation around X axis (pitch)
                    // event.values[1]: rotation around Y axis (roll)
                    val x = -event.values[1] * SENSITIVITY
                    val y = event.values[0] * SENSITIVITY

                    trySend(Position(x, y))
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Not needed for this use case
            }
        }

        listener = sensorListener

        gyroscope?.let {
            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        awaitClose {
            sensorManager.unregisterListener(sensorListener)
            listener = null
        }
    }

    companion object {
        private const val SENSITIVITY = 50f
    }
}