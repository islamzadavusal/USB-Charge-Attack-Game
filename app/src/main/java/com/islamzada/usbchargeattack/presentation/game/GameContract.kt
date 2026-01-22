package com.islamzada.usbchargeattack.presentation.game

import com.islamzada.usbchargeattack.domain.model.Enemy
import com.islamzada.usbchargeattack.domain.model.Position
import com.islamzada.usbchargeattack.domain.model.Projectile

object GameContract {

    sealed interface Intent {
        data object StartGame : Intent
        data object PauseGame : Intent
        data object ResumeGame : Intent
        data object GameTick : Intent
        data class GyroscopeUpdate(val offset: Position) : Intent
        data object WeaponFired : Intent
        data object SpawnEnemy : Intent
    }

    data class State(
        val isPlaying: Boolean = false,
        val isPaused: Boolean = false,
        val playerPosition: Position = Position(0f, 0f),
        val enemies: List<Enemy> = emptyList(),
        val projectiles: List<Projectile> = emptyList(),
        val score: Int = 0,
        val screenWidth: Float = 0f,
        val screenHeight: Float = 0f,
        val lastUpdateTime: Long = System.currentTimeMillis()
    ) {
        val isGameOver: Boolean
            get() = enemies.any { enemy ->
                enemy.isActive && playerPosition.distanceTo(enemy.position) < GAME_OVER_DISTANCE
            }

        companion object {
            private const val GAME_OVER_DISTANCE = 50f
        }
    }

    sealed interface Effect {
        data object Vibrate : Effect
        data object PlayFireSound : Effect
        data object PlayExplosionSound : Effect
        data object GameOver : Effect
    }
}