package com.islamzada.usbchargeattack.domain.usecase

import com.islamzada.usbchargeattack.domain.model.Enemy
import com.islamzada.usbchargeattack.domain.model.Position
import com.islamzada.usbchargeattack.domain.model.Projectile

class UpdateGameStateUseCase {

    data class GameUpdateResult(
        val enemies: List<Enemy>,
        val projectiles: List<Projectile>,
        val score: Int,
        val enemiesDestroyed: Int
    )

    operator fun invoke(
        enemies: List<Enemy>,
        projectiles: List<Projectile>,
        playerPosition: Position,
        deltaTime: Float,
        screenWidth: Float,
        screenHeight: Float
    ): GameUpdateResult {

        val updatedProjectiles = projectiles
            .map { it.update(deltaTime) }
            .filter { it.isActive }

        val movedEnemies = enemies.map { enemy ->
            if (enemy.isActive) {
                enemy.moveTowards(playerPosition, deltaTime)
            } else {
                enemy
            }
        }

        val (collidedEnemies, destroyedCount) = detectCollisions(
            movedEnemies,
            updatedProjectiles
        )

        val activeEnemies = collidedEnemies.filter { it.isActive }
        val activeProjectiles = updatedProjectiles.filter { projectile ->

            val hitEnemy = collidedEnemies.any { enemy ->
                projectile.checkCollision(enemy) && !enemy.isActive
            }
            !hitEnemy
        }

        return GameUpdateResult(
            enemies = activeEnemies,
            projectiles = activeProjectiles,
            score = destroyedCount,
            enemiesDestroyed = destroyedCount
        )
    }

    private fun detectCollisions(
        enemies: List<Enemy>,
        projectiles: List<Projectile>
    ): Pair<List<Enemy>, Int> {
        var destroyedCount = 0
        val processedEnemies = enemies.map { enemy ->
            val hitByProjectile = projectiles.any { projectile ->
                projectile.checkCollision(enemy)
            }

            if (hitByProjectile && enemy.isActive) {
                destroyedCount++
                enemy.takeDamage()
            } else {
                enemy
            }
        }

        return processedEnemies to destroyedCount
    }
}