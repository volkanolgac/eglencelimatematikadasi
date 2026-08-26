package com.example.carpimadasi.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val COSMETICS: List<CosmeticItem> = listOf(
    // Avatars
    CosmeticItem("avatar_classic", "Klasik Kaşif", CosmeticType.AVATAR, 0, "🧑‍🌾"),
    CosmeticItem("avatar_astro", "Uzay Kaptanı", CosmeticType.AVATAR, 100, "🧑‍🚀"),
    CosmeticItem("avatar_panda", "Sevimli Panda", CosmeticType.AVATAR, 120, "🐼"),
    CosmeticItem("avatar_superhero", "Süper Kahraman", CosmeticType.AVATAR, 150, "🦸"),
    CosmeticItem("avatar_knight", "Cesur Şövalye", CosmeticType.AVATAR, 180, "🛡️"),
    CosmeticItem("avatar_robot", "Robot Kaşif", CosmeticType.AVATAR, 200, "🤖"),

    // Hats
    CosmeticItem("hat_pirate", "Korsan Şapkası", CosmeticType.HAT, 50, "🏴‍☠️"),
    CosmeticItem("hat_crown", "Kral Tacı", CosmeticType.HAT, 80, "👑"),
    CosmeticItem("hat_cap", "Beyzbol Şapkası", CosmeticType.HAT, 30, "🧢"),
    CosmeticItem("hat_grad", "Bilge Kepi", CosmeticType.HAT, 70, "🎓"),
    CosmeticItem("hat_beanie", "Kış Beresi", CosmeticType.HAT, 40, "🧶"),

    // Pets
    CosmeticItem("pet_cat", "Kedi Yavrusu", CosmeticType.PET, 120, "🐱"),
    CosmeticItem("pet_dog", "Köpek Yavrusu", CosmeticType.PET, 120, "🐶"),
    CosmeticItem("pet_bird", "Mavi Kuş", CosmeticType.PET, 90, "🐦"),
    CosmeticItem("pet_turtle", "Kaplumbağa", CosmeticType.PET, 70, "🐢"),
    CosmeticItem("pet_fox", "Akıllı Tilki", CosmeticType.PET, 180, "🦊"),

    // Colors
    CosmeticItem("color_amber", "Turuncu", CosmeticType.COLOR, 0, "🟠"),
    CosmeticItem("color_sky", "Mavi", CosmeticType.COLOR, 40, "🔵"),
    CosmeticItem("color_mint", "Yeşil", CosmeticType.COLOR, 40, "🟢"),
    CosmeticItem("color_coral", "Pembe", CosmeticType.COLOR, 60, "🌸"),
    CosmeticItem("color_grape", "Mor", CosmeticType.COLOR, 80, "🟣"),
    CosmeticItem("color_gold", "Altın Sarısı", CosmeticType.COLOR, 90, "🟡"),

    // Themes
    CosmeticItem("theme_sky", "Gökyüzü Teması", CosmeticType.THEME, 0, "☁️"),
    CosmeticItem("theme_sunset", "Gün Batımı Teması", CosmeticType.THEME, 100, "🌅"),
    CosmeticItem("theme_space", "Uzay Teması", CosmeticType.THEME, 150, "🚀")
)

data class ExplorerColor(
    val body: Color,
    val cheek: Color
)

val COLOR_MAP: Map<String, ExplorerColor> = mapOf(
    "amber" to ExplorerColor(Color(0xFFFBBF24), Color(0xFFFB7185)),
    "color_amber" to ExplorerColor(Color(0xFFFBBF24), Color(0xFFFB7185)),
    "sky" to ExplorerColor(Color(0xFF38BDF8), Color(0xFFFB7185)),
    "color_sky" to ExplorerColor(Color(0xFF38BDF8), Color(0xFFFB7185)),
    "mint" to ExplorerColor(Color(0xFF34D399), Color(0xFFFB7185)),
    "color_mint" to ExplorerColor(Color(0xFF34D399), Color(0xFFFB7185)),
    "coral" to ExplorerColor(Color(0xFFFB7185), Color(0xFFFBBF24)),
    "color_coral" to ExplorerColor(Color(0xFFFB7185), Color(0xFFFBBF24)),
    "grape" to ExplorerColor(Color(0xFFC084FC), Color(0xFFFBBF24)),
    "color_grape" to ExplorerColor(Color(0xFFC084FC), Color(0xFFFBBF24)),
    "gold" to ExplorerColor(Color(0xFFFACC15), Color(0xFFF43F5E)),
    "color_gold" to ExplorerColor(Color(0xFFFACC15), Color(0xFFF43F5E))
)

val HAT_EMOJI: Map<String, String> = mapOf(
    "hat_pirate" to "🏴‍☠️",
    "hat_crown" to "👑",
    "hat_cap" to "🧢",
    "hat_grad" to "🎓",
    "hat_beanie" to "🧶"
)

val PET_EMOJI: Map<String, String> = mapOf(
    "pet_cat" to "🐱",
    "pet_dog" to "🐶",
    "pet_bird" to "🐦",
    "pet_turtle" to "🐢",
    "pet_fox" to "🦊"
)

data class ThemeConfig(
    val id: String,
    val name: String,
    val brush: Brush,
    val isSpace: Boolean = false,
    val isSunset: Boolean = false
)

val THEME_MAP: Map<String, ThemeConfig> = mapOf(
    "theme_sky" to ThemeConfig(
        id = "theme_sky",
        name = "Gökyüzü Teması",
        brush = Brush.verticalGradient(
            listOf(Color(0xFF7DD3FC), Color(0xFFBAE6FD), Color(0xFF86EFAC))
        )
    ),
    "theme_sunset" to ThemeConfig(
        id = "theme_sunset",
        name = "Gün Batımı Teması",
        brush = Brush.verticalGradient(
            listOf(Color(0xFFF97316), Color(0xFFFB7185), Color(0xFFA855F7), Color(0xFF6366F1))
        ),
        isSunset = true
    ),
    "theme_space" to ThemeConfig(
        id = "theme_space",
        name = "Uzay Teması",
        brush = Brush.verticalGradient(
            listOf(Color(0xFF090D16), Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF0F172A))
        ),
        isSpace = true
    )
)

data class IslandThemeInfo(
    val emoji: String,
    val name: String,
    val shortName: String,
    val gradientColors: List<Color>
)

val ISLAND_THEMES: Map<Int, IslandThemeInfo> = mapOf(
    1 to IslandThemeInfo("🌴", "1'ler Adası", "1'ler", listOf(Color(0xFF7DD3FC), Color(0xFF38BDF8))),
    2 to IslandThemeInfo("🏝️", "2'ler Adası", "2'ler", listOf(Color(0xFF6EE7B7), Color(0xFF34D399))),
    3 to IslandThemeInfo("🌋", "3'ler Adası", "3'ler", listOf(Color(0xFFFDBA74), Color(0xFFFB923C))),
    4 to IslandThemeInfo("🏖️", "4'ler Adası", "4'ler", listOf(Color(0xFFFCD34D), Color(0xFFFBBF24))),
    5 to IslandThemeInfo("🌳", "5'ler Adası", "5'ler", listOf(Color(0xFFBEF264), Color(0xFFA3E635))),
    6 to IslandThemeInfo("🌈", "6'ler Adası", "6'ler", listOf(Color(0xFFF472B6), Color(0xFFEC4899))),
    7 to IslandThemeInfo("⛰️", "7'ler Adası", "7'ler", listOf(Color(0xFF67E8F9), Color(0xFF22D3EE))),
    8 to IslandThemeInfo("👑", "8'ler Adası", "8'ler", listOf(Color(0xFFC4B5FD), Color(0xFFA78BFA))),
    9 to IslandThemeInfo("🏰", "9'lar Adası", "9'lar", listOf(Color(0xFFFDA4AF), Color(0xFFFB7185)))
)

data class BadgeInfo(
    val name: String,
    val emoji: String
)

enum class BadgeCategory(val title: String, val emoji: String) {
    ALL("Tümü", "🌟"),
    OPERATIONS("Ada Kupaları", "🏝️"),
    SPECIAL("Özel Başarılar", "⚡")
}

data class BadgeDefinition(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val category: BadgeCategory,
    val opType: OperationType? = null,
    val islandNum: Int? = null
)

val ALL_BADGES: List<BadgeDefinition> = buildList {
    // 1. Island Championship Badges for all 4 Operations (36 Badges)
    for (op in OperationType.entries) {
        for (i in 1..9) {
            val islandTheme = ISLAND_THEMES[i]
            val em = islandTheme?.emoji ?: "🏝️"
            add(
                BadgeDefinition(
                    id = "${op.id}_island$i",
                    name = "${op.shortTitle} $i'ler Şampiyonu",
                    emoji = em,
                    description = "${op.title} dünyasındaki $i'ler adasının tüm 10 seviyesini tamamla.",
                    category = BadgeCategory.OPERATIONS,
                    opType = op,
                    islandNum = i
                )
            )
        }
    }

    // 2. Special Achievements (9 Badges)
    add(
        BadgeDefinition(
            id = "first_chest",
            name = "İlk Sandık Kaşifi",
            emoji = "🎁",
            description = "İlk bölümünü 3 yıldızla bitir ve zafer hazine sandığını aç.",
            category = BadgeCategory.SPECIAL
        )
    )
    add(
        BadgeDefinition(
            id = "combo10",
            name = "10'lu Kombo Ustası",
            emoji = "⚡",
            description = "Tek bir oyunda peş peşe 10 soruyu hatasız doğru yanıtla.",
            category = BadgeCategory.SPECIAL
        )
    )
    add(
        BadgeDefinition(
            id = "streak7",
            name = "7 Günlük Seri Yıldızı",
            emoji = "🔥",
            description = "7 gün boyunca her gün oyuna girerek çalışma serini koru.",
            category = BadgeCategory.SPECIAL
        )
    )
    add(
        BadgeDefinition(
            id = "streak30",
            name = "30 Günlük Efsane",
            emoji = "🌟",
            description = "30 gün boyunca aralıksız matematik adalarını ziyaret et.",
            category = BadgeCategory.SPECIAL
        )
    )
    add(
        BadgeDefinition(
            id = "diamonds100",
            name = "Elmas Avcısı",
            emoji = "💎",
            description = "Toplam 100 elmas biriktir.",
            category = BadgeCategory.SPECIAL
        )
    )
    add(
        BadgeDefinition(
            id = "diamonds500",
            name = "Hazine Efendisi",
            emoji = "👑",
            description = "Toplam 500 elmas biriktir.",
            category = BadgeCategory.SPECIAL
        )
    )
    add(
        BadgeDefinition(
            id = "wardrobe1",
            name = "Moda İkonu",
            emoji = "👒",
            description = "Ada Mağazasından ilk karakter kostümünü, şapkanı veya evcil hayvanını al.",
            category = BadgeCategory.SPECIAL
        )
    )
    add(
        BadgeDefinition(
            id = "wardrobe5",
            name = "Büyük Koleksiyoncu",
            emoji = "✨",
            description = "Ada Mağazasından en az 5 farklı eşya satın al.",
            category = BadgeCategory.SPECIAL
        )
    )
    add(
        BadgeDefinition(
            id = "all_round_explorer",
            name = "Dört İşlem Kaşifi",
            emoji = "🧭",
            description = "Toplama, Çıkarma, Çarpma ve Bölme dünyalarının her birinde en az bir ada tamamla.",
            category = BadgeCategory.SPECIAL
        )
    )
}

val BADGE_INFO: Map<String, BadgeInfo> = ALL_BADGES.associate { it.id to BadgeInfo(it.name, it.emoji) }

fun getBadgeInfo(key: String): BadgeInfo {
    val def = ALL_BADGES.find { it.id == key || (key == "island${it.islandNum}" && it.opType == OperationType.MULTIPLICATION) }
    if (def != null) {
        return BadgeInfo(def.name, def.emoji)
    }
    val existing = BADGE_INFO[key]
    if (existing != null) return existing
    return BadgeInfo("Ada Şampiyonu", "🏆")
}

fun isBadgeEarned(badge: BadgeDefinition, saveState: SaveState): Boolean {
    if (saveState.badges.contains(badge.id)) return true
    if (badge.opType == OperationType.MULTIPLICATION && badge.islandNum != null) {
        if (saveState.badges.contains("island${badge.islandNum}")) return true
    }
    if (badge.opType != null && badge.islandNum != null) {
        val opProg = saveState.getOperationProgress(badge.opType)
        val island = opProg.islands[badge.islandNum]
        if (island != null && island.levels.isNotEmpty() && island.levels.all { it.completed }) {
            return true
        }
    }
    when (badge.id) {
        "first_chest" -> return saveState.badges.contains("first_chest")
        "combo10" -> return saveState.badges.contains("combo10")
        "streak7" -> return saveState.streak >= 7 || saveState.badges.contains("streak7")
        "streak30" -> return saveState.streak >= 30 || saveState.badges.contains("streak30")
        "diamonds100" -> return saveState.diamonds >= 100 || saveState.badges.contains("diamonds100")
        "diamonds500" -> return saveState.diamonds >= 500 || saveState.badges.contains("diamonds500")
        "wardrobe1" -> return saveState.ownedCosmetics.size > 3 || saveState.badges.contains("wardrobe1")
        "wardrobe5" -> return saveState.ownedCosmetics.size >= 5 || saveState.badges.contains("wardrobe5")
        "all_round_explorer" -> {
            val hasEach = OperationType.entries.all { op ->
                val prog = saveState.getOperationProgress(op)
                prog.islands.values.any { isl -> isl.levels.isNotEmpty() && isl.levels.all { it.completed } }
            }
            return hasEach
        }
    }
    return false
}
