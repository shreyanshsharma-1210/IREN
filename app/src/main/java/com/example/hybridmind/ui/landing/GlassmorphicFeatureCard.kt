package com.example.hybridmind.ui.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlassmorphicFeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false
) {
    Card(
        modifier = modifier
            .heightIn(min = 180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) {
                Color(0x30FFFFFF) // Dark semi-transparent glassmorphic
            } else {
                Color(0xCCFFFFFF) // Light semi-transparent glassmorphic
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 8.dp else 4.dp
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) {
                Color(0x40FFFFFF) // Subtle white border
            } else {
                Color(0x40E0E0E0)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDarkTheme) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0x20FFFFFF),
                                Color(0x10000000)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0x20FFFFFF),
                                Color(0x10F5F5F5)
                            )
                        )
                    }
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Icon with gradient background
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent,
                shadowElevation = if (isDarkTheme) 8.dp else 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = gradientColors
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF212121)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF616161),
                lineHeight = 20.sp
            )
        }
    }
}
