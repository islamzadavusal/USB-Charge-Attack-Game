package com.islamzada.usbchargeattack

import com.islamzada.usbchargeattack.domain.model.Enemy
import com.islamzada.usbchargeattack.domain.model.Position
import com.islamzada.usbchargeattack.domain.model.Projectile
import com.islamzada.usbchargeattack.domain.usecase.UpdateGameStateUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateGameStateUseCaseTest {

    private lateinit var useCase: UpdateGameStateUseCase

    @Before
    fun setup() {
        useCase = UpdateGameStateUseCase()
    }

    @Test
    fun `when projectile hits enemy, enemy should be destroyed and score increased`() {
        // Given
        val enemy = Enemy(position = Position(100f, 100f), health = 1)
        val projectile = Projectile(position = Position(100f, 100f))

        // When
        val result = useCase(
            enemies = listOf(enemy),
            projectiles = listOf(projectile),
            playerPosition = Position(100f, 500f),
            deltaTime = 0.016f,
            screenWidth = 1000f,
            screenHeight = 2000f
        )

        // Then
        assertEquals(1, result.score)
        assertEquals(1, result.enemiesDestroyed)
        assertTrue(result.enemies.none { it.isActive })
    }

    @Test
    fun `when no collision occurs, entities should remain active`() {
        // Given
        val enemy = Enemy(position = Position(100f, 100f))
        val projectile = Projectile(position = Position(500f, 500f))

        // When
        val result = useCase(
            enemies = listOf(enemy),
            projectiles = listOf(projectile),
            playerPosition = Position(100f, 500f),
            deltaTime = 0.016f,
            screenWidth = 1000f,
            screenHeight = 2000f
        )

        // Then
        assertEquals(0, result.score)
        assertEquals(0, result.enemiesDestroyed)
        assertTrue(result.enemies.all { it.isActive })
        assertTrue(result.projectiles.isNotEmpty())
    }

    @Test
    fun `projectiles should be removed when they go off screen`() {
        // Given
        val projectile = Projectile(position = Position(100f, -50f))

        // When
        val result = useCase(
            enemies = emptyList(),
            projectiles = listOf(projectile),
            playerPosition = Position(100f, 500f),
            deltaTime = 0.016f,
            screenWidth = 1000f,
            screenHeight = 2000f
        )

        // Then
        assertTrue(result.projectiles.isEmpty())
    }

    @Test
    fun `enemies should move towards player position`() {
        // Given
        val initialPosition = Position(200f, 100f)
        val playerPosition = Position(200f, 500f)
        val enemy = Enemy(position = initialPosition)

        // When
        val result = useCase(
            enemies = listOf(enemy),
            projectiles = emptyList(),
            playerPosition = playerPosition,
            deltaTime = 0.1f,
            screenWidth = 1000f,
            screenHeight = 2000f
        )

        // Then
        val updatedEnemy = result.enemies.first()
        assertTrue(updatedEnemy.position.y > initialPosition.y)
        assertTrue(updatedEnemy.position.distanceTo(playerPosition) < initialPosition.distanceTo(playerPosition))
    }
}