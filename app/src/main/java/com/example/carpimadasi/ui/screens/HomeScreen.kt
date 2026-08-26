package com.example.carpimadasi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carpimadasi.model.SaveState
import com.example.carpimadasi.ui.components.Big3DButton
import com.example.carpimadasi.ui.components.MatematikAdasiLogo
import com.example.carpimadasi.ui.components.DiamondBadge
import com.example.carpimadasi.ui.components.ExplorerView
import com.example.carpimadasi.ui.components.StreakBadge

@Composable
fun HomeScreen(
    saveState: SaveState,
    onNavigateToWorlds: () -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToParent: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight
        val isCompactHeight = screenHeight < 640.dp
        val avatarSize = if (isCompactHeight) 115.dp else 145.dp
        val topPadding = if (isCompactHeight) 12.dp else 20.dp
        val bottomPadding = if (isCompactHeight) 16.dp else 24.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .padding(top = topPadding, bottom = bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    DiamondBadge(count = saveState.diamonds, onClick = onNavigateToShop)
                    StreakBadge(streak = saveState.streak)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .testTag("achievements_top_button")
                            .size(44.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.92f))
                            .clickable(onClick = onNavigateToAchievements),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏆", fontSize = 20.sp)
                    }

                    Box(
                        modifier = Modifier
                            .testTag("settings_button")
                            .size(44.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.92f))
                            .clickable(onClick = onNavigateToSettings),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ayarlar",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Title and Character Hero Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                Spacer(modifier = Modifier.height(if (isCompactHeight) 8.dp else 16.dp))

                // Playful Kid-Friendly 3D Title Logo (MATEMATİK ADASI)
                MatematikAdasiLogo()

                Spacer(modifier = Modifier.height(if (isCompactHeight) 14.dp else 28.dp))

                // Animated Explorer Character
                ExplorerView(
                    avatar = saveState.equipped.avatar,
                    color = saveState.equipped.color,
                    hat = saveState.equipped.hat,
                    pet = saveState.equipped.pet,
                    size = avatarSize,
                    animate = true
                )

                Spacer(modifier = Modifier.height(if (isCompactHeight) 8.dp else 16.dp))
            }

            // Bottom Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(if (isCompactHeight) 10.dp else 14.dp)
            ) {
                Big3DButton(
                    text = "OYNA",
                    emoji = "🚀",
                    testTag = "play_button",
                    height = if (isCompactHeight) 56.dp else 64.dp,
                    gradientColors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                    shadowColor = Color(0xFF0369A1),
                    onClick = onNavigateToWorlds
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Big3DButton(
                        text = "Mağaza",
                        emoji = "🛍️",
                        testTag = "shop_button",
                        fontSize = 13,
                        height = if (isCompactHeight) 46.dp else 52.dp,
                        gradientColors = listOf(Color(0xFFFBBF24), Color(0xFFD97706)),
                        shadowColor = Color(0xFFB45309),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToShop
                    )

                    Big3DButton(
                        text = "Rozetler",
                        emoji = "🏆",
                        testTag = "achievements_button",
                        fontSize = 13,
                        height = if (isCompactHeight) 46.dp else 52.dp,
                        gradientColors = listOf(Color(0xFF34D399), Color(0xFF059669)),
                        shadowColor = Color(0xFF047857),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToAchievements
                    )

                    Big3DButton(
                        text = "Veli",
                        emoji = "👨‍👩‍👧",
                        testTag = "parent_button",
                        fontSize = 13,
                        height = if (isCompactHeight) 46.dp else 52.dp,
                        gradientColors = listOf(Color(0xFFA78BFA), Color(0xFF7C3AED)),
                        shadowColor = Color(0xFF6D28D9),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToParent
                    )
                }
            }
        }
    }
}

