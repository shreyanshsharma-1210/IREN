package com.example.hybridmind.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Ethereal animated beams background effect
 * Creates animated light ray beams emanating from center
 */
@Composable
fun EtherealBeams(
    modifier: Modifier = Modifier,
    beamCount: Int = 12,
    baseColor: Color = Color(0xFF8B5CF6), // Purple
    accentColor: Color = Color(0xFF06B6D4) // Cyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "beams")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.width.coerceAtLeast(size.height)
        
        rotate(rotation, pivot = center) {
            repeat(beamCount) { index ->
                val angle = (360f / beamCount) * index
                val angleRad = angle * PI.toFloat() / 180f
                
                // Alternating colors
                val color = if (index % 2 == 0) baseColor else accentColor
                
                drawBeam(
                    center = center,
                    angle = angleRad,
                    length = maxRadius * 1.5f,
                    color = color,
                    alpha = pulse * 0.4f,
                    width = 40f + (sin(rotation * 0.05f) * 10f)
                )
            }
        }
    }
}

private fun DrawScope.drawBeam(
    center: Offset,
    angle: Float,
    length: Float,
    color: Color,
    alpha: Float,
    width: Float
) {
    val endX = center.x + cos(angle) * length
    val endY = center.y + sin(angle) * length
    
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(
                color.copy(alpha = alpha),
                color.copy(alpha = 0f)
            ),
            start = center,
            end = Offset(endX, endY)
        ),
        start = center,
        end = Offset(endX, endY),
        strokeWidth = width,
        cap = StrokeCap.Round
    )
}
