package com.islamzada.usbchargeattack.domain.model

import java.util.UUID

data class Enemy(
    override val id: String = UUID.randomUUID().toString(),
    override val position: Position,
    override val isActive: Boolean = true,
    val health: Int = 1,
    val speed: Float = 0.5f
) : GameEntity {

    companion object {

        fun createRandom(screenWidth: Float, screenHeight: Float): Enemy {
            val random = kotlin.random.Random
            val x = random.nextFloat() * screenWidth
            val y = random.nextFloat() * (screenHeight * 0.6f)

            return Enemy(
                position = Position(x, y)
            )
        }
    }

    fun takeDamage(): Enemy = copy(
        health = health - 1,
        isActive = health - 1 > 0
    )

    fun moveTowards(target: Position, deltaTime: Float): Enemy {
        val direction = target - position
        val distance = position.distanceTo(target)

        if (distance < 0.01f) return this

        val normalizedDirection = Position(
            direction.x / distance,
            direction.y / distance
        )

        val velocity = normalizedDirection * (speed * deltaTime)

        return copy(position = position + velocity)
    }
}