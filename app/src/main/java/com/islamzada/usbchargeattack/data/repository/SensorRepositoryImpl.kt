package com.islamzada.usbchargeattack.data.repository

import com.islamzada.usbchargeattack.data.source.SensorDataSource
import com.islamzada.usbchargeattack.domain.model.Position
import com.islamzada.usbchargeattack.domain.repository.SensorRepository
import kotlinx.coroutines.flow.Flow

class SensorRepositoryImpl(
    private val sensorDataSource: SensorDataSource
) : SensorRepository {

    override fun observeGyroscope(): Flow<Position> {
        return sensorDataSource.observeGyroscope()
    }

    override suspend fun startListening() {
        // Gyroscope starts when flow is collected
        // No explicit start needed with callbackFlow
    }

    override suspend fun stopListening() {
        // Gyroscope stops when flow collection is cancelled
        // No explicit stop needed with callbackFlow
    }
}