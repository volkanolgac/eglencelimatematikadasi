package com.example.carpimadasi.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carpimadasi.model.COLOR_MAP
import com.example.carpimadasi.model.ExplorerColor
import com.example.carpimadasi.model.HAT_EMOJI
import com.example.carpimadasi.model.PET_EMOJI

@Composable
fun ExplorerView(
    modifier: Modifier = Modifier,
    avatar: String = "avatar_classic",
    color: String = "color_amber",
    hat: String? = null,
    pet: String? = null,
    size: Dp = 120.dp,
    animate: Boolean = true,
    kick: Boolean = false,
    eyes: String = "normal" // "normal", "x", "happy"
) {
    val explorerColor = COLOR_MAP[color] ?: COLOR_MAP["color_amber"]!!
    val hatEmoji = hat?.let { HAT_EMOJI[it] }
    val petEmoji = pet?.let { PET_EMOJI[it] }

    val infiniteTransition = rememberInfiniteTransition(label = "explorer_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (animate && !kick) -12f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )

    val yTranslation = if (kick) -35f else floatOffset

    Box(
        modifier = modifier
            .size(size)
            .offset(y = yTranslation.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val scale = this.size.width / 100f
            drawAvatarBody(avatar, explorerColor, scale, eyes)
            if (hat != null) {
                drawWearableHat(hat, scale, avatar)
            }
        }

        // Companion Pet Beside Explorer
        if (petEmoji != null) {
            Text(
                text = petEmoji,
                fontSize = (size.value * 0.28f).sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (size * 0.12f), y = (size * 0.05f))
            )
        }
    }
}

private fun DrawScope.drawWearableHat(hat: String, s: Float, avatar: String) {
    val strokeColor = Color(0xFF1E293B)
    val headY = when (avatar) {
        "avatar_astro" -> 16f * s
        "avatar_knight" -> 18f * s
        "avatar_robot" -> 14f * s
        else -> 18f * s
    }

    when (hat) {
        "hat_pirate" -> {
            // Pirate Captain Hat (Tricorn)
            val piratePath = Path().apply {
                moveTo(22f * s, headY + 4f * s)
                quadraticTo(50f * s, headY - 14f * s, 78f * s, headY + 4f * s)
                quadraticTo(68f * s, headY + 8f * s, 50f * s, headY + 6f * s)
                quadraticTo(32f * s, headY + 8f * s, 22f * s, headY + 4f * s)
                close()
            }
            drawPath(piratePath, Color(0xFF1E293B))
            drawPath(piratePath, strokeColor, style = Stroke(2f * s))

            // Gold Brim Trim
            val trimPath = Path().apply {
                moveTo(24f * s, headY + 4f * s)
                quadraticTo(50f * s, headY - 12f * s, 76f * s, headY + 4f * s)
            }
            drawPath(trimPath, Color(0xFFFBBF24), style = Stroke(2.2f * s, cap = StrokeCap.Round))

            // White Pirate Skull / Cross emblem in center
            drawCircle(Color.White, radius = 3.2f * s, center = Offset(50f * s, headY - 2f * s))
            drawLine(Color.White, Offset(46f * s, headY + 3f * s), Offset(54f * s, headY - 7f * s), strokeWidth = 1.5f * s)
            drawLine(Color.White, Offset(54f * s, headY + 3f * s), Offset(46f * s, headY - 7f * s), strokeWidth = 1.5f * s)
        }

        "hat_grad" -> {
            // Bilge Kepi (Graduation Cap - 🎓)
            // Underneath Skull Cap base
            val baseCap = Path().apply {
                moveTo(35f * s, headY + 3f * s)
                quadraticTo(50f * s, headY + 8f * s, 65f * s, headY + 3f * s)
                lineTo(64f * s, headY - 3f * s)
                lineTo(36f * s, headY - 3f * s)
                close()
            }
            drawPath(baseCap, Color(0xFF0F172A))
            drawPath(baseCap, strokeColor, style = Stroke(1.8f * s))

            // Diamond Mortarboard Top
            val mortarPath = Path().apply {
                moveTo(50f * s, headY - 14f * s) // Top
                lineTo(76f * s, headY - 5f * s)  // Right
                lineTo(50f * s, headY + 3f * s)  // Bottom
                lineTo(24f * s, headY - 5f * s)  // Left
                close()
            }
            drawPath(mortarPath, Color(0xFF1E293B))
            drawPath(mortarPath, strokeColor, style = Stroke(2f * s))

            // Center Button
            drawCircle(Color(0xFFFBBF24), radius = 2.6f * s, center = Offset(50f * s, headY - 5f * s))

            // Golden Hanging Tassel
            val tasselPath = Path().apply {
                moveTo(50f * s, headY - 5f * s)
                quadraticTo(68f * s, headY - 2f * s, 74f * s, headY + 9f * s)
            }
            drawPath(tasselPath, Color(0xFFFACC15), style = Stroke(2.2f * s, cap = StrokeCap.Round))
            drawCircle(Color(0xFFEAB308), radius = 3.2f * s, center = Offset(74f * s, headY + 10f * s))
        }

        "hat_beanie" -> {
            // Kış Beresi (Warm Knitted Beanie - 🧶)
            val beaniePath = Path().apply {
                moveTo(28f * s, headY + 4f * s)
                quadraticTo(27f * s, headY - 15f * s, 50f * s, headY - 16f * s)
                quadraticTo(73f * s, headY - 15f * s, 72f * s, headY + 4f * s)
                close()
            }
            drawPath(beaniePath, Color(0xFFEF4444))
            drawPath(beaniePath, strokeColor, style = Stroke(2f * s))

            // Knit Texture Ribs
            drawLine(Color(0xFFFCA5A5), Offset(42f * s, headY - 13f * s), Offset(42f * s, headY + 1f * s), strokeWidth = 2f * s)
            drawLine(Color(0xFFFCA5A5), Offset(50f * s, headY - 14f * s), Offset(50f * s, headY + 1f * s), strokeWidth = 2f * s)
            drawLine(Color(0xFFFCA5A5), Offset(58f * s, headY - 13f * s), Offset(58f * s, headY + 1f * s), strokeWidth = 2f * s)

            // Folded Bottom Brim Cuff
            drawRoundRect(
                color = Color(0xFFDC2626),
                topLeft = Offset(24f * s, headY - 1f * s),
                size = Size(52f * s, 8f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * s)
            )
            drawRoundRect(
                color = strokeColor,
                topLeft = Offset(24f * s, headY - 1f * s),
                size = Size(52f * s, 8f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * s),
                style = Stroke(1.8f * s)
            )

            // Fluffy Top Pom-Pom
            drawCircle(Color(0xFFFEF08A), radius = 5.2f * s, center = Offset(50f * s, headY - 17f * s))
            drawCircle(strokeColor, radius = 5.2f * s, center = Offset(50f * s, headY - 17f * s), style = Stroke(1.8f * s))
        }

        "hat_cowboy" -> {
            // Cowboy Hat Brim
            drawOval(
                color = Color(0xFF92400E),
                topLeft = Offset(20f * s, headY),
                size = Size(60f * s, 14f * s)
            )
            drawOval(
                color = strokeColor,
                topLeft = Offset(20f * s, headY),
                size = Size(60f * s, 14f * s),
                style = Stroke(2f * s)
            )

            // Cowboy Hat Crown (Top)
            val crownPath = Path().apply {
                moveTo(32f * s, headY + 6f * s)
                lineTo(34f * s, headY - 10f * s)
                quadraticTo(50f * s, headY - 7f * s, 66f * s, headY - 10f * s)
                lineTo(68f * s, headY + 6f * s)
                close()
            }
            drawPath(crownPath, Color(0xFFB45309))
            drawPath(crownPath, strokeColor, style = Stroke(2f * s))

            // Hat Band
            drawLine(
                Color(0xFFFEF3C7),
                Offset(33f * s, headY + 3f * s),
                Offset(67f * s, headY + 3f * s),
                strokeWidth = 2.5f * s,
                cap = StrokeCap.Round
            )
        }

        "hat_crown" -> {
            // Gold King Crown
            val crownPath = Path().apply {
                moveTo(30f * s, headY + 4f * s)
                lineTo(28f * s, headY - 12f * s)
                lineTo(38f * s, headY - 4f * s)
                lineTo(50f * s, headY - 16f * s)
                lineTo(62f * s, headY - 4f * s)
                lineTo(72f * s, headY - 12f * s)
                lineTo(70f * s, headY + 4f * s)
                close()
            }
            drawPath(crownPath, Color(0xFFFACC15))
            drawPath(crownPath, strokeColor, style = Stroke(2f * s))

            // Crown Jewels
            drawCircle(Color(0xFFEF4444), radius = 2.2f * s, center = Offset(50f * s, headY - 14f * s))
            drawCircle(Color(0xFF38BDF8), radius = 2f * s, center = Offset(30f * s, headY - 10f * s))
            drawCircle(Color(0xFF34D399), radius = 2f * s, center = Offset(70f * s, headY - 10f * s))
            drawCircle(Color(0xFFEC4899), radius = 2f * s, center = Offset(50f * s, headY))
        }

        "hat_cap" -> {
            // Baseball Cap Dome
            val domePath = Path().apply {
                moveTo(30f * s, headY + 5f * s)
                quadraticTo(50f * s, headY - 14f * s, 70f * s, headY + 5f * s)
                close()
            }
            drawPath(domePath, Color(0xFF0284C7))
            drawPath(domePath, strokeColor, style = Stroke(2f * s))

            // Front Visor
            val visorPath = Path().apply {
                moveTo(28f * s, headY + 4f * s)
                quadraticTo(14f * s, headY + 9f * s, 34f * s, headY + 11f * s)
                lineTo(48f * s, headY + 5f * s)
                close()
            }
            drawPath(visorPath, Color(0xFF0369A1))
            drawPath(visorPath, strokeColor, style = Stroke(1.8f * s))

            // Top Button
            drawCircle(Color(0xFFFACC15), radius = 2.5f * s, center = Offset(50f * s, headY - 6f * s))
        }

        "hat_party" -> {
            // Party Cone Hat
            val conePath = Path().apply {
                moveTo(34f * s, headY + 5f * s)
                lineTo(50f * s, headY - 20f * s)
                lineTo(66f * s, headY + 5f * s)
                close()
            }
            drawPath(conePath, Color(0xFFEC4899))
            drawPath(conePath, strokeColor, style = Stroke(2f * s))

            // Colorful Stripes
            drawLine(Color(0xFFFBBF24), Offset(40f * s, headY - 5f * s), Offset(60f * s, headY - 5f * s), strokeWidth = 3f * s)
            drawLine(Color(0xFF38BDF8), Offset(44f * s, headY - 12f * s), Offset(56f * s, headY - 12f * s), strokeWidth = 2.5f * s)

            // Top Pom-Pom Ball
            drawCircle(Color(0xFFFBBF24), radius = 4f * s, center = Offset(50f * s, headY - 21f * s))
            drawCircle(strokeColor, radius = 4f * s, center = Offset(50f * s, headY - 21f * s), style = Stroke(1.5f * s))
        }
    }
}

private fun DrawScope.drawAvatarBody(
    avatar: String,
    color: ExplorerColor,
    s: Float,
    eyes: String
) {
    val strokeColor = Color(0xFF1E293B)
    val strokeW = 2.6f * s

    when (avatar) {
        "avatar_astro" -> {
            // Space suit backpack
            drawRoundRect(
                color = Color(0xFFCBD5E1),
                topLeft = Offset(22f * s, 45f * s),
                size = Size(56f * s, 38f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s)
            )
            drawRoundRect(
                color = strokeColor,
                topLeft = Offset(22f * s, 45f * s),
                size = Size(56f * s, 38f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s),
                style = Stroke(width = strokeW)
            )

            // Body
            drawOval(
                color = Color(0xFFF8FAFC),
                topLeft = Offset(20f * s, 38f * s),
                size = Size(60f * s, 52f * s)
            )
            drawOval(
                color = strokeColor,
                topLeft = Offset(20f * s, 38f * s),
                size = Size(60f * s, 52f * s),
                style = Stroke(width = strokeW)
            )

            // Control panel
            drawRoundRect(
                color = color.body,
                topLeft = Offset(38f * s, 58f * s),
                size = Size(24f * s, 14f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * s)
            )
            drawRoundRect(
                color = strokeColor,
                topLeft = Offset(38f * s, 58f * s),
                size = Size(24f * s, 14f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * s),
                style = Stroke(width = 1.8f * s)
            )
            drawCircle(Color(0xFF38BDF8), radius = 2.5f * s, center = Offset(44f * s, 65f * s))
            drawCircle(Color(0xFFFB7185), radius = 2.5f * s, center = Offset(56f * s, 65f * s))

            // Feet
            drawOval(Color(0xFF64748B), topLeft = Offset(29f * s, 83f * s), size = Size(18f * s, 12f * s))
            drawOval(strokeColor, topLeft = Offset(29f * s, 83f * s), size = Size(18f * s, 12f * s), style = Stroke(strokeW))
            drawOval(Color(0xFF64748B), topLeft = Offset(53f * s, 83f * s), size = Size(18f * s, 12f * s))
            drawOval(strokeColor, topLeft = Offset(53f * s, 83f * s), size = Size(18f * s, 12f * s), style = Stroke(strokeW))

            // Helmet
            drawCircle(Color(0xFFF1F5F9), radius = 26f * s, center = Offset(50f * s, 34f * s))
            drawCircle(strokeColor, radius = 26f * s, center = Offset(50f * s, 34f * s), style = Stroke(strokeW))

            // Visor
            drawOval(color.body, topLeft = Offset(30f * s, 19f * s), size = Size(40f * s, 32f * s))
            drawOval(Color(0xFF0F172A).copy(alpha = 0.35f), topLeft = Offset(32f * s, 21f * s), size = Size(36f * s, 28f * s))
            drawOval(strokeColor, topLeft = Offset(30f * s, 19f * s), size = Size(40f * s, 32f * s), style = Stroke(2f * s))

            // Cheeks
            drawCircle(color.cheek.copy(alpha = 0.7f), radius = 3.5f * s, center = Offset(36f * s, 41f * s))
            drawCircle(color.cheek.copy(alpha = 0.7f), radius = 3.5f * s, center = Offset(64f * s, 41f * s))

            // Antenna
            drawLine(strokeColor, Offset(50f * s, 8f * s), Offset(50f * s, 2f * s), strokeWidth = 2.5f * s, cap = StrokeCap.Round)
            drawCircle(Color(0xFFFBBF24), radius = 3.5f * s, center = Offset(50f * s, 2f * s))
            drawCircle(strokeColor, radius = 3.5f * s, center = Offset(50f * s, 2f * s), style = Stroke(1.5f * s))
        }

        "avatar_panda" -> {
            // Panda Ears
            drawCircle(strokeColor, radius = 9f * s, center = Offset(28f * s, 18f * s))
            drawCircle(Color(0xFFE2E8F0), radius = 4.5f * s, center = Offset(28f * s, 18f * s))
            drawCircle(strokeColor, radius = 9f * s, center = Offset(72f * s, 18f * s))
            drawCircle(Color(0xFFE2E8F0), radius = 4.5f * s, center = Offset(72f * s, 18f * s))

            // Body
            drawOval(Color(0xFFF8FAFC), topLeft = Offset(18f * s, 34f * s), size = Size(64f * s, 56f * s))
            drawOval(strokeColor, topLeft = Offset(18f * s, 34f * s), size = Size(64f * s, 56f * s), style = Stroke(strokeW))
            drawOval(color.body.copy(alpha = 0.85f), topLeft = Offset(28f * s, 50f * s), size = Size(44f * s, 36f * s))

            // Feet
            drawOval(strokeColor, topLeft = Offset(30f * s, 83f * s), size = Size(16f * s, 10f * s))
            drawOval(strokeColor, topLeft = Offset(54f * s, 83f * s), size = Size(16f * s, 10f * s))

            // Arms
            drawOval(strokeColor, topLeft = Offset(13f * s, 48f * s), size = Size(14f * s, 24f * s))
            drawOval(strokeColor, topLeft = Offset(73f * s, 48f * s), size = Size(14f * s, 24f * s))

            // Head
            drawCircle(Color(0xFFF8FAFC), radius = 24f * s, center = Offset(50f * s, 36f * s))
            drawCircle(strokeColor, radius = 24f * s, center = Offset(50f * s, 36f * s), style = Stroke(strokeW))

            // Eye Patches
            drawOval(strokeColor, topLeft = Offset(32f * s, 25f * s), size = Size(14f * s, 18f * s))
            drawOval(strokeColor, topLeft = Offset(54f * s, 25f * s), size = Size(14f * s, 18f * s))

            // Cheeks & Nose
            drawCircle(color.cheek.copy(alpha = 0.6f), radius = 5f * s, center = Offset(33f * s, 43f * s))
            drawCircle(color.cheek.copy(alpha = 0.6f), radius = 5f * s, center = Offset(67f * s, 43f * s))
            drawOval(strokeColor, topLeft = Offset(46.5f * s, 38.5f * s), size = Size(7f * s, 5f * s))
        }

        "avatar_superhero", "avatar_wizard" -> {
            // Superhero Red Cape
            val capePath = Path().apply {
                moveTo(20f * s, 48f * s)
                quadraticTo(50f * s, 38f * s, 80f * s, 48f * s)
                lineTo(92f * s, 88f * s)
                lineTo(8f * s, 88f * s)
                close()
            }
            drawPath(capePath, Color(0xFFEF4444))
            drawPath(capePath, strokeColor, style = Stroke(strokeW))

            // Superhero Suit Body
            drawOval(color.body, topLeft = Offset(20f * s, 36f * s), size = Size(60f * s, 52f * s))
            drawOval(strokeColor, topLeft = Offset(20f * s, 36f * s), size = Size(60f * s, 52f * s), style = Stroke(strokeW))

            // Golden Hero Belt
            drawRoundRect(
                Color(0xFFFBBF24),
                topLeft = Offset(30f * s, 68f * s),
                size = Size(40f * s, 8f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f * s)
            )
            drawRoundRect(
                strokeColor,
                topLeft = Offset(30f * s, 68f * s),
                size = Size(40f * s, 8f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f * s),
                style = Stroke(1.5f * s)
            )

            // Superhero Chest Emblem (Star / Diamond Badge)
            val emblemPath = Path().apply {
                moveTo(50f * s, 48f * s)
                lineTo(58f * s, 56f * s)
                lineTo(50f * s, 64f * s)
                lineTo(42f * s, 56f * s)
                close()
            }
            drawPath(emblemPath, Color(0xFFFACC15))
            drawPath(emblemPath, strokeColor, style = Stroke(1.5f * s))

            // Boots
            drawOval(Color(0xFFDC2626), topLeft = Offset(30f * s, 83f * s), size = Size(16f * s, 10f * s))
            drawOval(strokeColor, topLeft = Offset(30f * s, 83f * s), size = Size(16f * s, 10f * s), style = Stroke(strokeW))
            drawOval(Color(0xFFDC2626), topLeft = Offset(54f * s, 83f * s), size = Size(16f * s, 10f * s))
            drawOval(strokeColor, topLeft = Offset(54f * s, 83f * s), size = Size(16f * s, 10f * s), style = Stroke(strokeW))

            // Head
            drawCircle(color.body, radius = 24f * s, center = Offset(50f * s, 36f * s))
            drawCircle(strokeColor, radius = 24f * s, center = Offset(50f * s, 36f * s), style = Stroke(strokeW))

            // Hero Mask
            val maskPath = Path().apply {
                moveTo(28f * s, 34f * s)
                quadraticTo(50f * s, 30f * s, 72f * s, 34f * s)
                quadraticTo(74f * s, 42f * s, 64f * s, 44f * s)
                quadraticTo(50f * s, 38f * s, 36f * s, 44f * s)
                quadraticTo(26f * s, 42f * s, 28f * s, 34f * s)
                close()
            }
            drawPath(maskPath, Color(0xFF1E293B))

            // Cheeks
            drawCircle(color.cheek.copy(alpha = 0.6f), radius = 5f * s, center = Offset(34f * s, 44f * s))
            drawCircle(color.cheek.copy(alpha = 0.6f), radius = 5f * s, center = Offset(66f * s, 44f * s))
        }

        "avatar_knight" -> {
            // Shoulders
            drawCircle(Color(0xFF94A3B8), radius = 9f * s, center = Offset(20f * s, 52f * s))
            drawCircle(strokeColor, radius = 9f * s, center = Offset(20f * s, 52f * s), style = Stroke(2f * s))
            drawCircle(Color(0xFF94A3B8), radius = 9f * s, center = Offset(80f * s, 52f * s))
            drawCircle(strokeColor, radius = 9f * s, center = Offset(80f * s, 52f * s), style = Stroke(2f * s))

            // Body Armor
            drawOval(Color(0xFFCBD5E1), topLeft = Offset(20f * s, 36f * s), size = Size(60f * s, 52f * s))
            drawOval(strokeColor, topLeft = Offset(20f * s, 36f * s), size = Size(60f * s, 52f * s), style = Stroke(strokeW))
            drawOval(color.body, topLeft = Offset(32f * s, 50f * s), size = Size(36f * s, 30f * s))

            // Shield emblem
            val shield = Path().apply {
                moveTo(50f * s, 58f * s)
                lineTo(58f * s, 62f * s)
                lineTo(50f * s, 72f * s)
                lineTo(42f * s, 62f * s)
                close()
            }
            drawPath(shield, Color(0xFFEF4444))
            drawPath(shield, strokeColor, style = Stroke(1.5f * s))

            // Helmet
            drawCircle(Color(0xFF94A3B8), radius = 24f * s, center = Offset(50f * s, 36f * s))
            drawCircle(strokeColor, radius = 24f * s, center = Offset(50f * s, 36f * s), style = Stroke(strokeW))
            drawRoundRect(Color(0xFFE2E8F0), topLeft = Offset(30f * s, 24f * s), size = Size(40f * s, 22f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * s))
            drawRoundRect(strokeColor, topLeft = Offset(30f * s, 24f * s), size = Size(40f * s, 22f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * s), style = Stroke(2f * s))

            // Feather Plume
            val plume = Path().apply {
                moveTo(50f * s, 14f * s)
                quadraticTo(60f * s, 2f * s, 68f * s, 8f * s)
                quadraticTo(56f * s, 12f * s, 50f * s, 16f * s)
                close()
            }
            drawPath(plume, Color(0xFFEF4444))

            // Cheeks
            drawCircle(color.cheek.copy(alpha = 0.6f), radius = 4f * s, center = Offset(35f * s, 40f * s))
            drawCircle(color.cheek.copy(alpha = 0.6f), radius = 4f * s, center = Offset(65f * s, 40f * s))
        }

        "avatar_robot" -> {
            // Antenna
            drawLine(strokeColor, Offset(50f * s, 14f * s), Offset(50f * s, 3f * s), strokeWidth = 2.5f * s)
            drawCircle(Color(0xFF38BDF8), radius = 4f * s, center = Offset(50f * s, 3f * s))
            drawCircle(strokeColor, radius = 4f * s, center = Offset(50f * s, 3f * s), style = Stroke(1.5f * s))

            // Ears
            drawRoundRect(Color(0xFF94A3B8), topLeft = Offset(18f * s, 28f * s), size = Size(6f * s, 14f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * s))
            drawRoundRect(Color(0xFF94A3B8), topLeft = Offset(76f * s, 28f * s), size = Size(6f * s, 14f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * s))

            // Body
            drawRoundRect(color.body, topLeft = Offset(22f * s, 44f * s), size = Size(56f * s, 40f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f * s))
            drawRoundRect(strokeColor, topLeft = Offset(22f * s, 44f * s), size = Size(56f * s, 40f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f * s), style = Stroke(strokeW))

            // Chest Screen
            drawRoundRect(Color(0xFF0F172A), topLeft = Offset(34f * s, 54f * s), size = Size(32f * s, 20f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * s))
            val wave = Path().apply {
                moveTo(38f * s, 64f * s)
                lineTo(44f * s, 58f * s)
                lineTo(50f * s, 68f * s)
                lineTo(56f * s, 60f * s)
                lineTo(62f * s, 64f * s)
            }
            drawPath(wave, Color(0xFF34D399), style = Stroke(width = 2f * s, cap = StrokeCap.Round, join = StrokeJoin.Round))

            // Head Screen
            drawRoundRect(Color(0xFFF1F5F9), topLeft = Offset(26f * s, 14f * s), size = Size(48f * s, 34f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f * s))
            drawRoundRect(strokeColor, topLeft = Offset(26f * s, 14f * s), size = Size(48f * s, 34f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f * s), style = Stroke(strokeW))

            // Cheeks
            drawCircle(color.cheek.copy(alpha = 0.6f), radius = 4f * s, center = Offset(34f * s, 40f * s))
            drawCircle(color.cheek.copy(alpha = 0.6f), radius = 4f * s, center = Offset(66f * s, 40f * s))
        }

        else -> { // classic Leo
            // Body
            drawOval(color.body, topLeft = Offset(18f * s, 34f * s), size = Size(64f * s, 56f * s))
            drawOval(strokeColor, topLeft = Offset(18f * s, 34f * s), size = Size(64f * s, 56f * s), style = Stroke(strokeW))
            drawOval(Color.White.copy(alpha = 0.85f), topLeft = Offset(28f * s, 50f * s), size = Size(44f * s, 36f * s))

            // Feet
            drawOval(color.body, topLeft = Offset(30f * s, 83f * s), size = Size(16f * s, 10f * s))
            drawOval(strokeColor, topLeft = Offset(30f * s, 83f * s), size = Size(16f * s, 10f * s), style = Stroke(strokeW))
            drawOval(color.body, topLeft = Offset(54f * s, 83f * s), size = Size(16f * s, 10f * s))
            drawOval(strokeColor, topLeft = Offset(54f * s, 83f * s), size = Size(16f * s, 10f * s), style = Stroke(strokeW))

            // Arms
            drawOval(color.body, topLeft = Offset(13f * s, 48f * s), size = Size(14f * s, 24f * s))
            drawOval(strokeColor, topLeft = Offset(13f * s, 48f * s), size = Size(14f * s, 24f * s), style = Stroke(strokeW))
            drawOval(color.body, topLeft = Offset(73f * s, 48f * s), size = Size(14f * s, 24f * s))
            drawOval(strokeColor, topLeft = Offset(73f * s, 48f * s), size = Size(14f * s, 24f * s), style = Stroke(strokeW))

            // Head
            drawCircle(color.body, radius = 24f * s, center = Offset(50f * s, 36f * s))
            drawCircle(strokeColor, radius = 24f * s, center = Offset(50f * s, 36f * s), style = Stroke(strokeW))

            // Ears
            drawCircle(color.body, radius = 7f * s, center = Offset(30f * s, 20f * s))
            drawCircle(strokeColor, radius = 7f * s, center = Offset(30f * s, 20f * s), style = Stroke(strokeW))
            drawCircle(color.body, radius = 7f * s, center = Offset(70f * s, 20f * s))
            drawCircle(strokeColor, radius = 7f * s, center = Offset(70f * s, 20f * s), style = Stroke(strokeW))

            // Cheeks
            drawCircle(color.cheek.copy(alpha = 0.6f), radius = 5f * s, center = Offset(34f * s, 42f * s))
            drawCircle(color.cheek.copy(alpha = 0.6f), radius = 5f * s, center = Offset(66f * s, 42f * s))
        }
    }

    // EYES & MOUTH RENDERING
    when (eyes) {
        "x" -> {
            // White disc backings
            drawCircle(Color.White, radius = 5.5f * s, center = Offset(40f * s, 34f * s))
            drawCircle(strokeColor, radius = 5.5f * s, center = Offset(40f * s, 34f * s), style = Stroke(1.2f * s))
            drawCircle(Color.White, radius = 5.5f * s, center = Offset(60f * s, 34f * s))
            drawCircle(strokeColor, radius = 5.5f * s, center = Offset(60f * s, 34f * s), style = Stroke(1.2f * s))

            // Black X marks
            val xStroke = 2.8f * s
            drawLine(strokeColor, Offset(37f * s, 31f * s), Offset(43f * s, 37f * s), strokeWidth = xStroke, cap = StrokeCap.Round)
            drawLine(strokeColor, Offset(43f * s, 31f * s), Offset(37f * s, 37f * s), strokeWidth = xStroke, cap = StrokeCap.Round)
            drawLine(strokeColor, Offset(57f * s, 31f * s), Offset(63f * s, 37f * s), strokeWidth = xStroke, cap = StrokeCap.Round)
            drawLine(strokeColor, Offset(63f * s, 31f * s), Offset(57f * s, 37f * s), strokeWidth = xStroke, cap = StrokeCap.Round)

            // Dizzy wavy mouth
            val dizzyMouth = Path().apply {
                moveTo(42f * s, 46f * s)
                quadraticTo(46f * s, 43f * s, 50f * s, 46f * s)
                quadraticTo(54f * s, 49f * s, 58f * s, 46f * s)
            }
            drawPath(dizzyMouth, strokeColor, style = Stroke(width = 2.5f * s, cap = StrokeCap.Round))
        }
        "happy" -> {
            val leftHappyEye = Path().apply {
                moveTo(36f * s, 34f * s)
                quadraticTo(40f * s, 28f * s, 44f * s, 34f * s)
            }
            val rightHappyEye = Path().apply {
                moveTo(56f * s, 34f * s)
                quadraticTo(60f * s, 28f * s, 64f * s, 34f * s)
            }
            drawPath(leftHappyEye, strokeColor, style = Stroke(width = 3f * s, cap = StrokeCap.Round))
            drawPath(rightHappyEye, strokeColor, style = Stroke(width = 3f * s, cap = StrokeCap.Round))

            val happySmile = Path().apply {
                moveTo(42f * s, 43f * s)
                quadraticTo(50f * s, 51f * s, 58f * s, 43f * s)
                close()
            }
            drawPath(happySmile, Color(0xFFF43F5E))
            drawPath(happySmile, strokeColor, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
        }
        else -> { // normal cute sparkles
            drawCircle(strokeColor, radius = 4.5f * s, center = Offset(40f * s, 34f * s))
            drawCircle(strokeColor, radius = 4.5f * s, center = Offset(60f * s, 34f * s))
            drawCircle(Color.White, radius = 1.8f * s, center = Offset(41.5f * s, 32.5f * s))
            drawCircle(Color.White, radius = 1.8f * s, center = Offset(61.5f * s, 32.5f * s))

            val smile = Path().apply {
                moveTo(42f * s, 44f * s)
                quadraticTo(50f * s, 50f * s, 58f * s, 44f * s)
            }
            drawPath(smile, strokeColor, style = Stroke(width = 2.5f * s, cap = StrokeCap.Round))
        }
    }
}
