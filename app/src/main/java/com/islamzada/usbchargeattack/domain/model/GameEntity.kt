package com.islamzada.usbchargeattack.domain.model

sealed interface GameEntity {
    val id: String
    val position: Position
    val isActive: Boolean
}