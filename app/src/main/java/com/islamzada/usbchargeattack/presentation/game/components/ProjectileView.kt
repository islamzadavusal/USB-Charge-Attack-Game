package com.islamzada.usbchargeattack.presentation.game.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProjectileView(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "projectile")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Canvas(modifier = modifier.size(24.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val center = Offset(centerX, centerY)

        // Outer energy ring (rotating)
        rotate(rotation, pivot = center) {
            drawEnergyRing(center, size.width * 0.45f, glowIntensity)
        }

        // Particle trails (8 directions)
        for (i in 0..7) {
            val angle = (i * 45f + rotation * 0.5f) * (Math.PI / 180f).toFloat()
            val trailLength = size.width * 0.3f * pulse
            val endX = centerX + cos(angle) * trailLength
            val endY = centerY + sin(angle) * trailLength

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFDD00),
                        Color(0xFFFF6B00).copy(alpha = 0f)
                    ),
                    start = center,
                    end = Offset(endX, endY)
                ),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }

        // Middle glow layer
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFF00).copy(alpha = glowIntensity),
                    Color(0xFFFF8800).copy(alpha = glowIntensity * 0.6f),
                    Color(0xFFFF0000).copy(alpha = 0f)
                ),
                center = center,
                radius = size.width * 0.5f * pulse
            ),
            radius = size.width * 0.5f * pulse,
            center = center
        )

        // Inner core (bright white center)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    Color(0xFFFFFF00),
                    Color(0xFFFFAA00)
                ),
                center = center,
                radius = size.width * 0.2f
            ),
            radius = size.width * 0.2f * pulse,
            center = center
        )

        // Hotspot
        drawCircle(
            color = Color.White,
            radius = size.width * 0.08f,
            center = center
        )
    }
}

private fun DrawScope.drawEnergyRing(center: Offset, radius: Float, intensity: Float) {
    val points = 6
    val path = Path()

    for (i in 0 until points) {
        val angle1 = (2 * Math.PI * i / points).toFloat()
        val angle2 = (2 * Math.PI * (i + 1) / points).toFloat()

        val x1 = center.x + radius * cos(angle1)
        val y1 = center.y + radius * sin(angle1)
        val x2 = center.x + radius * cos(angle2)
        val y2 = center.y + radius * sin(angle2)

        if (i == 0) {
            path.moveTo(x1, y1)
        }
        path.lineTo(x2, y2)
    }
    path.close()

    drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFDD00).copy(alpha = intensity),
                Color(0xFFFF6B00).copy(alpha = intensity * 0.7f)
            )
        ),
        style = Stroke(width = 3f, cap = StrokeCap.Round)
    )
}