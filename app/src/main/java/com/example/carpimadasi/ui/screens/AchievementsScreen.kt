package com.example.carpimadasi.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carpimadasi.model.ALL_BADGES
import com.example.carpimadasi.model.BadgeCategory
import com.example.carpimadasi.model.BadgeDefinition
import com.example.carpimadasi.model.SaveState
import com.example.carpimadasi.model.isBadgeEarned

@Composable
fun AchievementsScreen(
    saveState: SaveState,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(BadgeCategory.ALL) }

    val allBadges = ALL_BADGES
    val earnedCount = remember(saveState) {
        allBadges.count { isBadgeEarned(it, saveState) }
    }
    val totalCount = allBadges.size
    val progressFraction = if (totalCount > 0) earnedCount.toFloat() / totalCount else 0f

    val filteredBadges = remember(selectedCategory, saveState) {
        when (selectedCategory) {
            BadgeCategory.ALL -> allBadges
            BadgeCategory.OPERATIONS -> allBadges.filter { it.category == BadgeCategory.OPERATIONS }
            BadgeCategory.SPECIAL -> allBadges.filter { it.category == BadgeCategory.SPECIAL }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompactHeight = maxHeight < 650.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = if (isCompactHeight) 12.dp else 18.dp)
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .testTag("achievements_back_button")
                        .size(44.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.92f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color(0xFF334155)
                    )
                }

                // Title Banner
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                        .shadow(3.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                            )
                        )
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🏆", fontSize = 18.sp, modifier = Modifier.padding(end = 6.dp))
                        Text(
                            text = "KAZANIMLARIM",
                            fontSize = if (isCompactHeight) 15.sp else 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF92400E)
                        )
                    }
                }

                // Earned count pill
                Box(
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⭐ $earnedCount/$totalCount",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0284C7)
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompactHeight) 10.dp else 14.dp))

            // Progress Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.95f), Color(0xFFF8FAFC).copy(alpha = 0.95f))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = if (isCompactHeight) 10.dp else 14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Rozet İlerlemesi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                        }
                        Text(
                            text = "%${(progressFraction * 100).toInt()} Tamamlandı",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFD97706)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFFF59E0B),
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (earnedCount == totalCount) {
                            "🎉 Tebrikler! Tüm rozetleri kazandın, sen gerçek bir matematik efsanesisin!"
                        } else {
                            "Adalardaki bölümleri tamamlayarak ve yeni rekorlar kırarak rozetleri topla! 🚀"
                        },
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompactHeight) 10.dp else 12.dp))

            // Category Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BadgeCategory.entries.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    val bgColors = if (isSelected) {
                        listOf(Color(0xFFFBBF24), Color(0xFFD97706))
                    } else {
                        listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.9f))
                    }
                    val textColor = if (isSelected) Color.White else Color(0xFF475569)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(bgColors))
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${cat.emoji} ${cat.title}",
                            fontSize = if (isCompactHeight) 11.sp else 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredBadges, key = { it.id }) { badge ->
                    val earned = isBadgeEarned(badge, saveState)
                    BadgeCard(badge = badge, earned = earned)
                }
            }
        }
    }
}

@Composable
private fun BadgeCard(
    badge: BadgeDefinition,
    earned: Boolean
) {
    val cardBg = if (earned) {
        Brush.verticalGradient(
            listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.7f), Color(0xFFF1F5F9).copy(alpha = 0.7f))
        )
    }

    val borderColor = if (earned) Color(0xFFFDE68A) else Color(0xFFE2E8F0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (earned) 3.dp else 1.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(12.dp)
            .testTag("badge_card_${badge.id}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(if (earned) 3.dp else 0.dp, CircleShape)
                    .clip(CircleShape)
                    .background(if (earned) Color.White else Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (earned) badge.emoji else "🔒",
                    fontSize = if (earned) 28.sp else 22.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badge Name
            Text(
                text = badge.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (earned) Color(0xFF78350F) else Color(0xFF64748B),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Badge Description
            Text(
                text = badge.description,
                fontSize = 11.sp,
                color = if (earned) Color(0xFF92400E).copy(alpha = 0.85f) else Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (earned) Color(0xFF10B981) else Color(0xFF94A3B8).copy(alpha = 0.2f)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    if (earned) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Kazanıldı",
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Kazanıldı",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Kilitli",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Kilitli",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}
