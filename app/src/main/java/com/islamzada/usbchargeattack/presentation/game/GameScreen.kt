package com.islamzada.usbchargeattack.presentation.game

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.islamzada.usbchargeattack.presentation.game.components.EnemyView
import com.islamzada.usbchargeattack.presentation.game.components.PlayerIndicator
import com.islamzada.usbchargeattack.presentation.game.components.ProjectileView
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GameContract.Effect.Vibrate -> vibrateDevice(context)
                is GameContract.Effect.PlayFireSound -> {}
                is GameContract.Effect.PlayExplosionSound -> {}
                is GameContract.Effect.GameOver -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E27),
                        Color(0xFF1A1F3A),
                        Color(0xFF0D1126)
                    )
                )
            )
    ) {
        // Animated background stars/particles
        AnimatedStarfield()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    val width = with(density) { coordinates.size.width.toFloat() }
                    val height = with(density) { coordinates.size.height.toFloat() }
                    viewModel.setScreenDimensions(width, height)
                }
        ) {
            if (!state.isPlaying) {
                StartScreen(
                    onStartClick = { viewModel.handleIntent(GameContract.Intent.StartGame) }
                )
            } else {
                GamePlayArea(state = state)

                // Modern HUD
                ModernHUD(
                    score = state.score,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp)
                )

                if (state.isPaused) {
                    PauseOverlay(
                        onResumeClick = { viewModel.handleIntent(GameContract.Intent.ResumeGame) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedStarfield() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")

    val star1Y by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "star1"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw moving stars
        for (i in 0..30) {
            val x = (size.width * (i * 37 % 100) / 100f)
            val y = ((star1Y + i * 50) % size.height)
            val alpha = (0.3f + (i % 5) * 0.1f)

            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = 1f + (i % 3),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun GamePlayArea(state: GameContract.State) {
    Box(modifier = Modifier.fillMaxSize()) {
        state.enemies.forEach { enemy ->
            if (enemy.isActive) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                enemy.position.x.toInt(),
                                enemy.position.y.toInt()
                            )
                        }
                ) {
                    EnemyView()
                }
            }
        }

        state.projectiles.forEach { projectile ->
            if (projectile.isActive) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                projectile.position.x.toInt(),
                                projectile.position.y.toInt()
                            )
                        }
                ) {
                    ProjectileView()
                }
            }
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        state.playerPosition.x.toInt(),
                        state.playerPosition.y.toInt()
                    )
                }
        ) {
            PlayerIndicator()
        }
    }
}

@Composable
private fun StartScreen(onStartClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "title")

    val titleGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Epic title with glow
            Text(
                text = "⚡ USB CHARGE",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF00F5FF),
                style = LocalTextStyle.current.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0xFF00F5FF).copy(alpha = titleGlow),
                        blurRadius = 30f
                    )
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "ATTACK",
                fontSize = 52.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFF6B00),
                style = LocalTextStyle.current.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0xFFFF6B00).copy(alpha = titleGlow),
                        blurRadius = 30f
                    )
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = (-10).dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Instructions card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1F3A).copy(alpha = 0.8f)
                ),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00F5FF).copy(alpha = 0.5f),
                                Color(0xFFFF6B00).copy(alpha = 0.5f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InstructionRow("📱", "Tilt phone to move")
                    Spacer(modifier = Modifier.height(12.dp))
                    InstructionRow("🔌", "Plug charger to shoot")
                    Spacer(modifier = Modifier.height(12.dp))
                    InstructionRow("🎯", "Destroy all enemies!")
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Epic start button
            Button(
                onClick = onStartClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .height(64.dp)
                    .widthIn(min = 200.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00F5FF),
                                Color(0xFF00D9FF),
                                Color(0xFFFF6B00)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "START MISSION",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun InstructionRow(icon: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = icon,
            fontSize = 24.sp,
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ModernHUD(
    score: Int,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D1126).copy(alpha = 0.85f)
        ),
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00F5FF).copy(alpha = 0.6f),
                        Color(0xFFFF6B00).copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(
                text = "SCORE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00F5FF),
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = score.toString().padStart(5, '0'),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                style = LocalTextStyle.current.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0xFFFF6B00),
                        blurRadius = 10f
                    )
                )
            )
        }
    }
}

@Composable
private fun PauseOverlay(onResumeClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .blur(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "⏸️ PAUSED",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF00F5FF),
                style = LocalTextStyle.current.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0xFF00F5FF),
                        blurRadius = 20f
                    )
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onResumeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 180.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00F5FF),
                                Color(0xFFFF6B00)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "▶️ RESUME",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

private fun vibrateDevice(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(50)
    }
}