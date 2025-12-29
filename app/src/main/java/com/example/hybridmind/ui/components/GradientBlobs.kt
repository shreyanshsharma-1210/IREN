package com.example.hybridmind.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Gradient blob shape for background decoration
 * Can be animated (for landing page) or static (for other screens)
 */
@Composable
fun GradientBlob(
    colors: List<Color>,
    size: Dp = 300.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    blur: Dp = 50.dp,
    animated: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Animation for landing page
    val infiniteTransition = rememberInfiniteTransition(label = "blob_animation")
    
    val animatedOffsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (animated) 30f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_x"
    )
    
    val animatedOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (animated) 20f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_y"
    )
    
    Box(
        modifier = modifier
            .offset(
                x = offsetX + if (animated) animatedOffsetX.dp else 0.dp,
                y = offsetY + if (animated) animatedOffsetY.dp else 0.dp
            )
            .size(size)
            .blur(blur)
            .background(
                brush = Brush.radialGradient(colors),
                shape = BlobShape()
            )
    )
}

/**
 * Pre-configured gradient blob variants
 */
object GradientBlobs {
    @Composable
    fun PurpleBlue(
        size: Dp = 300.dp,
        offsetX: Dp = 0.dp,
        offsetY: Dp = 0.dp,
        animated: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        GradientBlob(
            colors = listOf(
                Color(0xFF6366F1),
                Color(0xFF8B5CF6),
                Color(0xFFA78BFA)
            ),
            size = size,
            offsetX = offsetX,
            offsetY = offsetY,
            animated = animated,
            modifier = modifier
        )
    }
    
    @Composable
    fun OrangePink(
        size: Dp = 300.dp,
        offsetX: Dp = 0.dp,
        offsetY: Dp = 0.dp,
        animated: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        GradientBlob(
            colors = listOf(
                Color(0xFFF59E0B),
                Color(0xFFEC4899),
                Color(0xFFF97316)
            ),
            size = size,
            offsetX = offsetX,
            offsetY = offsetY,
            animated = animated,
            modifier = modifier
        )
    }
    
    @Composable
    fun CyanBlue(
        size: Dp = 300.dp,
        offsetX: Dp = 0.dp,
        offsetY: Dp = 0.dp,
        animated: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        GradientBlob(
            colors = listOf(
                Color(0xFF06B6D4),
                Color(0xFF3B82F6),
                Color(0xFF6366F1)
            ),
            size = size,
            offsetX = offsetX,
            offsetY = offsetY,
            animated = animated,
            modifier = modifier
        )
    }
    
    @Composable
    fun GreenTeal(
        size: Dp = 300.dp,
        offsetX: Dp = 0.dp,
        offsetY: Dp = 0.dp,
        animated: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        GradientBlob(
            colors = listOf(
                Color(0xFF10B981),
                Color(0xFF14B8A6),
                Color(0xFF06B6D4)
            ),
            size = size,
            offsetX = offsetX,
            offsetY = offsetY,
            animated = animated,
            modifier = modifier
        )
    }
}

/**
 * Organic blob shape
 */
private fun BlobShape() = GenericShape { size, _ ->
    moveTo(size.width * 0.5f, 0f)
    cubicTo(
        size.width * 0.8f, size.height * 0.1f,
        size.width * 0.9f, size.height * 0.4f,
        size.width, size.height * 0.5f
    )
    cubicTo(
        size.width * 0.9f, size.height * 0.7f,
        size.width * 0.7f, size.height * 0.9f,
        size.width * 0.5f, size.height
    )
    cubicTo(
        size.width * 0.3f, size.height * 0.9f,
        size.width * 0.1f, size.height * 0.7f,
        0f, size.height * 0.5f
    )
    cubicTo(
        size.width * 0.1f, size.height * 0.3f,
        size.width * 0.2f, size.height * 0.1f,
        size.width * 0.5f, 0f
    )
}
