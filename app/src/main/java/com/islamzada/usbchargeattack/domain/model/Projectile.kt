package com.islamzada.usbchargeattack.domain.model

import java.util.UUID


data class Projectile(
    override val id: String = UUID.randomUUID().toString(),
    override val position: Position,
    override val isActive: Boolean = true,
    val velocity: Position = Position(0f, -SPEED),
    val damage: Int = 1
) : GameEntity {

    companion object {
        private const val SPEED = 800f
        const val COLLISION_RADIUS = 20f
    }

    fun update(deltaTime: Float): Projectile {
        val newPosition = position + (velocity * deltaTime)

        val stillActive = newPosition.y > -COLLISION_RADIUS

        return copy(
            position = newPosition,
            isActive = stillActive
        )
    }

    fun checkCollision(enemy: Enemy): Boolean {
        return isActive &&
                enemy.isActive &&
                position.distanceTo(enemy.position) < COLLISION_RADIUS
    }
}