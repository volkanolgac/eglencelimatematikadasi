package com.example.carpimadasi.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class MonsterColors(
    val body: Color,
    val belly: Color,
    val eye: Color = Color(0xFF1E293B)
)

private val MONSTER_PALETTES = listOf(
    MonsterColors(Color(0xFFC084FC), Color(0xFFF3E8FF)),
    MonsterColors(Color(0xFFFB7185), Color(0xFFFFE4E6)),
    MonsterColors(Color(0xFF34D399), Color(0xFFD1FAE5)),
    MonsterColors(Color(0xFFFBBF24), Color(0xFFFEF3C7)),
    MonsterColors(Color(0xFF60A5FA), Color(0xFFDBEAFE)),
    MonsterColors(Color(0xFFF472B6), Color(0xFFFCE7F3))
)

@Composable
fun MonsterView(
    modifier: Modifier = Modifier,
    variant: Int = 0,
    size: Dp = 105.dp,
    state: String = "idle" // "idle", "approach", "attack", "flee", "eat"
) {
    val colors = MONSTER_PALETTES[Math.floorMod(variant, MONSTER_PALETTES.size)]

    val infiniteTransition = rememberInfiniteTransition(label = "monster_anim")
    val idleBob by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (state == "idle" || state == "approach") -8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "monster_bob"
    )

    val chompScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (state == "eat") 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(250, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "monster_chomp"
    )

    val yOffset = when (state) {
        "flee" -> -20f
        "attack" -> -5f
        else -> idleBob
    }

    val xOffset = when (state) {
        "attack" -> -18f
        "flee" -> 15f
        else -> 0f
    }

    Box(
        modifier = modifier
            .size(size)
            .offset(x = xOffset.dp, y = yOffset.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val scale = (this.size.width / 100f) * chompScale
            drawMonster(colors, scale, state)
        }
    }
}

private fun DrawScope.drawMonster(
    colors: MonsterColors,
    s: Float,
    state: String
) {
    val strokeColor = Color(0xFF1E293B)
    val strokeW = 3f * s

    // Body shape
    val bodyPath = Path().apply {
        moveTo(50f * s, 15f * s)
        cubicTo(25f * s, 15f * s, 12f * s, 35f * s, 12f * s, 55f * s)
        cubicTo(12f * s, 75f * s, 25f * s, 88f * s, 50f * s, 88f * s)
        cubicTo(75f * s, 88f * s, 88f * s, 75f * s, 88f * s, 55f * s)
        cubicTo(88f * s, 35f * s, 75f * s, 15f * s, 50f * s, 15f * s)
        close()
    }
    drawPath(bodyPath, colors.body)
    drawPath(bodyPath, strokeColor, style = Stroke(strokeW))

    // Belly
    drawOval(
        color = colors.belly.copy(alpha = 0.85f),
        topLeft = Offset(26f * s, 40f * s),
        size = Size(48f * s, 40f * s)
    )

    // Horns
    val leftHorn = Path().apply {
        moveTo(30f * s, 18f * s)
        lineTo(26f * s, 6f * s)
        lineTo(38f * s, 14f * s)
        close()
    }
    drawPath(leftHorn, colors.body)
    drawPath(leftHorn, strokeColor, style = Stroke(2.5f * s))

    val rightHorn = Path().apply {
        moveTo(70f * s, 18f * s)
        lineTo(74f * s, 6f * s)
        lineTo(62f * s, 14f * s)
        close()
    }
    drawPath(rightHorn, colors.body)
    drawPath(rightHorn, strokeColor, style = Stroke(2.5f * s))

    // Eyes
    drawCircle(Color.White, radius = 10f * s, center = Offset(38f * s, 42f * s))
    drawCircle(strokeColor, radius = 10f * s, center = Offset(38f * s, 42f * s), style = Stroke(2.5f * s))
    drawCircle(Color.White, radius = 10f * s, center = Offset(62f * s, 42f * s))
    drawCircle(strokeColor, radius = 10f * s, center = Offset(62f * s, 42f * s), style = Stroke(2.5f * s))

    if (state == "eat" || state == "attack") {
        // Attack pupils
        drawCircle(colors.eye, radius = 5f * s, center = Offset(36f * s, 43f * s))
        drawCircle(colors.eye, radius = 5f * s, center = Offset(60f * s, 43f * s))
        drawCircle(Color.White, radius = 1.5f * s, center = Offset(38f * s, 41f * s))
        drawCircle(Color.White, radius = 1.5f * s, center = Offset(62f * s, 41f * s))

        // Big Chomp Mouth
        val mouth = Path().apply {
            moveTo(32f * s, 60f * s)
            quadraticTo(50f * s, 78f * s, 68f * s, 60f * s)
            quadraticTo(50f * s, 54f * s, 32f * s, 60f * s)
            close()
        }
        drawPath(mouth, Color(0xFFB91C1C))
        drawPath(mouth, strokeColor, style = Stroke(2.5f * s))

        // Teeth (upper)
        val teethPointsUpper = listOf(
            Triple(38f * s, 58f * s, 42f * s),
            Triple(46f * s, 58f * s, 50f * s),
            Triple(54f * s, 58f * s, 58f * s)
        )
        for ((x1, y1, x2) in teethPointsUpper) {
            val t = Path().apply {
                moveTo(x1, y1)
                lineTo((x1 + x2) / 2f + 2f * s, y1 + 8f * s)
                lineTo(x2 + 4f * s, y1)
                close()
            }
            drawPath(t, Color.White)
        }
    } else {
        // Normal eyes
        drawCircle(colors.eye, radius = 5f * s, center = Offset(38f * s, 44f * s))
        drawCircle(colors.eye, radius = 5f * s, center = Offset(62f * s, 44f * s))
        drawCircle(Color.White, radius = 1.8f * s, center = Offset(39.5f * s, 42f * s))
        drawCircle(Color.White, radius = 1.8f * s, center = Offset(63.5f * s, 42f * s))

        // Goofy teeth smile
        val smile = Path().apply {
            moveTo(35f * s, 62f * s)
            quadraticTo(50f * s, 74f * s, 65f * s, 62f * s)
        }
        drawPath(smile, strokeColor, style = Stroke(2.5f * s, cap = StrokeCap.Round))

        // 2 little buck teeth
        drawRoundRect(Color.White, topLeft = Offset(42f * s, 62f * s), size = Size(4f * s, 6f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f * s))
        drawRoundRect(strokeColor, topLeft = Offset(42f * s, 62f * s), size = Size(4f * s, 6f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f * s), style = Stroke(1.5f * s))
        drawRoundRect(Color.White, topLeft = Offset(54f * s, 62f * s), size = Size(4f * s, 6f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f * s))
        drawRoundRect(strokeColor, topLeft = Offset(54f * s, 62f * s), size = Size(4f * s, 6f * s), cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f * s), style = Stroke(1.5f * s))
    }

    // Feet
    drawOval(colors.body, topLeft = Offset(27f * s, 86f * s), size = Size(16f * s, 8f * s))
    drawOval(strokeColor, topLeft = Offset(27f * s, 86f * s), size = Size(16f * s, 8f * s), style = Stroke(2.5f * s))
    drawOval(colors.body, topLeft = Offset(57f * s, 86f * s), size = Size(16f * s, 8f * s))
    drawOval(strokeColor, topLeft = Offset(57f * s, 86f * s), size = Size(16f * s, 8f * s), style = Stroke(2.5f * s))
}
