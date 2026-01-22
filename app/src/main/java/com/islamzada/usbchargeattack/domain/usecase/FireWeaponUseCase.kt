package com.islamzada.usbchargeattack.domain.usecase

import com.islamzada.usbchargeattack.domain.model.Position
import com.islamzada.usbchargeattack.domain.model.Projectile

class FireWeaponUseCase {
    operator fun invoke(playerPosition: Position): List<Projectile> {
        return listOf(

            Projectile(
                position = Position(
                    playerPosition.x - PROJECTILE_SPREAD,
                    playerPosition.y + PROJECTILE_BACK_OFFSET
                )
            ),

            Projectile(
                position = playerPosition
            ),

            Projectile(
                position = Position(
                    playerPosition.x + PROJECTILE_SPREAD,
                    playerPosition.y + PROJECTILE_BACK_OFFSET
                )
            )
        )
    }

    companion object {
        private const val PROJECTILE_SPREAD = 40f
        private const val PROJECTILE_BACK_OFFSET = 30f
    }
}