package com.islamzada.usbchargeattack.domain.repository

import kotlinx.coroutines.flow.Flow

interface ChargingRepository {

    fun observeChargingState(): Flow<Boolean>

    suspend fun startObserving()

    suspend fun stopObserving()
}