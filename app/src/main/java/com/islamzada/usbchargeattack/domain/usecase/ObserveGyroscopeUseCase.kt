package com.islamzada.usbchargeattack.domain.usecase

import com.islamzada.usbchargeattack.domain.model.Position
import com.islamzada.usbchargeattack.domain.repository.SensorRepository
import kotlinx.coroutines.flow.Flow

class ObserveGyroscopeUseCase(
    private val sensorRepository: SensorRepository
) {
    operator fun invoke(): Flow<Position> = sensorRepository.observeGyroscope()
}