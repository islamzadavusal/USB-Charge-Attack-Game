package com.islamzada.usbchargeattack.di

import com.islamzada.usbchargeattack.presentation.game.GameViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import com.islamzada.usbchargeattack.domain.usecase.FireWeaponUseCase
import com.islamzada.usbchargeattack.domain.usecase.ObserveChargingStateUseCase
import com.islamzada.usbchargeattack.domain.usecase.ObserveGyroscopeUseCase
import com.islamzada.usbchargeattack.domain.usecase.UpdateGameStateUseCase
import com.islamzada.usbchargeattack.data.repository.ChargingRepositoryImpl
import com.islamzada.usbchargeattack.data.repository.SensorRepositoryImpl
import com.islamzada.usbchargeattack.data.source.ChargingDataSource
import com.islamzada.usbchargeattack.data.source.SensorDataSource
import com.islamzada.usbchargeattack.domain.repository.ChargingRepository
import com.islamzada.usbchargeattack.domain.repository.SensorRepository
import org.koin.android.ext.koin.androidContext

val dataModule = module {

    // Data Sources
    single { SensorDataSource(androidContext()) }
    single { ChargingDataSource(androidContext()) }

    // Repositories
    single<SensorRepository> { SensorRepositoryImpl(get()) }
    single<ChargingRepository> { ChargingRepositoryImpl(get()) }
}

val presentationModule = module {

    viewModel { GameViewModel(get(), get(), get(), get()) }
}

val domainModule = module {

    factory { ObserveGyroscopeUseCase(get()) }
    factory { ObserveChargingStateUseCase(get()) }
    factory { FireWeaponUseCase() }
    factory { UpdateGameStateUseCase() }
}

val appModules = listOf(
    dataModule,
    domainModule,
    presentationModule
)