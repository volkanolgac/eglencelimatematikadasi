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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.carpimadasi.model.ISLAND_THEMES
import com.example.carpimadasi.model.OperationType
import com.example.carpimadasi.model.ParentStats
import com.example.carpimadasi.model.SaveState
import com.example.carpimadasi.model.isBadgeEarned
import com.example.carpimadasi.ui.components.Big3DButton

@Composable
fun ParentScreen(
    saveState: SaveState,
    parentStats: ParentStats,
    onBack: () -> Unit,
    onUnlockAll: (OperationType) -> Unit,
    onLockAll: (OperationType) -> Unit,
    onToggleIsland: (OperationType, Int) -> Unit,
    onChangePin: (String) -> Boolean,
    onResetProgress: () -> Unit
) {
    var isAuthenticated by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    if (!isAuthenticated) {
        // PIN Entry Screen
        PinEntryView(
            enteredPin = enteredPin,
            pinError = pinError,
            onDigitClick = { digit ->
                if (enteredPin.length < 4) {
                    val next = enteredPin + digit
                    enteredPin = next
                    pinError = false
                    if (next.length == 4) {
                        if (next == saveState.parentPin) {
                            isAuthenticated = true
                            enteredPin = ""
                        } else {
                            pinError = true
                        }
                    }
                }
            },
            onDeleteClick = {
                if (enteredPin.isNotEmpty()) {
                    enteredPin = enteredPin.dropLast(1)
                    pinError = false
                }
            },
            onBack = onBack
        )
    } else {
        // Authenticated Parent Portal
        ParentPortalDashboard(
            saveState = saveState,
            stats = parentStats,
            onBack = onBack,
            onUnlockAll = onUnlockAll,
            onLockAll = onLockAll,
            onToggleIsland = onToggleIsland,
            onChangePin = onChangePin,
            onResetProgress = {
                onResetProgress()
                onBack()
            }
        )
    }
}

@Composable
private fun PinEntryView(
    enteredPin: String,
    pinError: Boolean,
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onBack: () -> Unit
) {
    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val isCompact = maxHeight < 650.dp
        val keyHeight = if (isCompact) 48.dp else 58.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = if (isCompact) 14.dp else 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Box(
                    modifier = Modifier
                        .testTag("pin_back_button")
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
            }

            // Title and Dots
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 48.dp else 60.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE9FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Veli Kilidi",
                        tint = Color(0xFF7C3AED),
                        modifier = Modifier.size(if (isCompact) 26.dp else 30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 14.dp))

                Box(
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(Color(0xFFEDE9FE), Color(0xFFF1F5F9))
                            )
                        )
                        .padding(vertical = 6.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔐 Veli Paneli Girişi",
                        fontSize = if (isCompact) 17.sp else 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6D28D9)
                    )
                }

                Text(
                    text = "Lütfen 4 haneli veli PIN kodunuzu girin",
                    fontSize = if (isCompact) 12.sp else 14.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 20.dp))

                // 4 PIN Dots
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(if (isCompact) 16.dp else 20.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        pinError -> Color(0xFFEF4444)
                                        isFilled -> Color(0xFF7C3AED)
                                        else -> Color(0xFFCBD5E1)
                                    }
                                )
                        )
                    }
                }

                if (pinError) {
                    Text(
                        text = "Hatalı PIN! Tekrar deneyin. (Varsayılan: 1234)",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            // Numeric Keypad
            Column(
                modifier = Modifier.fillMaxWidth(if (isCompact) 0.9f else 0.85f),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 10.dp)
            ) {
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "⌫")
                )

                for (row in rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 10.dp)
                    ) {
                        for (digit in row) {
                            if (digit.isEmpty()) {
                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(keyHeight)
                                        .shadow(3.dp, RoundedCornerShape(16.dp))
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .clickable {
                                            if (digit == "⌫") onDeleteClick()
                                            else onDigitClick(digit)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = digit,
                                        fontSize = if (isCompact) 21.sp else 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentPortalDashboard(
    saveState: SaveState,
    stats: ParentStats,
    onBack: () -> Unit,
    onUnlockAll: (OperationType) -> Unit,
    onLockAll: (OperationType) -> Unit,
    onToggleIsland: (OperationType, Int) -> Unit,
    onChangePin: (String) -> Boolean,
    onResetProgress: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showPinChangeDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var pinChangeMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .testTag("parent_back_button")
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
                    .padding(horizontal = 8.dp)
                    .shadow(3.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color(0xFFEDE9FE), Color(0xFFF1F5F9))
                        )
                    )
                    .padding(vertical = 7.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("👨‍👩‍👧", fontSize = 17.sp, modifier = Modifier.padding(end = 6.dp))
                    Text(
                        text = "VELİ PANELİ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6D28D9)
                    )
                }
            }

            Box(modifier = Modifier.size(44.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4 Tabs (Raporlar, Rozetler, Adalar, Ayarlar)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabTitles = listOf("📊 Raporlar", "🏆 Rozetler", "🏝️ Adalar", "⚙️ Ayarlar")
            for (idx in tabTitles.indices) {
                val isSelected = selectedTab == idx
                val bgCol = if (isSelected) Color(0xFF7C3AED) else Color.White.copy(alpha = 0.9f)
                val textCol = if (isSelected) Color.White else Color(0xFF475569)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgCol)
                        .clickable { selectedTab = idx }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabTitles[idx],
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textCol,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Content
        when (selectedTab) {
            0 -> ReportsTab(stats = stats, saveState = saveState, onNavigateToBadgesTab = { selectedTab = 1 })
            1 -> ParentBadgesTab(saveState = saveState)
            2 -> IslandsLockTab(
                saveState = saveState,
                onUnlockAll = onUnlockAll,
                onLockAll = onLockAll,
                onToggleIsland = onToggleIsland
            )
            3 -> SettingsTab(
                onOpenPinDialog = {
                    newPinInput = ""
                    pinChangeMsg = null
                    showPinChangeDialog = true
                },
                onOpenResetDialog = { showResetDialog = true }
            )
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("İlerlemeyi Sıfırla?", fontWeight = FontWeight.Bold) },
            text = { Text("Tüm ada seviyeleri, elmaslar ve kazanılan eşyalar sıfırlanacaktır. Bu işlem geri alınamaz.") },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    onResetProgress()
                }) {
                    Text("Evet, Sıfırla", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    // PIN Change Dialog
    if (showPinChangeDialog) {
        AlertDialog(
            onDismissRequest = { showPinChangeDialog = false },
            title = { Text("Veli PIN Kodunu Değiştir", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Yeni 4 haneli PIN kodunuzu girin:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPinInput = it },
                        singleLine = true,
                        placeholder = { Text("Örn: 5678") }
                    )
                    if (pinChangeMsg != null) {
                        Text(
                            text = pinChangeMsg!!,
                            color = if (pinChangeMsg!!.startsWith("Başarılı")) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPinInput.length == 4) {
                        if (onChangePin(newPinInput)) {
                            pinChangeMsg = "Başarılı! PIN güncellendi."
                            showPinChangeDialog = false
                        } else {
                            pinChangeMsg = "Lütfen 4 haneli rakam girin."
                        }
                    } else {
                        pinChangeMsg = "PIN 4 haneli olmalıdır."
                    }
                }) {
                    Text("Kaydet", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinChangeDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun ReportsTab(
    stats: ParentStats,
    saveState: SaveState,
    onNavigateToBadgesTab: () -> Unit
) {
    val allBadges = ALL_BADGES
    val earnedCount = remember(saveState) {
        allBadges.count { isBadgeEarned(it, saveState) }
    }
    val totalCount = allBadges.size
    val progressFraction = if (totalCount > 0) earnedCount.toFloat() / totalCount else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Today Summary Cards Grid (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Bugünkü Aktivite",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatMetricCard("⏱️ Süre", "${stats.todayMinutes} dk", Modifier.weight(1f))
                    StatMetricCard("📝 Çözülen", "${stats.todayQuestions} soru", Modifier.weight(1f))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatMetricCard("✅ Doğru", "${stats.todayCorrect}", Modifier.weight(1f))
                    StatMetricCard("🔥 Seri", "${stats.streak} gün", Modifier.weight(1f))
                }
            }
        }

        // Achievements & Badges Overview Card for Parents
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable(onClick = onNavigateToBadgesTab)
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏆", fontSize = 18.sp, modifier = Modifier.padding(end = 6.dp))
                            Text(
                                text = "Kazanılan Rozetler & Başarılar",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Text(
                            text = "$earnedCount / $totalCount",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF7C3AED)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF8B5CF6),
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "%${(progressFraction * 100).toInt()} Tamamlandı",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6D28D9)
                        )
                        Text(
                            text = "Tümünü İncele ➔",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7C3AED)
                        )
                    }
                }
            }
        }

        // Hardest multiplication pairs
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "🧠 En Çok Tekrar Gereken Sorular",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (stats.hardest.isEmpty()) {
                        Text(
                            text = "Henüz yanlış cevaplanan soru kaydedilmedi. Harika gidiyor!",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    } else {
                        stats.hardest.forEach { wrong ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${wrong.a} ve ${wrong.b} işlemi",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "${wrong.count} yanlış",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Completed Islands
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "🏆 Tamamlanan Adalar (10/10)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (stats.completedIslands.isEmpty()) {
                        Text(
                            text = "Henüz tamamen bitirilen ada yok. Keşfe devam!",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            stats.completedIslands.forEach { t ->
                                val islandInfo = ISLAND_THEMES[t]
                                Text(
                                    text = "${islandInfo?.emoji} ${islandInfo?.shortName}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669),
                                    modifier = Modifier
                                        .background(Color(0xFFD1FAE5), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Activity History
        item {
            Text(
                text = "Son Günlerin Geçmişi",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )
        }

        items(stats.history.reversed()) { day ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = day.date, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    Text(text = "${day.minutes} dk • ${day.correct}/${day.questions} doğru", fontSize = 13.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
private fun ParentBadgesTab(saveState: SaveState) {
    var selectedCat by remember { mutableStateOf(BadgeCategory.ALL) }
    val allBadges = ALL_BADGES
    val earnedCount = remember(saveState) {
        allBadges.count { isBadgeEarned(it, saveState) }
    }
    val totalCount = allBadges.size
    val progressFraction = if (totalCount > 0) earnedCount.toFloat() / totalCount else 0f

    val filtered = remember(selectedCat, saveState) {
        when (selectedCat) {
            BadgeCategory.ALL -> allBadges
            BadgeCategory.OPERATIONS -> allBadges.filter { it.category == BadgeCategory.OPERATIONS }
            BadgeCategory.SPECIAL -> allBadges.filter { it.category == BadgeCategory.SPECIAL }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Parent Badge Metric Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Kazanılan Rozetler",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "$earnedCount / $totalCount Rozet Açıldı",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7C3AED)
                        )
                    }
                    Text(
                        text = "%${(progressFraction * 100).toInt()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF6D28D9)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF8B5CF6),
                    trackColor = Color(0xFFE2E8F0)
                )
            }
        }

        // Filter Category Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BadgeCategory.entries.forEach { cat ->
                val isSelected = selectedCat == cat
                val bgCol = if (isSelected) Color(0xFF7C3AED) else Color.White.copy(alpha = 0.9f)
                val textCol = if (isSelected) Color.White else Color(0xFF475569)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgCol)
                        .clickable { selectedCat = cat }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cat.emoji} ${cat.title}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textCol,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Badges List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered, key = { it.id }) { badge ->
                val earned = isBadgeEarned(badge, saveState)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (earned) 2.dp else 1.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (earned) Color.White else Color(0xFFF8FAFC))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (earned) Color(0xFFFEF3C7) else Color(0xFFE2E8F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (earned) badge.emoji else "🔒",
                                    fontSize = if (earned) 22.sp else 16.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.name,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (earned) Color(0xFF1E293B) else Color(0xFF64748B)
                                )
                                Text(
                                    text = badge.description,
                                    fontSize = 11.5.sp,
                                    color = if (earned) Color(0xFF475569) else Color(0xFF94A3B8),
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (earned) Color(0xFFD1FAE5) else Color(0xFFE2E8F0)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (earned) "Kazanıldı ✅" else "Kilitli 🔒",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (earned) Color(0xFF059669) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Column {
            Text(text = title, fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
        }
    }
}

@Composable
private fun IslandsLockTab(
    saveState: SaveState,
    onUnlockAll: (OperationType) -> Unit,
    onLockAll: (OperationType) -> Unit,
    onToggleIsland: (OperationType, Int) -> Unit
) {
    var selectedOp by remember { mutableStateOf(OperationType.MULTIPLICATION) }
    val opProg = saveState.getOperationProgress(selectedOp)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Operation Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OperationType.entries.forEach { op ->
                val isSelected = selectedOp == op
                val bgCol = if (isSelected) Color(0xFF7C3AED) else Color.White.copy(alpha = 0.9f)
                val textCol = if (isSelected) Color.White else Color(0xFF475569)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgCol)
                        .clickable { selectedOp = op }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${op.emoji} ${op.symbol}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textCol
                    )
                }
            }
        }

        // Action Buttons: Tüm Kilitleri Aç & Tümünü Kilitle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Big3DButton(
                text = "Tüm Kilitleri Aç",
                emoji = "🔓",
                fontSize = 12,
                height = 44.dp,
                gradientColors = listOf(Color(0xFF34D399), Color(0xFF059669)),
                shadowColor = Color(0xFF047857),
                modifier = Modifier.weight(1f),
                onClick = { onUnlockAll(selectedOp) }
            )

            Big3DButton(
                text = "Tümünü Kilitle",
                emoji = "🔒",
                fontSize = 12,
                height = 44.dp,
                gradientColors = listOf(Color(0xFFF87171), Color(0xFFDC2626)),
                shadowColor = Color(0xFFB91C1C),
                modifier = Modifier.weight(1f),
                onClick = { onLockAll(selectedOp) }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .shadow(1.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF1F5F9))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🏝️ ${selectedOp.title} Adaları (${opProg.unlockedIslands.size}/9 Açık)",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF334155)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(8) { idx ->
                val table = idx + 2 // 2 to 9
                val islandInfo = ISLAND_THEMES[table] ?: ISLAND_THEMES[1]!!
                val isUnlocked = opProg.unlockedIslands.contains(table)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isUnlocked) Color.White else Color(0xFFF8FAFC))
                        .clickable { onToggleIsland(selectedOp, table) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(islandInfo.emoji, fontSize = 24.sp)
                            Column {
                                Text(
                                    text = "${selectedOp.symbol} ${islandInfo.name}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) Color(0xFF1E293B) else Color(0xFF64748B)
                                )
                                Text(
                                    text = if (isUnlocked) "Açık 🔓" else "Kilitli 🔒",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isUnlocked) Color(0xFF16A34A) else Color(0xFFDC2626)
                                )
                            }
                        }

                        Switch(
                            checked = isUnlocked,
                            onCheckedChange = { onToggleIsland(selectedOp, table) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF7C3AED),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFCBD5E1)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsTab(
    onOpenPinDialog: () -> Unit,
    onOpenResetDialog: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Veli Güvenlik Ayarları",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A)
                )

                Big3DButton(
                    text = "PIN Kodunu Değiştir",
                    emoji = "🔑",
                    fontSize = 15,
                    height = 50.dp,
                    gradientColors = listOf(Color(0xFFA78BFA), Color(0xFF7C3AED)),
                    shadowColor = Color(0xFF6D28D9),
                    onClick = onOpenPinDialog
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Tehlikeli Bölge",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFEF4444)
                )

                Text(
                    text = "Tüm ilerlemeyi ve elmasları sıfırlayarak baştan başlayın.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )

                Big3DButton(
                    text = "Tüm İlerlemeyi Sıfırla",
                    emoji = "⚠️",
                    fontSize = 15,
                    height = 50.dp,
                    gradientColors = listOf(Color(0xFFF87171), Color(0xFFDC2626)),
                    shadowColor = Color(0xFFB91C1C),
                    onClick = onOpenResetDialog
                )
            }
        }
    }
}

