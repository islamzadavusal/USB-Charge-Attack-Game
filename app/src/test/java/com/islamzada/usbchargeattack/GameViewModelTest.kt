package com.islamzada.usbchargeattack

import app.cash.turbine.test
import com.islamzada.usbchargeattack.domain.model.Position
import com.islamzada.usbchargeattack.domain.usecase.FireWeaponUseCase
import com.islamzada.usbchargeattack.domain.usecase.ObserveChargingStateUseCase
import com.islamzada.usbchargeattack.domain.usecase.ObserveGyroscopeUseCase
import com.islamzada.usbchargeattack.domain.usecase.UpdateGameStateUseCase
import com.islamzada.usbchargeattack.presentation.game.GameContract
import com.islamzada.usbchargeattack.presentation.game.GameViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GameViewModel.
 * Tests MVI state management, intent handling, and effect emission.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var observeGyroscopeUseCase: ObserveGyroscopeUseCase
    private lateinit var observeChargingStateUseCase: ObserveChargingStateUseCase
    private lateinit var fireWeaponUseCase: FireWeaponUseCase
    private lateinit var updateGameStateUseCase: UpdateGameStateUseCase

    private lateinit var viewModel: GameViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        observeGyroscopeUseCase = mockk()
        observeChargingStateUseCase = mockk()
        fireWeaponUseCase = FireWeaponUseCase()
        updateGameStateUseCase = UpdateGameStateUseCase()

        every { observeGyroscopeUseCase() } returns flowOf()
        every { observeChargingStateUseCase() } returns flowOf()

        viewModel = GameViewModel(
            observeGyroscopeUseCase,
            observeChargingStateUseCase,
            fireWeaponUseCase,
            updateGameStateUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be not playing`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isPlaying)
            assertFalse(state.isPaused)
            assertEquals(0, state.score)
        }
    }

    @Test
    fun `when StartGame intent is handled, game should start`() = runTest {
        // Given
        viewModel.setScreenDimensions(1000f, 2000f)

        // When
        viewModel.handleIntent(GameContract.Intent.StartGame)

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isPlaying)
            assertFalse(state.isPaused)
            assertEquals(0, state.score)
        }
    }

    @Test
    fun `when PauseGame intent is handled, game should pause`() = runTest {
        // Given
        viewModel.setScreenDimensions(1000f, 2000f)
        viewModel.handleIntent(GameContract.Intent.StartGame)

        // When
        viewModel.handleIntent(GameContract.Intent.PauseGame)

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isPaused)
        }
    }

    @Test
    fun `when WeaponFired intent is handled, projectile should be added`() = runTest {
        // Given
        viewModel.setScreenDimensions(1000f, 2000f)
        viewModel.handleIntent(GameContract.Intent.StartGame)

        // When
        viewModel.handleIntent(GameContract.Intent.WeaponFired)

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.projectiles.size)
        }
    }

    @Test
    fun `when WeaponFired intent is handled, vibrate effect should be sent`() = runTest {
        // Given
        viewModel.setScreenDimensions(1000f, 2000f)
        viewModel.handleIntent(GameContract.Intent.StartGame)

        // When
        viewModel.handleIntent(GameContract.Intent.WeaponFired)

        // Then
        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is GameContract.Effect.Vibrate || effect is GameContract.Effect.PlayFireSound)
        }
    }

    @Test
    fun `when GyroscopeUpdate intent is handled, player position should update`() = runTest {
        // Given
        viewModel.setScreenDimensions(1000f, 2000f)
        viewModel.handleIntent(GameContract.Intent.StartGame)
        val initialPosition = viewModel.state.value.playerPosition

        // When
        viewModel.handleIntent(GameContract.Intent.GyroscopeUpdate(Position(10f, 0f)))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.playerPosition.x > initialPosition.x)
        }
    }

    @Test
    fun `when SpawnEnemy intent is handled, enemy should be added`() = runTest {
        // Given
        viewModel.setScreenDimensions(1000f, 2000f)
        viewModel.handleIntent(GameContract.Intent.StartGame)

        // When
        viewModel.handleIntent(GameContract.Intent.SpawnEnemy)

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.enemies.size)
        }
    }
}