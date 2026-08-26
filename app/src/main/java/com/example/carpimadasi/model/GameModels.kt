package com.example.carpimadasi.model

import kotlinx.serialization.Serializable

enum class Screen {
    HOME,
    WORLDS,
    MAP,
    GAME,
    CHEST,
    SHOP,
    SETTINGS,
    PARENT,
    ACHIEVEMENTS
}

enum class OperationType(
    val id: String,
    val title: String,
    val shortTitle: String,
    val symbol: String,
    val emoji: String,
    val subtitle: String
) {
    ADDITION("addition", "Toplama Adası", "Toplama", "➕", "🏝️", "+1'den +9'a Toplama"),
    SUBTRACTION("subtraction", "Çıkarma Adası", "Çıkarma", "➖", "🌋", "-1'den -9'a Çıkarma"),
    MULTIPLICATION("multiplication", "Çarpım Adası", "Çarpma", "✖️", "🌴", "1'den 9'a Çarpım Tablosu"),
    DIVISION("division", "Bölme Adası", "Bölme", "➗", "🏰", "1'den 9'a Kalansız Bölme")
}

enum class QuestionFormat {
    STANDARD,       // a [op] b = ?
    REVERSED,       // b [op] a = ?
    MISSING_FIRST,  // ? [op] b = c
    MISSING_SECOND, // a [op] ? = c
    VISUAL          // word problem
}

data class Question(
    val a: Int,
    val b: Int,
    val answer: Int,
    val format: QuestionFormat,
    val options: List<Int>,
    val prompt: String,
    val subPrompt: String? = null,
    val operation: OperationType = OperationType.MULTIPLICATION
)

@Serializable
data class LevelProgress(
    var stars: Int = 0,
    var bestStreak: Int = 0,
    var completed: Boolean = false
)

@Serializable
data class IslandProgress(
    val levels: MutableList<LevelProgress> = MutableList(10) { LevelProgress() }
)

@Serializable
data class OperationProgress(
    val unlockedIslands: MutableList<Int> = mutableListOf(1),
    val islands: MutableMap<Int, IslandProgress> = mutableMapOf()
)

@Serializable
data class WrongRecord(
    val key: String,
    val a: Int,
    val b: Int,
    var count: Int = 1
)

@Serializable
data class SessionStat(
    val date: String,
    var minutesPlayed: Int = 0,
    var questionsAnswered: Int = 0,
    var correctCount: Int = 0,
    var wrongCount: Int = 0,
    val wrongRecords: MutableList<WrongRecord> = mutableListOf()
)

data class DailyHistory(
    val date: String,
    val minutes: Int,
    val correct: Int,
    val questions: Int
)

data class ParentStats(
    val todayMinutes: Int,
    val todayQuestions: Int,
    val todayCorrect: Int,
    val totalQuestions: Int,
    val hardest: List<WrongRecord>,
    val completedIslands: List<Int>,
    val history: List<DailyHistory>,
    val streak: Int
)

enum class CosmeticType {
    AVATAR,
    HAT,
    PET,
    COLOR,
    THEME
}

data class CosmeticItem(
    val id: String,
    val name: String,
    val type: CosmeticType,
    val price: Int,
    val emoji: String
)

@Serializable
data class EquippedCosmetics(
    var avatar: String = "avatar_classic",
    var hat: String? = null,
    var pet: String? = null,
    var color: String = "color_amber",
    var theme: String? = "theme_sky"
)

@Serializable
data class SaveState(
    var diamonds: Int = 50,
    var unlockedIslands: MutableList<Int> = mutableListOf(1),
    val islands: MutableMap<Int, IslandProgress> = mutableMapOf(),
    val operations: MutableMap<String, OperationProgress> = mutableMapOf(),
    val ownedCosmetics: MutableList<String> = mutableListOf("avatar_classic", "color_amber", "theme_sky"),
    var equipped: EquippedCosmetics = EquippedCosmetics(),
    var soundEnabled: Boolean = true,
    var musicEnabled: Boolean = true,
    val sessions: MutableList<SessionStat> = mutableListOf(),
    var streak: Int = 0,
    var lastPlayDate: String? = null,
    var parentPin: String = "1234",
    val badges: MutableList<String> = mutableListOf()
) {
    fun getOperationProgress(op: OperationType): OperationProgress {
        val key = op.id
        if (!operations.containsKey(key)) {
            if (op == OperationType.MULTIPLICATION && islands.isNotEmpty()) {
                operations[key] = OperationProgress(
                    unlockedIslands = unlockedIslands.toMutableList(),
                    islands = islands.toMutableMap()
                )
            } else {
                operations[key] = OperationProgress(
                    unlockedIslands = mutableListOf(1),
                    islands = mutableMapOf()
                )
            }
        }
        return operations[key]!!
    }

    fun deepCopy(): SaveState {
        val newIslands = mutableMapOf<Int, IslandProgress>()
        islands.forEach { (k, v) ->
            newIslands[k] = IslandProgress(v.levels.map { it.copy() }.toMutableList())
        }
        val newOperations = mutableMapOf<String, OperationProgress>()
        operations.forEach { (opKey, opProg) ->
            val opIslands = mutableMapOf<Int, IslandProgress>()
            opProg.islands.forEach { (k, v) ->
                opIslands[k] = IslandProgress(v.levels.map { it.copy() }.toMutableList())
            }
            newOperations[opKey] = OperationProgress(
                unlockedIslands = opProg.unlockedIslands.toMutableList(),
                islands = opIslands
            )
        }

        val newSessions = sessions.map { s ->
            SessionStat(
                date = s.date,
                minutesPlayed = s.minutesPlayed,
                questionsAnswered = s.questionsAnswered,
                correctCount = s.correctCount,
                wrongCount = s.wrongCount,
                wrongRecords = s.wrongRecords.map { it.copy() }.toMutableList()
            )
        }.toMutableList()

        return SaveState(
            diamonds = this.diamonds,
            unlockedIslands = this.unlockedIslands.toMutableList(),
            islands = newIslands,
            operations = newOperations,
            ownedCosmetics = this.ownedCosmetics.toMutableList(),
            equipped = this.equipped.copy(),
            soundEnabled = this.soundEnabled,
            musicEnabled = this.musicEnabled,
            sessions = newSessions,
            streak = this.streak,
            lastPlayDate = this.lastPlayDate,
            parentPin = this.parentPin,
            badges = this.badges.toMutableList()
        )
    }
}

data class GameResult(
    val stars: Int,
    val diamonds: Int,
    val bestStreak: Int
)

data class GameSession(
    val operation: OperationType = OperationType.MULTIPLICATION,
    val table: Int,
    val levelIndex: Int
)

