package com.example.carpimadasi.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Big3DButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "big_button",
    emoji: String? = null,
    gradientColors: List<Color> = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
    shadowColor: Color = Color(0xFF0369A1),
    textColor: Color = Color.White,
    fontSize: Int = 20,
    height: Dp = 62.dp,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateFloatAsState(targetValue = if (isPressed) 4f else 0f, label = "press_anim")

    Box(
        modifier = modifier
            .testTag(testTag)
            .offset(y = pressOffset.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Shadow base
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (6 - pressOffset).coerceAtLeast(0f).dp)
                .background(shadowColor, RoundedCornerShape(26.dp))
                .padding(vertical = height / 2)
        )

        // Main face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Brush.verticalGradient(gradientColors))
                .padding(horizontal = 8.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (emoji != null) {
                    Text(text = emoji, fontSize = (fontSize + 2).sp, modifier = Modifier.padding(end = 4.dp))
                }
                Text(
                    text = text,
                    color = textColor,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
fun DiamondBadge(
    count: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.92f))
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text("💎", fontSize = 18.sp, modifier = Modifier.padding(end = 4.dp))
        Text(
            text = "$count",
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0284C7)
        )
    }
}

@Composable
fun StreakBadge(
    streak: Int,
    modifier: Modifier = Modifier,
    showSuffix: Boolean = true
) {
    if (streak <= 0) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Color(0xFFFEF3C7).copy(alpha = 0.95f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text("🔥", fontSize = 16.sp, modifier = Modifier.padding(end = 3.dp))
        Text(
            text = if (showSuffix) "$streak gün" else "$streak",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFD97706)
        )
    }
}

@Composable
fun StarsDisplay(
    stars: Int,
    modifier: Modifier = Modifier,
    starSize: Int = 24
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..3) {
            Text(
                text = if (i <= stars) "⭐" else "☆",
                fontSize = starSize.sp
            )
        }
    }
}
