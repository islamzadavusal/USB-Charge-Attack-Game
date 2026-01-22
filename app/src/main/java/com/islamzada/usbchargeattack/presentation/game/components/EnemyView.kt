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
fun EnemyView(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "enemy")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val dangerGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Canvas(modifier = modifier.size(36.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val center = Offset(centerX, centerY)

        // Danger field (pulsing red)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF0000).copy(alpha = dangerGlow * 0.4f),
                    Color(0xFFFF4400).copy(alpha = dangerGlow * 0.2f),
                    Color(0xFFFF0000).copy(alpha = 0f)
                ),
                center = center,
                radius = size.width * 0.6f * pulse
            ),
            radius = size.width * 0.6f * pulse,
            center = center
        )

        // Rotating outer ring
        rotate(rotation, pivot = center) {
            drawHexagonRing(center, size.width * 0.42f, dangerGlow)
        }

        // Counter-rotating inner ring
        rotate(-rotation * 1.5f, pivot = center) {
            drawHexagonRing(center, size.width * 0.28f, dangerGlow * 0.7f)
        }

        // Main hostile body (octagon)
        val octagonPath = Path().apply {
            val points = 8
            val radius = size.width * 0.35f

            for (i in 0 until points) {
                val angle = (2 * Math.PI * i / points - Math.PI / 2).toFloat()
                val x = center.x + radius * cos(angle)
                val y = center.y + radius * sin(angle)

                if (i == 0) moveTo(x, y)
                else lineTo(x, y)
            }
            close()
        }

        // Body glow
        drawPath(
            path = octagonPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF3300).copy(alpha = 0.6f),
                    Color(0xFFCC0000).copy(alpha = 0.8f)
                ),
                center = center
            )
        )

        // Body main
        drawPath(
            path = octagonPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF4400),
                    Color(0xFFCC0000),
                    Color(0xFF880000)
                ),
                center = center
            )
        )

        // Body outline
        drawPath(
            path = octagonPath,
            color = Color(0xFFFF0000),
            style = Stroke(width = 3f)
        )

        // Danger core (pulsing)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFF00).copy(alpha = dangerGlow),
                    Color(0xFFFF6600),
                    Color(0xFFFF0000)
                ),
                center = center,
                radius = size.width * 0.2f * pulse
            ),
            radius = size.width * 0.2f * pulse,
            center = center
        )

        // Core outline
        drawCircle(
            color = Color(0xFFFF0000),
            radius = size.width * 0.2f * pulse,
            center = center,
            style = Stroke(width = 2f)
        )

        // Danger symbol (X)
        val symbolSize = size.width * 0.12f
        drawLine(
            color = Color(0xFF000000).copy(alpha = 0.8f),
            start = Offset(centerX - symbolSize, centerY - symbolSize),
            end = Offset(centerX + symbolSize, centerY + symbolSize),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF000000).copy(alpha = 0.8f),
            start = Offset(centerX + symbolSize, centerY - symbolSize),
            end = Offset(centerX - symbolSize, centerY + symbolSize),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        // Energy spikes
        val spikeCount = 4
        for (i in 0 until spikeCount) {
            val angle = (2 * Math.PI * i / spikeCount + rotation * Math.PI / 180).toFloat()
            val startRadius = size.width * 0.38f
            val endRadius = size.width * 0.45f * pulse

            val startX = center.x + startRadius * cos(angle)
            val startY = center.y + startRadius * sin(angle)
            val endX = center.x + endRadius * cos(angle)
            val endY = center.y + endRadius * sin(angle)

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFF0000),
                        Color(0xFFFF0000).copy(alpha = 0f)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY)
                ),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHexagonRing(
    center: Offset,
    radius: Float,
    intensity: Float
) {
    val points = 6
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
        color = Color(0xFFFF0000).copy(alpha = intensity * 0.6f),
        style = Stroke(width = 2.5f)
    )
}