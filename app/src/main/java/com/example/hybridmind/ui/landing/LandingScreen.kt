package com.example.hybridmind.ui.landing

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hybridmind.R
import com.example.hybridmind.data.ThemeMode
import com.example.hybridmind.data.ThemePreference
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit,
    onSignup: () -> Unit
) {
    val context = LocalContext.current
    val themePreference = remember { ThemePreference(context) }
    val currentThemeMode by themePreference.observeThemeMode().collectAsState(initial = ThemeMode.SYSTEM)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val systemDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val isDarkTheme = when (currentThemeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> systemDarkTheme
    }

    Scaffold(
        topBar = {
            // Top bar with theme toggle on left, Login/Signup on right
            TopAppBar(
                title = {
                    // Empty title - no "IREN" text
                    Spacer(modifier = Modifier.width(1.dp))
                },
                navigationIcon = {
                    // Theme toggle button on the left
                    IconButton(
                        onClick = {
                            scope.launch {
                                val nextMode = when (currentThemeMode) {
                                    ThemeMode.SYSTEM -> ThemeMode.DARK
                                    ThemeMode.DARK -> ThemeMode.LIGHT
                                    ThemeMode.LIGHT -> ThemeMode.SYSTEM
                                }
                                themePreference.saveThemeMode(nextMode)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = when (currentThemeMode) {
                                ThemeMode.SYSTEM -> Icons.Filled.SettingsBrightness
                                ThemeMode.DARK -> Icons.Filled.DarkMode
                                ThemeMode.LIGHT -> Icons.Filled.LightMode
                            },
                            contentDescription = when (currentThemeMode) {
                                ThemeMode.SYSTEM -> "Auto Theme"
                                ThemeMode.DARK -> "Dark Theme"
                                ThemeMode.LIGHT -> "Light Theme"
                            },
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Login button
                    TextButton(
                        onClick = onLogin,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Log In", fontWeight = FontWeight.Medium)
                    }
                    
                    // Signup button
                    Button(
                        onClick = onSignup,
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Sign Up")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) {
                        Color(0xFF0A0E27) // Match dark gradient start
                    } else {
                        Color(0xFFF0F4FF) // Match light gradient start
                    }
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
        ) {
            // Determine dark theme based on current theme mode setting
            val systemDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
            val isDarkTheme = when (currentThemeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> systemDarkTheme
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isDarkTheme) {
                            // Dark: Deep blue to purple gradient
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0A0E27),
                                    Color(0xFF1A1635),
                                    Color(0xFF2D1B4E)
                                )
                            )
                        } else {
                            // Light: White to pale blue gradient
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFF0F4FF),
                                    Color(0xFFE8F1FF),
                                    Color(0xFFFFFFFF)
                                )
                            )
                        }
                    )
            ) {
                Column {
                    // Hero Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 600.dp)
                            .padding(vertical = 32.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 3D Brain in Hexagon Container
                            Box(
                                modifier = Modifier.size(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Glassmorphic hexagon background
                                Surface(
                                    modifier = Modifier.size(180.dp),
                                    shape = CircleShape,
                                    color = if (isDarkTheme) {
                                        Color(0x4000D9FF) // Neon blue glow
                                    } else {
                                        Color(0x30B3E5FC) // Soft light blue glass
                                    },
                                    shadowElevation = if (isDarkTheme) 16.dp else 6.dp,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (isDarkTheme) {
                                            Color(0xFF00D9FF)
                                        } else {
                                            Color(0x50039BE5) // Visible blue border
                                        }
                                    )
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                if (isDarkTheme) {
                                                    Brush.radialGradient(
                                                        colors = listOf(
                                                            Color(0x8000D9FF),
                                                            Color(0x60AA00FF),
                                                            Color(0x20000000)
                                                        )
                                                    )
                                                } else {
                                                    Brush.radialGradient(
                                                        colors = listOf(
                                                            Color(0x4081D4FA), // Bright cyan-blue glow
                                                            Color(0x2003A9F4),
                                                            Color.Transparent
                                                        )
                                                    )
                                                }
                                            )
                                    ) {
                                        // IREN Logo
                                        Image(
                                            painter = painterResource(id = R.drawable.iren_logo),
                                            contentDescription = "IREN Logo",
                                            modifier = Modifier
                                                .size(120.dp)
                                                .alpha(0.9f)
                                                .clip(CircleShape)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))

                            // Welcome text
                            Text(
                                text = "Welcome to",
                                style = MaterialTheme.typography.headlineSmall,
                                color = if (isDarkTheme) Color.White else Color(0xFF424242),
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // HybridMind title
                            Text(
                                text = "IREN",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 52.sp
                                ),
                                color = if (isDarkTheme) Color.White else Color(0xFF212121)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // IREN Full Form
                            Text(
                                text = "Image Recognition & Explanation Network",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                ),
                                color = if (isDarkTheme) Color(0xFFBDBDBD) else Color(0xFF616161),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Main tagline
                            Text(
                                text = "AI Made Easy!",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp
                                ),
                                color = if (isDarkTheme) Color.White else Color(0xFF424242)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Subtitle
                            Text(
                                text = "Your Intelligent AI Companion.",
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF616161),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Online & Offline • Multimodal • Private",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isDarkTheme) Color(0xFFBDBDBD) else Color(0xFF757575),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(40.dp))

                            // Get Started Button with gradient
                            Button(
                                onClick = onGetStarted,
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .height(64.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(32.dp),
                                contentPadding = PaddingValues(0.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = if (isDarkTheme) 12.dp else 8.dp,
                                    pressedElevation = 4.dp
                                ),
                                border = if (isDarkTheme) {
                                    androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        Color(0xFF00D9FF)
                                    )
                                } else null
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            if (isDarkTheme) {
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xFF0066FF), // Deep blue
                                                        Color(0xFF00D9FF)  // Cyan glow
                                                    )
                                                )
                                            } else {
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xFF00BCD4), // Cyan
                                                        Color(0xFF2196F3)  // Blue
                                                    )
                                                )
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Get Started",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }
                    }

                    // Features Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 32.dp)
                    ) {
                        Text(
                            text = "Features",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color(0xFF212121),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // 2x2 Grid of feature cards
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Row 1
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                GlassmorphicFeatureCard(
                                    icon = Icons.Default.Image,
                                    title = "Multimodal AI",
                                    description = "Advanced AI for image and text understanding, online and offline.",
                                    gradientColors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6)),
                                    modifier = Modifier.weight(1f),
                                    isDarkTheme = isDarkTheme
                                )

                                GlassmorphicFeatureCard(
                                    icon = Icons.Default.OfflineBolt,
                                    title = "Works Offline",
                                    description = "Download AI models to your device. No internet needed.",
                                    gradientColors = listOf(Color(0xFF00FF88), Color(0xFF00D9A5)), // Neon green
                                    modifier = Modifier.weight(1f),
                                    isDarkTheme = isDarkTheme
                                )
                            }

                            // Row 2
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                GlassmorphicFeatureCard(
                                    icon = Icons.AutoMirrored.Filled.Chat,
                                    title = "Natural Conversations",
                                    description = "Chat naturally about images with persistent context.",
                                    gradientColors = listOf(Color(0xFFFF9800), Color(0xFFFFB74D)),
                                    modifier = Modifier.weight(1f),
                                    isDarkTheme = isDarkTheme
                                )

                                GlassmorphicFeatureCard(
                                    icon = Icons.Default.Android,
                                    title = "Privacy First",
                                    description = "Your data stays on your device in offline mode.",
                                    gradientColors = listOf(Color(0xFFE91E63), Color(0xFFAA00FF)), // Purple
                                    modifier = Modifier.weight(1f),
                                    isDarkTheme = isDarkTheme
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ModernFeatureItem(
    icon: ImageVector,
    title: String,
    description: String,
    isDarkTheme: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF2E2E2E) else Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with background circle
            Surface(
                shape = CircleShape,
                color = if (isDarkTheme) Color(0xFF424242) else Color(0xFFF5F5F5),
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF424242),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) Color(0xFFBDBDBD) else Color(0xFF757575),
                    lineHeight = 20.sp
                )
            }
        }
    }
}


