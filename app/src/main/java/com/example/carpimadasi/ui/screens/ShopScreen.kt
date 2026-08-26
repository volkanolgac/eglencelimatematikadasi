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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carpimadasi.model.COLOR_MAP
import com.example.carpimadasi.model.COSMETICS
import com.example.carpimadasi.model.CosmeticItem
import com.example.carpimadasi.model.CosmeticType
import com.example.carpimadasi.model.SaveState
import com.example.carpimadasi.model.THEME_MAP
import com.example.carpimadasi.ui.components.Big3DButton
import com.example.carpimadasi.ui.components.DiamondBadge
import com.example.carpimadasi.ui.components.ExplorerView

@Composable
fun ShopScreen(
    saveState: SaveState,
    onBack: () -> Unit,
    onBuy: (CosmeticItem) -> Unit,
    onEquip: (CosmeticItem) -> Unit,
    onUnequip: (CosmeticItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(CosmeticType.AVATAR) }

    val categories = listOf(
        CosmeticType.AVATAR to "Karakter",
        CosmeticType.HAT to "Şapka",
        CosmeticType.PET to "Hayvan",
        CosmeticType.COLOR to "Renk",
        CosmeticType.THEME to "Tema"
    )

    val itemsForCategory = COSMETICS.filter { it.type == selectedCategory }

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
                    .testTag("shop_back_button")
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
                        Brush.horizontalGradient(
                            listOf(Color(0xFFEDE9FE), Color(0xFFF1F5F9))
                        )
                    )
                    .padding(vertical = 7.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("🛍️", fontSize = 16.sp, modifier = Modifier.padding(end = 5.dp))
                    Text(
                        text = "ADA MAĞAZASI",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6D28D9),
                        maxLines = 1
                    )
                }
            }

            DiamondBadge(count = saveState.diamonds)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Character Live Preview Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.4f))
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            ExplorerView(
                avatar = saveState.equipped.avatar,
                color = saveState.equipped.color,
                hat = saveState.equipped.hat,
                pet = saveState.equipped.pet,
                size = 100.dp,
                animate = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Tabs
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories.size) { idx ->
                val (cat, label) = categories[idx]
                val isSelected = selectedCategory == cat

                val bgCol = if (isSelected) Color(0xFF0284C7) else Color.White.copy(alpha = 0.9f)
                val textCol = if (isSelected) Color.White else Color(0xFF475569)

                Box(
                    modifier = Modifier
                        .testTag("shop_tab_${cat.name.lowercase()}")
                        .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgCol)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textCol
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Item Cards Grid (2 columns)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(itemsForCategory.size) { idx ->
                val item = itemsForCategory[idx]
                val isOwned = saveState.ownedCosmetics.contains(item.id) || item.price == 0
                val isEquipped = when (item.type) {
                    CosmeticType.AVATAR -> saveState.equipped.avatar == item.id
                    CosmeticType.HAT -> saveState.equipped.hat == item.id
                    CosmeticType.PET -> saveState.equipped.pet == item.id
                    CosmeticType.COLOR -> saveState.equipped.color == item.id
                    CosmeticType.THEME -> saveState.equipped.theme == item.id
                }

                Box(
                    modifier = Modifier
                        .testTag("shop_item_${item.id}")
                        .shadow(3.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.94f))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Visual item preview
                        when (item.type) {
                            CosmeticType.AVATAR -> {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ExplorerView(
                                        avatar = item.id,
                                        color = saveState.equipped.color,
                                        size = 62.dp,
                                        animate = false
                                    )
                                }
                            }
                            CosmeticType.COLOR -> {
                                val col = COLOR_MAP[item.id] ?: COLOR_MAP["color_amber"]!!
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .shadow(2.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(col.body),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.4f))
                                    )
                                }
                            }
                            CosmeticType.HAT -> {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.emoji,
                                        fontSize = 34.sp
                                    )
                                }
                            }
                            CosmeticType.PET -> {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.emoji,
                                        fontSize = 34.sp
                                    )
                                }
                            }
                            CosmeticType.THEME -> {
                                val themeConfig = THEME_MAP[item.id]
                                Box(
                                    modifier = Modifier
                                        .size(width = 66.dp, height = 46.dp)
                                        .shadow(2.dp, RoundedCornerShape(10.dp))
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(themeConfig?.brush ?: Brush.verticalGradient(listOf(Color(0xFF7DD3FC), Color(0xFF38BDF8)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.emoji,
                                        fontSize = 22.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        if (isEquipped) {
                            Big3DButton(
                                text = if (item.type == CosmeticType.HAT || item.type == CosmeticType.PET) "Çıkar" else "Kullanımda",
                                testTag = "unequip_button_${item.id}",
                                fontSize = 12,
                                height = 40.dp,
                                gradientColors = if (item.type == CosmeticType.HAT || item.type == CosmeticType.PET) {
                                    listOf(Color(0xFF94A3B8), Color(0xFF64748B))
                                } else {
                                    listOf(Color(0xFF34D399), Color(0xFF059669))
                                },
                                shadowColor = if (item.type == CosmeticType.HAT || item.type == CosmeticType.PET) Color(0xFF475569) else Color(0xFF047857),
                                onClick = {
                                    if (item.type == CosmeticType.HAT || item.type == CosmeticType.PET) {
                                        onUnequip(item)
                                    }
                                }
                            )
                        } else if (isOwned) {
                            Big3DButton(
                                text = "Kullan",
                                testTag = "equip_button_${item.id}",
                                fontSize = 13,
                                height = 40.dp,
                                gradientColors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                                shadowColor = Color(0xFF0369A1),
                                onClick = { onEquip(item) }
                            )
                        } else {
                            val canAfford = saveState.diamonds >= item.price
                            Big3DButton(
                                text = "💎 ${item.price}",
                                testTag = "buy_button_${item.id}",
                                fontSize = 13,
                                height = 40.dp,
                                enabled = canAfford,
                                gradientColors = if (canAfford) listOf(Color(0xFFFBBF24), Color(0xFFD97706)) else listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8)),
                                shadowColor = if (canAfford) Color(0xFFB45309) else Color(0xFF64748B),
                                onClick = { onBuy(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}
