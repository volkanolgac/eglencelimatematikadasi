package com.example.carpimadasi.logic

import android.content.Context
import com.example.carpimadasi.model.CosmeticItem
import com.example.carpimadasi.model.CosmeticType
import com.example.carpimadasi.model.DailyHistory
import com.example.carpimadasi.model.GameResult
import com.example.carpimadasi.model.GameSession
import com.example.carpimadasi.model.IslandProgress
import com.example.carpimadasi.model.OperationProgress
import com.example.carpimadasi.model.OperationType
import com.example.carpimadasi.model.ParentStats
import com.example.carpimadasi.model.SaveState
import com.example.carpimadasi.model.SessionStat
import com.example.carpimadasi.model.WrongRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveRepository(context: Context) {

    private val prefs = context.getSharedPreferences("carpim_adasi_save_v1", Context.MODE_PRIVATE)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val _saveState = MutableStateFlow(load())
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private fun todayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun yesterdayString(): String {
        val yesterday = Date(System.currentTimeMillis() - 86400000L)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterday)
    }

    private fun defaultSave(): SaveState {
        val base = SaveState()
        for (op in OperationType.entries) {
            val opProg = OperationProgress()
            for (i in 1..9) {
                opProg.islands[i] = IslandProgress()
            }
            opProg.unlockedIslands.clear()
            opProg.unlockedIslands.add(1)
            base.operations[op.id] = opProg
        }
        for (i in 1..9) {
            base.islands[i] = IslandProgress()
        }
        base.unlockedIslands.clear()
        base.unlockedIslands.add(1)
        syncIslandUnlocks(base)
        return base
    }

    private fun syncIslandUnlocks(state: SaveState) {
        for (op in OperationType.entries) {
            val opProg = state.getOperationProgress(op)
            if (!opProg.unlockedIslands.contains(1)) {
                opProg.unlockedIslands.add(1)
            }
            val distinct = opProg.unlockedIslands.distinct().sorted()
            opProg.unlockedIslands.clear()
            opProg.unlockedIslands.addAll(distinct)
        }

        // Also sync legacy fields
        val mulProg = state.getOperationProgress(OperationType.MULTIPLICATION)
        state.unlockedIslands.clear()
        state.unlockedIslands.addAll(mulProg.unlockedIslands)
    }

    private fun load(): SaveState {
        val raw = prefs.getString("save_json", null) ?: return defaultSave()
        return try {
            val parsed = json.decodeFromString<SaveState>(raw)
            for (op in OperationType.entries) {
                val opProg = parsed.getOperationProgress(op)
                for (i in 1..9) {
                    if (!opProg.islands.containsKey(i)) {
                        opProg.islands[i] = IslandProgress()
                    }
                }
            }
            for (i in 1..9) {
                if (!parsed.islands.containsKey(i)) {
                    parsed.islands[i] = IslandProgress()
                }
            }
            if (!parsed.ownedCosmetics.contains("avatar_classic")) parsed.ownedCosmetics.add("avatar_classic")
            if (!parsed.ownedCosmetics.contains("color_amber")) parsed.ownedCosmetics.add("color_amber")
            if (!parsed.ownedCosmetics.contains("theme_sky")) parsed.ownedCosmetics.add("theme_sky")
            syncIslandUnlocks(parsed)
            parsed
        } catch (_: Exception) {
            defaultSave()
        }
    }

    private fun persist(state: SaveState) {
        try {
            val raw = json.encodeToString(state)
            prefs.edit().putString("save_json", raw).apply()
            _saveState.value = state.deepCopy()
        } catch (_: Exception) {
        }
    }

    fun updateStreak() {
        val state = _saveState.value
        val today = todayString()
        if (state.lastPlayDate == today) return

        val yesterday = yesterdayString()
        if (state.lastPlayDate == yesterday) {
            state.streak += 1
        } else {
            state.streak = 1
        }
        state.lastPlayDate = today
        if (state.streak >= 7 && !state.badges.contains("streak7")) {
            state.badges.add("streak7")
        }
        persist(state)
    }

    fun getTodaySession(): SessionStat {
        val state = _saveState.value
        val t = todayString()
        var s = state.sessions.find { it.date == t }
        if (s == null) {
            s = SessionStat(date = t)
            state.sessions.add(s)
            if (state.sessions.size > 60) {
                val sub = state.sessions.takeLast(60).toMutableList()
                state.sessions.clear()
                state.sessions.addAll(sub)
            }
            persist(state)
        }
        return s
    }

    fun handleGameComplete(
        session: GameSession,
        result: GameResult,
        onNewBadge: (String) -> Unit
    ) {
        val state = _saveState.value
        val opProg = state.getOperationProgress(session.operation)
        val island = opProg.islands[session.table]
        if (island != null && session.levelIndex in 0 until island.levels.size) {
            val lvl = island.levels[session.levelIndex]
            lvl.completed = true
            lvl.stars = maxOf(lvl.stars, result.stars)
            lvl.bestStreak = maxOf(lvl.bestStreak, result.bestStreak)
        }

        // Also sync legacy multiplication islands map if multiplication
        if (session.operation == OperationType.MULTIPLICATION) {
            val legacyIsland = state.islands[session.table]
            if (legacyIsland != null && session.levelIndex in 0 until legacyIsland.levels.size) {
                val lvl = legacyIsland.levels[session.levelIndex]
                lvl.completed = true
                lvl.stars = maxOf(lvl.stars, result.stars)
                lvl.bestStreak = maxOf(lvl.bestStreak, result.bestStreak)
            }
        }

        state.diamonds += result.diamonds

        val nextTable = session.table + 1
        if (nextTable <= 9 && !opProg.unlockedIslands.contains(nextTable)) {
            opProg.unlockedIslands.add(nextTable)
        }
        syncIslandUnlocks(state)

        // Whole island finished badge check
        val wholeIslandFinished = island?.levels?.all { it.completed } == true
        if (wholeIslandFinished) {
            val badgeKey = "${session.operation.id}_island${session.table}"
            if (!state.badges.contains(badgeKey)) {
                state.badges.add(badgeKey)
                onNewBadge(badgeKey)
            }
        }

        // Combo badge
        if (result.bestStreak >= 10 && !state.badges.contains("combo10")) {
            state.badges.add("combo10")
            onNewBadge("combo10")
        }

        // First chest badge
        if (!state.badges.contains("first_chest")) {
            state.badges.add("first_chest")
            onNewBadge("first_chest")
        }

        // Diamonds badges
        if (state.diamonds >= 100 && !state.badges.contains("diamonds100")) {
            state.badges.add("diamonds100")
            onNewBadge("diamonds100")
        }
        if (state.diamonds >= 500 && !state.badges.contains("diamonds500")) {
            state.badges.add("diamonds500")
            onNewBadge("diamonds500")
        }

        // All round explorer badge
        val hasEach = OperationType.entries.all { op ->
            val prog = state.getOperationProgress(op)
            prog.islands.values.any { isl -> isl.levels.isNotEmpty() && isl.levels.all { it.completed } }
        }
        if (hasEach && !state.badges.contains("all_round_explorer")) {
            state.badges.add("all_round_explorer")
            onNewBadge("all_round_explorer")
        }

        // Add 2 minutes to today session
        val todaySess = getTodaySession()
        todaySess.minutesPlayed += 2

        persist(state)
    }

    fun recordQuestionResult(
        op: OperationType = OperationType.MULTIPLICATION,
        a: Int,
        b: Int,
        correct: Boolean
    ) {
        val session = getTodaySession()
        session.questionsAnswered += 1
        if (correct) {
            session.correctCount += 1
        } else {
            session.wrongCount += 1
            val key = QuestionGenerator.wrongKey(op, a, b)
            val existing = session.wrongRecords.find { it.key == key }
            if (existing != null) {
                existing.count += 1
            } else {
                session.wrongRecords.add(WrongRecord(key = key, a = a, b = b, count = 1))
            }
        }
        persist(_saveState.value)
    }

    fun buyCosmetic(item: CosmeticItem) {
        val state = _saveState.value.deepCopy()
        if (state.diamonds >= item.price && !state.ownedCosmetics.contains(item.id)) {
            state.diamonds -= item.price
            state.ownedCosmetics.add(item.id)
            if (!state.badges.contains("wardrobe1")) {
                state.badges.add("wardrobe1")
            }
            if (state.ownedCosmetics.size >= 5 && !state.badges.contains("wardrobe5")) {
                state.badges.add("wardrobe5")
            }
            persist(state)
        }
    }

    fun equipCosmetic(item: CosmeticItem) {
        val state = _saveState.value.deepCopy()
        when (item.type) {
            CosmeticType.AVATAR -> state.equipped.avatar = item.id
            CosmeticType.HAT -> state.equipped.hat = item.id
            CosmeticType.PET -> state.equipped.pet = item.id
            CosmeticType.COLOR -> state.equipped.color = item.id
            CosmeticType.THEME -> state.equipped.theme = item.id
        }
        persist(state)
    }

    fun unequipCosmetic(item: CosmeticItem) {
        val state = _saveState.value.deepCopy()
        when (item.type) {
            CosmeticType.HAT -> state.equipped.hat = null
            CosmeticType.PET -> state.equipped.pet = null
            CosmeticType.AVATAR -> state.equipped.avatar = "avatar_classic"
            CosmeticType.COLOR -> state.equipped.color = "color_amber"
            CosmeticType.THEME -> state.equipped.theme = null
        }
        persist(state)
    }

    fun toggleSound(): Boolean {
        val state = _saveState.value.deepCopy()
        state.soundEnabled = !state.soundEnabled
        persist(state)
        return state.soundEnabled
    }

    fun toggleMusic(): Boolean {
        val state = _saveState.value.deepCopy()
        state.musicEnabled = !state.musicEnabled
        persist(state)
        return state.musicEnabled
    }

    fun changeParentPin(pin: String): Boolean {
        if (pin.length == 4 && pin.all { it.isDigit() }) {
            val state = _saveState.value.deepCopy()
            state.parentPin = pin
            persist(state)
            return true
        }
        return false
    }

    fun toggleIslandUnlock(op: OperationType, tableNum: Int) {
        if (tableNum == 1) return
        val state = _saveState.value.deepCopy()
        val opProg = state.getOperationProgress(op)
        if (opProg.unlockedIslands.contains(tableNum)) {
            opProg.unlockedIslands.remove(tableNum)
        } else {
            opProg.unlockedIslands.add(tableNum)
        }
        syncIslandUnlocks(state)
        persist(state)
    }

    fun unlockAllIslands(op: OperationType? = null) {
        val state = _saveState.value.deepCopy()
        if (op != null) {
            val opProg = state.getOperationProgress(op)
            opProg.unlockedIslands.clear()
            opProg.unlockedIslands.addAll(1..9)
        } else {
            for (o in OperationType.entries) {
                val opProg = state.getOperationProgress(o)
                opProg.unlockedIslands.clear()
                opProg.unlockedIslands.addAll(1..9)
            }
            state.unlockedIslands.clear()
            state.unlockedIslands.addAll(1..9)
        }
        syncIslandUnlocks(state)
        persist(state)
    }

    fun lockAllIslands(op: OperationType? = null) {
        val state = _saveState.value.deepCopy()
        if (op != null) {
            val opProg = state.getOperationProgress(op)
            opProg.unlockedIslands.clear()
            opProg.unlockedIslands.add(1)
        } else {
            for (o in OperationType.entries) {
                val opProg = state.getOperationProgress(o)
                opProg.unlockedIslands.clear()
                opProg.unlockedIslands.add(1)
            }
            state.unlockedIslands.clear()
            state.unlockedIslands.add(1)
        }
        syncIslandUnlocks(state)
        persist(state)
    }

    fun resetToInitialLocks() {
        lockAllIslands(null)
    }

    fun resetProgress() {
        val state = _saveState.value
        val pin = state.parentPin
        val sound = state.soundEnabled
        val music = state.musicEnabled

        val fresh = defaultSave()
        fresh.parentPin = pin
        fresh.soundEnabled = sound
        fresh.musicEnabled = music
        persist(fresh)
    }

    fun computeParentStats(): ParentStats {
        val state = _saveState.value
        val today = getTodaySession()
        val completedIslands = mutableListOf<Int>()
        for (op in OperationType.entries) {
            val opProg = state.getOperationProgress(op)
            for (t in 1..9) {
                val island = opProg.islands[t]
                if (island?.levels?.all { it.completed } == true) {
                    completedIslands.add(t)
                }
            }
        }

        val wrongMap = mutableMapOf<String, WrongRecord>()
        for (s in state.sessions) {
            for (w in s.wrongRecords) {
                val ex = wrongMap[w.key]
                if (ex != null) {
                    ex.count += w.count
                } else {
                    wrongMap[w.key] = WrongRecord(w.key, w.a, w.b, w.count)
                }
            }
        }

        val hardest = wrongMap.values.sortedByDescending { it.count }.take(5)
        val totalQ = state.sessions.sumOf { it.questionsAnswered }
        val history = state.sessions.takeLast(14).map {
            DailyHistory(
                date = it.date,
                minutes = it.minutesPlayed,
                correct = it.correctCount,
                questions = it.questionsAnswered
            )
        }

        return ParentStats(
            todayMinutes = today.minutesPlayed,
            todayQuestions = today.questionsAnswered,
            todayCorrect = today.correctCount,
            totalQuestions = totalQ,
            hardest = hardest,
            completedIslands = completedIslands.distinct(),
            history = history,
            streak = state.streak
        )
    }
}

