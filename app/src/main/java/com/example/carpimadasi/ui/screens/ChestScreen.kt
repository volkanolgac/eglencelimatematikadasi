package com.example.carpimadasi.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carpimadasi.logic.SoundManager
import com.example.carpimadasi.model.BADGE_INFO
import com.example.carpimadasi.model.GameResult
import com.example.carpimadasi.ui.components.Big3DButton
import com.example.carpimadasi.ui.components.StarsDisplay

@Composable
fun ChestScreen(
    result: GameResult,
    newBadgeKey: String?,
    soundManager: SoundManager,
    onContinue: () -> Unit
) {
    LaunchedEffect(Unit) {
        soundManager.victory()
        soundManager.chest()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "chest_bounce")
    val chestY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chest_y"
    )

    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val isCompactHeight = maxHeight < 640.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = if (isCompactHeight) 16.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title & Stars Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🎉 HARİKA İŞ! 🎉",
                    fontSize = if (isCompactHeight) 24.sp else 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Bölümü Başarıyla Tamamladın",
                    fontSize = if (isCompactHeight) 14.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0284C7),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(if (isCompactHeight) 10.dp else 16.dp))

                StarsDisplay(stars = result.stars, starSize = if (isCompactHeight) 30 else 36)
            }

            // Animated Treasure Chest & Diamond Reward Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🎁",
                    fontSize = if (isCompactHeight) 56.sp else 72.sp,
                    modifier = Modifier.offset(y = chestY.dp)
                )

                Spacer(modifier = Modifier.height(if (isCompactHeight) 10.dp else 16.dp))

                // Diamonds Earned Card
                Box(
                    modifier = Modifier
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("💎", fontSize = 26.sp)
                        Text(
                            text = "+${result.diamonds} Elmas!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0284C7)
                        )
                    }
                }

                if (result.bestStreak > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "En Yüksek Seri: 🔥 ${result.bestStreak}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFD97706)
                    )
                }

                // New Badge Alert
                if (newBadgeKey != null) {
                    val badge = BADGE_INFO[newBadgeKey]
                    if (badge != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .shadow(6.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(badge.emoji, fontSize = 20.sp)
                                Text(
                                    text = "Yeni Rozet: ${badge.name}!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            }

            // Continue Button
            Big3DButton(
                text = "Devam Et",
                emoji = "➡️",
                testTag = "chest_continue_button",
                height = if (isCompactHeight) 54.dp else 60.dp,
                gradientColors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                shadowColor = Color(0xFF0369A1),
                onClick = onContinue
            )
        }
    }
}
