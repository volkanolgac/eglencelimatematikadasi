package com.example.carpimadasi.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carpimadasi.model.THEME_MAP

@Composable
fun AtmosphereBackground(
    themeId: String? = "theme_sky",
    content: @Composable () -> Unit
) {
    val theme = THEME_MAP[themeId ?: "theme_sky"] ?: THEME_MAP["theme_sky"]!!
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()

    val infiniteTransition = rememberInfiniteTransition(label = "clouds_anim")

    val cloud1X by infiniteTransition.animateFloat(
        initialValue = -150f,
        targetValue = screenWidth + 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud1"
    )

    val cloud2X by infiniteTransition.animateFloat(
        initialValue = -120f,
        targetValue = screenWidth + 120f,
        animationSpec = infiniteRepeatable(
            animation = tween(36000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud2"
    )

    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_twinkle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.brush)
    ) {
        if (theme.isSpace) {
            // Stars in space
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stars = listOf(
                    Offset(size.width * 0.15f, size.height * 0.08f) to 3f,
                    Offset(size.width * 0.82f, size.height * 0.12f) to 4f,
                    Offset(size.width * 0.30f, size.height * 0.28f) to 2.5f,
                    Offset(size.width * 0.70f, size.height * 0.36f) to 3.5f,
                    Offset(size.width * 0.12f, size.height * 0.55f) to 4f,
                    Offset(size.width * 0.60f, size.height * 0.70f) to 3f,
                    Offset(size.width * 0.88f, size.height * 0.82f) to 2.5f
                )
                for ((pos, r) in stars) {
                    drawCircle(Color.White.copy(alpha = starAlpha), radius = r, center = pos)
                }
            }

            // Floating Saturn Planet
            Text(
                text = "🪐",
                fontSize = 42.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-24).dp, y = 48.dp)
            )
        } else {
            // Sun for Sunset
            if (theme.isSunset) {
                Text(
                    text = "🌅",
                    fontSize = 44.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-24).dp, y = 48.dp)
                )
            }

            // Floating Clouds
            val cloudColor = if (theme.isSunset) Color(0xFFFED7AA).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.90f)

            // Cloud 1
            Canvas(
                modifier = Modifier
                    .offset(x = cloud1X.dp, y = 60.dp)
                    .size(width = 110.dp, height = 55.dp)
            ) {
                drawOval(cloudColor, topLeft = Offset(10f, 15f), size = Size(50f, 35f))
                drawOval(cloudColor, topLeft = Offset(35f, 5f), size = Size(55f, 45f))
                drawOval(cloudColor, topLeft = Offset(65f, 20f), size = Size(40f, 30f))
            }

            // Cloud 2
            Canvas(
                modifier = Modifier
                    .offset(x = cloud2X.dp, y = 240.dp)
                    .size(width = 90.dp, height = 45.dp)
            ) {
                drawOval(cloudColor.copy(alpha = 0.75f), topLeft = Offset(10f, 12f), size = Size(40f, 30f))
                drawOval(cloudColor.copy(alpha = 0.75f), topLeft = Offset(30f, 5f), size = Size(45f, 35f))
                drawOval(cloudColor.copy(alpha = 0.75f), topLeft = Offset(55f, 15f), size = Size(32f, 25f))
            }
        }

        // Screen content
        content()
    }
}
