package com.islamzada.usbchargeattack.presentation.game

import androidx.lifecycle.viewModelScope
import com.islamzada.usbchargeattack.domain.model.Enemy
import com.islamzada.usbchargeattack.domain.model.Position
import com.islamzada.usbchargeattack.domain.usecase.FireWeaponUseCase
import com.islamzada.usbchargeattack.domain.usecase.ObserveChargingStateUseCase
import com.islamzada.usbchargeattack.domain.usecase.ObserveGyroscopeUseCase
import com.islamzada.usbchargeattack.domain.usecase.UpdateGameStateUseCase
import com.islamzada.usbchargeattack.presentation.common.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GameViewModel(
    private val observeGyroscopeUseCase: ObserveGyroscopeUseCase,
    private val observeChargingStateUseCase: ObserveChargingStateUseCase,
    private val fireWeaponUseCase: FireWeaponUseCase,
    private val updateGameStateUseCase: UpdateGameStateUseCase
) : BaseViewModel<GameContract.State, GameContract.Intent, GameContract.Effect>(
    initialState = GameContract.State()
) {

    private var gameLoopJob: Job? = null
    private var enemySpawnJob: Job? = null

    init {
        observeGyroscope()
        observeChargingEvents()
    }

    override fun handleIntent(intent: GameContract.Intent) {
        when (intent) {
            is GameContract.Intent.StartGame -> startGame()
            is GameContract.Intent.PauseGame -> pauseGame()
            is GameContract.Intent.ResumeGame -> resumeGame()
            is GameContract.Intent.GameTick -> updateGame()
            is GameContract.Intent.GyroscopeUpdate -> updatePlayerPosition(intent.offset)
            is GameContract.Intent.WeaponFired -> fireWeapon()
            is GameContract.Intent.SpawnEnemy -> spawnEnemy()
        }
    }

    fun setScreenDimensions(width: Float, height: Float) {
        setState {
            copy(
                screenWidth = width,
                screenHeight = height,
                playerPosition = Position(width / 2, height - PLAYER_OFFSET_FROM_BOTTOM)
            )
        }
    }

    private fun startGame() {
        setState {
            copy(
                isPlaying = true,
                isPaused = false,
                enemies = emptyList(),
                projectiles = emptyList(),
                score = 0,
                lastUpdateTime = System.currentTimeMillis()
            )
        }
        startGameLoop()
        startEnemySpawner()
    }

    private fun pauseGame() {
        setState { copy(isPaused = true) }
        stopGameLoop()
        stopEnemySpawner()
    }

    private fun resumeGame() {
        setState {
            copy(
                isPaused = false,
                lastUpdateTime = System.currentTimeMillis()
            )
        }
        startGameLoop()
        startEnemySpawner()
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (true) {
                delay(GAME_TICK_DELAY)
                if (currentState.isPlaying && !currentState.isPaused) {
                    handleIntent(GameContract.Intent.GameTick)
                }
            }
        }
    }

    private fun stopGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = null
    }

    private fun startEnemySpawner() {
        enemySpawnJob?.cancel()
        enemySpawnJob = viewModelScope.launch {
            while (true) {
                delay(ENEMY_SPAWN_INTERVAL)
                if (currentState.isPlaying && !currentState.isPaused) {
                    handleIntent(GameContract.Intent.SpawnEnemy)
                }
            }
        }
    }

    private fun stopEnemySpawner() {
        enemySpawnJob?.cancel()
        enemySpawnJob = null
    }

    private fun updateGame() {
        val state = currentState

        if (state.isGameOver) {
            endGame()
            return
        }

        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - state.lastUpdateTime) / 1000f

        val result = updateGameStateUseCase(
            enemies = state.enemies,
            projectiles = state.projectiles,
            playerPosition = state.playerPosition,
            deltaTime = deltaTime,
            screenWidth = state.screenWidth,
            screenHeight = state.screenHeight
        )

        setState {
            copy(
                enemies = result.enemies,
                projectiles = result.projectiles,
                score = score + result.score,
                lastUpdateTime = currentTime
            )
        }

        if (result.enemiesDestroyed > 0) {
            sendEffect(GameContract.Effect.PlayExplosionSound)
        }
    }

    private fun updatePlayerPosition(offset: Position) {
        if (!currentState.isPlaying || currentState.isPaused) return

        setState {
            val newX = (playerPosition.x + offset.x).coerceIn(
                PLAYER_MARGIN,
                screenWidth - PLAYER_MARGIN
            )
            val newY = (playerPosition.y + offset.y).coerceIn(
                screenHeight * 0.7f,
                screenHeight - PLAYER_MARGIN
            )

            copy(playerPosition = Position(newX, newY))
        }
    }

    private fun fireWeapon() {
        if (!currentState.isPlaying || currentState.isPaused) return

        val projectiles = fireWeaponUseCase(currentState.playerPosition)

        setState {
            copy(projectiles = this.projectiles + projectiles)
        }

        sendEffect(GameContract.Effect.Vibrate)
        sendEffect(GameContract.Effect.PlayFireSound)
    }

    private fun spawnEnemy() {
        val state = currentState

        if (state.enemies.size >= MAX_ENEMIES) return

        val enemy = Enemy.createRandom(state.screenWidth, state.screenHeight)

        setState {
            copy(enemies = enemies + enemy)
        }
    }

    private fun endGame() {
        setState {
            copy(
                isPlaying = false,
                isPaused = false
            )
        }
        stopGameLoop()
        stopEnemySpawner()
        sendEffect(GameContract.Effect.GameOver)
    }

    private fun observeGyroscope() {
        observeGyroscopeUseCase()
            .onEach { offset ->
                handleIntent(GameContract.Intent.GyroscopeUpdate(offset))
            }
            .catch { /* Sensor errors are not critical */ }
            .launchIn(viewModelScope)
    }

    private fun observeChargingEvents() {
        observeChargingStateUseCase()
            .onEach { isCharging ->
                if (isCharging) {
                    handleIntent(GameContract.Intent.WeaponFired)
                }
            }
            .catch { /* Charging observation errors are not critical */ }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        stopGameLoop()
        stopEnemySpawner()
    }

    companion object {
        private const val GAME_TICK_DELAY = 16L // ~60 FPS
        private const val ENEMY_SPAWN_INTERVAL = 2000L // 2 seconds
        private const val MAX_ENEMIES = 10
        private const val PLAYER_MARGIN = 50f
        private const val PLAYER_OFFSET_FROM_BOTTOM = 100f
    }
}