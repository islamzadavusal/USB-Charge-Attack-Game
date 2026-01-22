package com.islamzada.usbchargeattack.domain.repository

import com.islamzada.usbchargeattack.domain.model.Position
import kotlinx.coroutines.flow.Flow

interface SensorRepository {

    fun observeGyroscope(): Flow<Position>

    suspend fun startListening()

    suspend fun stopListening()
}