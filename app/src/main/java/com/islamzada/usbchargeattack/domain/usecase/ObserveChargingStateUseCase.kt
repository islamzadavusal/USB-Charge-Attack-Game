package com.islamzada.usbchargeattack.domain.usecase

import com.islamzada.usbchargeattack.domain.repository.ChargingRepository
import kotlinx.coroutines.flow.Flow

class ObserveChargingStateUseCase(
    private val chargingRepository: ChargingRepository
) {
    operator fun invoke(): Flow<Boolean> = chargingRepository.observeChargingState()
}