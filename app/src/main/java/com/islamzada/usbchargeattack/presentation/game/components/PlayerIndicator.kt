package com.islamzada.usbchargeattack.presentation.game.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PlayerIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "player")

    val enginePulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "engine"
    )

    val shieldRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shield"
    )

    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Canvas(modifier = modifier.size(50.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val center = Offset(centerX, centerY)

        // Outer shield (rotating)
        rotate(shieldRotation, pivot = center) {
            drawShield(center, size.width * 0.48f, glowIntensity)
        }

        // Energy aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF00F5FF).copy(alpha = glowIntensity * 0.3f),
                    Color(0xFF0080FF).copy(alpha = 0f)
                ),
                center = center,
                radius = size.width * 0.5f
            ),
            radius = size.width * 0.5f,
            center = center
        )

        // Main ship body
        val shipPath = Path().apply {
            // Nose (sharp point)
            moveTo(centerX, centerY - size.height * 0.35f)

            // Left wing
            lineTo(centerX - size.width * 0.25f, centerY + size.height * 0.1f)
            lineTo(centerX - size.width * 0.15f, centerY + size.height * 0.05f)

            // Center body left
            lineTo(centerX - size.width * 0.1f, centerY + size.height * 0.25f)

            // Engine exhaust left
            lineTo(centerX - size.width * 0.08f, centerY + size.height * 0.35f)
            lineTo(centerX - size.width * 0.05f, centerY + size.height * 0.3f)

            // Center bottom
            lineTo(centerX, centerY + size.height * 0.32f)

            // Engine exhaust right
            lineTo(centerX + size.width * 0.05f, centerY + size.height * 0.3f)
            lineTo(centerX + size.width * 0.08f, centerY + size.height * 0.35f)

            // Center body right
            lineTo(centerX + size.width * 0.1f, centerY + size.height * 0.25f)

            // Right wing
            lineTo(centerX + size.width * 0.15f, centerY + size.height * 0.05f)
            lineTo(centerX + size.width * 0.25f, centerY + size.height * 0.1f)

            close()
        }

        // Ship glow
        drawPath(
            path = shipPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF00F5FF).copy(alpha = 0.5f),
                    Color(0xFF0080FF).copy(alpha = 0.3f)
                )
            )
        )

        // Ship main body
        drawPath(
            path = shipPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF00D9FF),
                    Color(0xFF0066CC),
                    Color(0xFF003366)
                )
            )
        )

        // Ship outline
        drawPath(
            path = shipPath,
            color = Color(0xFF00F5FF),
            style = Stroke(width = 2f)
        )

        // Cockpit window
        drawPath(
            path = Path().apply {
                moveTo(centerX, centerY - size.height * 0.25f)
                lineTo(centerX - size.width * 0.08f, centerY - size.height * 0.05f)
                lineTo(centerX + size.width * 0.08f, centerY - size.height * 0.05f)
                close()
            },
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF66FFFF).copy(alpha = 0.7f),
                    Color(0xFF0099CC).copy(alpha = 0.4f)
                )
            )
        )

        // Engine flames (pulsing)
        val flameAlpha = enginePulse * 0.8f

        // Left engine
        drawPath(
            path = Path().apply {
                moveTo(centerX - size.width * 0.08f, centerY + size.height * 0.35f)
                lineTo(centerX - size.width * 0.09f, centerY + size.height * 0.35f + 10f * enginePulse)
                lineTo(centerX - size.width * 0.05f, centerY + size.height * 0.35f + 15f * enginePulse)
                close()
            },
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF00F5FF).copy(alpha = flameAlpha),
                    Color(0xFF0080FF).copy(alpha = flameAlpha * 0.5f),
                    Color(0xFF0066CC).copy(alpha = 0f)
                )
            )
        )

        // Right engine
        drawPath(
            path = Path().apply {
                moveTo(centerX + size.width * 0.08f, centerY + size.height * 0.35f)
                lineTo(centerX + size.width * 0.09f, centerY + size.height * 0.35f + 10f * enginePulse)
                lineTo(centerX + size.width * 0.05f, centerY + size.height * 0.35f + 15f * enginePulse)
                close()
            },
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF00F5FF).copy(alpha = flameAlpha),
                    Color(0xFF0080FF).copy(alpha = flameAlpha * 0.5f),
                    Color(0xFF0066CC).copy(alpha = 0f)
                )
            )
        )

        // Wing details
        drawLine(
            color = Color(0xFF00F5FF).copy(alpha = 0.6f),
            start = Offset(centerX - size.width * 0.22f, centerY + size.height * 0.09f),
            end = Offset(centerX - size.width * 0.12f, centerY + size.height * 0.02f),
            strokeWidth = 1.5f
        )

        drawLine(
            color = Color(0xFF00F5FF).copy(alpha = 0.6f),
            start = Offset(centerX + size.width * 0.22f, centerY + size.height * 0.09f),
            end = Offset(centerX + size.width * 0.12f, centerY + size.height * 0.02f),
            strokeWidth = 1.5f
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawShield(
    center: Offset,
    radius: Float,
    intensity: Float
) {
    val points = 8
    val path = Path()

    for (i in 0 until points) {
        val angle = (2 * Math.PI * i / points).toFloat()
        val x = center.x + radius * cos(angle)
        val y = center.y + radius * sin(angle)

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()

    drawPath(
        path = path,
        color = Color(0xFF00F5FF).copy(alpha = intensity * 0.3f),
        style = Stroke(width = 2f)
    )
}