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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carpimadasi.model.ISLAND_THEMES
import com.example.carpimadasi.model.OperationType
import com.example.carpimadasi.model.SaveState
import com.example.carpimadasi.ui.components.DiamondBadge
import com.example.carpimadasi.ui.components.StarsDisplay
import com.example.carpimadasi.ui.components.StreakBadge

@Composable
fun MapScreen(
    operation: OperationType = OperationType.MULTIPLICATION,
    saveState: SaveState,
    onBackToWorlds: () -> Unit,
    onStartGame: (operation: OperationType, table: Int, levelIndex: Int) -> Unit
) {
    var selectedIsland by remember { mutableStateOf<Int?>(null) }

    if (selectedIsland == null) {
        // 3x3 Islands Grid View for Selected Operation
        IslandGridView(
            operation = operation,
            saveState = saveState,
            onSelectIsland = { selectedIsland = it },
            onBack = onBackToWorlds
        )
    } else {
        // 10 Levels Grid View for Selected Island
        val table = selectedIsland!!
        LevelGridView(
            operation = operation,
            table = table,
            saveState = saveState,
            onBack = { selectedIsland = null },
            onSelectLevel = { levelIndex ->
                onStartGame(operation, table, levelIndex)
            }
        )
    }
}

@Composable
private fun IslandGridView(
    operation: OperationType,
    saveState: SaveState,
    onSelectIsland: (Int) -> Unit,
    onBack: () -> Unit
) {
    val opProg = saveState.getOperationProgress(operation)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Top navigation bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .testTag("map_back_button")
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

            val (opBg, opTextCol) = when (operation) {
                OperationType.ADDITION -> listOf(Color(0xFFDCFCE7), Color(0xFFBBF7D0)) to Color(0xFF15803D)
                OperationType.SUBTRACTION -> listOf(Color(0xFFFFEDD5), Color(0xFFFED7AA)) to Color(0xFFC2410C)
                OperationType.MULTIPLICATION -> listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD)) to Color(0xFF0369A1)
                OperationType.DIVISION -> listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE)) to Color(0xFF6D28D9)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
                    .shadow(3.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(androidx.compose.ui.graphics.Brush.horizontalGradient(opBg))
                    .padding(vertical = 7.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${operation.emoji} ${operation.title.uppercase()}",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = opTextCol,
                    maxLines = 1
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DiamondBadge(count = saveState.diamonds)
                StreakBadge(streak = saveState.streak)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .shadow(2.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color(0xFFFEF3C7), Color(0xFFFFFBEB))
                    )
                )
                .padding(vertical = 7.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✨ Bir Ada Seç ve Keşfe Başla! 🚀",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF92400E),
                textAlign = TextAlign.Center
            )
        }

        // 3x3 Grid of 9 Islands
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(9) { index ->
                val table = index + 1
                val isUnlocked = opProg.unlockedIslands.contains(table)
                val islandInfo = ISLAND_THEMES[table] ?: ISLAND_THEMES[1]!!
                val progress = opProg.islands[table]
                val completedLevels = progress?.levels?.count { it.completed } ?: 0

                val bgBrush = if (isUnlocked) {
                    Brush.verticalGradient(islandInfo.gradientColors)
                } else {
                    Brush.verticalGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))
                }

                Box(
                    modifier = Modifier
                        .testTag("island_card_$table")
                        .alpha(if (isUnlocked) 1f else 0.42f)
                        .shadow(if (isUnlocked) 6.dp else 1.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(bgBrush)
                        .clickable(enabled = isUnlocked) {
                            onSelectIsland(table)
                        }
                        .padding(vertical = 14.dp, horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isUnlocked) islandInfo.emoji else "🔒",
                            fontSize = 32.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = islandInfo.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isUnlocked) Color.White else Color(0xFF475569),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isUnlocked) "$completedLevels/10" else "Kilitli",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color.White.copy(alpha = 0.9f) else Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelGridView(
    operation: OperationType,
    table: Int,
    saveState: SaveState,
    onBack: () -> Unit,
    onSelectLevel: (Int) -> Unit
) {
    val islandInfo = ISLAND_THEMES[table] ?: ISLAND_THEMES[1]!!
    val opProg = saveState.getOperationProgress(operation)
    val islandProgress = opProg.islands[table]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Top navigation bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .testTag("level_back_button")
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

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
                    .shadow(3.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color(0xFFEDE9FE), Color(0xFFF1F5F9))
                        )
                    )
                    .padding(vertical = 7.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${islandInfo.emoji} ${islandInfo.name}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4338CA),
                    maxLines = 1
                )
            }

            DiamondBadge(count = saveState.diamonds)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .shadow(2.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color(0xFFFEF3C7), Color(0xFFFFFBEB))
                    )
                )
                .padding(vertical = 7.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎯 Bir Bölüm Seç ve Maceraya Başla! ⭐",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF92400E),
                textAlign = TextAlign.Center
            )
        }

        // 10 Levels Grid (2 columns x 5 rows)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(10) { levelIndex ->
                val levelNum = levelIndex + 1
                val isUnlocked = levelIndex == 0 || (islandProgress?.levels?.getOrNull(levelIndex - 1)?.completed == true)
                val lvlData = islandProgress?.levels?.getOrNull(levelIndex)
                val stars = lvlData?.stars ?: 0
                val isCompleted = lvlData?.completed == true

                val cardBg = if (isUnlocked) {
                    Brush.verticalGradient(
                        if (isCompleted) listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                        else listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9))
                    )
                } else {
                    Brush.verticalGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))
                }

                val textColor = when {
                    !isUnlocked -> Color(0xFF64748B)
                    isCompleted -> Color.White
                    else -> Color(0xFF1E293B)
                }

                Box(
                    modifier = Modifier
                        .testTag("level_card_$levelNum")
                        .alpha(if (isUnlocked) 1f else 0.42f)
                        .shadow(if (isUnlocked) 4.dp else 1.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(cardBg)
                        .clickable(enabled = isUnlocked) {
                            onSelectLevel(levelIndex)
                        }
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isUnlocked) "Bölüm $levelNum" else "🔒 Bölüm $levelNum",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        if (isUnlocked) {
                            StarsDisplay(stars = stars, starSize = 18)
                        } else {
                            Text(
                                text = "Kilitli",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }
    }
}

