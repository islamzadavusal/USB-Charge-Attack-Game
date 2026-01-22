package com.islamzada.usbchargeattack.domain.model
data class Position(
    val x: Float,
    val y: Float
) {
    operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    operator fun minus(other: Position) = Position(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Position(x * scalar, y * scalar)

    fun distanceTo(other: Position): Float {
        val dx = x - other.x
        val dy = y - other.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}