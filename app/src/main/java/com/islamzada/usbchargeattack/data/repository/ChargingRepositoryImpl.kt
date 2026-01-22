package com.islamzada.usbchargeattack.data.repository

import com.islamzada.usbchargeattack.data.source.ChargingDataSource
import com.islamzada.usbchargeattack.domain.repository.ChargingRepository
import kotlinx.coroutines.flow.Flow

class ChargingRepositoryImpl(
    private val chargingDataSource: ChargingDataSource
) : ChargingRepository {

    override fun observeChargingState(): Flow<Boolean> {
        return chargingDataSource.observeChargingState()
    }

    override suspend fun startObserving() {
        // BroadcastReceiver registration happens when flow is collected
    }

    override suspend fun stopObserving() {
        // BroadcastReceiver unregistration happens when flow collection is cancelled
    }
}