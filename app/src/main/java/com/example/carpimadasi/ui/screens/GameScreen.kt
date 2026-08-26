package com.example.carpimadasi.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.carpimadasi.logic.QuestionGenerator
import com.example.carpimadasi.logic.SoundManager
import com.example.carpimadasi.model.GameResult
import com.example.carpimadasi.model.GameSession
import com.example.carpimadasi.model.Question
import com.example.carpimadasi.model.SaveState
import com.example.carpimadasi.ui.components.Big3DButton
import com.example.carpimadasi.ui.components.ExplorerView
import com.example.carpimadasi.ui.components.MonsterView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val QUESTION_TIME_LIMIT = 12f
private const val TOTAL_QUESTIONS = 10

@Composable
fun GameScreen(
    session: GameSession,
    saveState: SaveState,
    soundManager: SoundManager,
    onGameFinished: (GameResult) -> Unit,
    onExitToMap: () -> Unit,
    onRecordQuestion: (a: Int, b: Int, correct: Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var questionIndex by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var streak by remember { mutableIntStateOf(0) }
    var maxStreak by remember { mutableIntStateOf(0) }
    var diamondsEarned by remember { mutableIntStateOf(0) }

    var currentQuestion by remember {
        val wrongs = saveState.sessions.flatMap { it.wrongRecords }
        mutableStateOf(QuestionGenerator.makeQuestion(operation = session.operation, table = session.table, level = session.levelIndex + 1, wrongs = wrongs, questionIndex = 0))
    }

    var timeLeft by remember { mutableStateOf(QUESTION_TIME_LIMIT) }
    var isTimerActive by remember { mutableStateOf(true) }

    // Feedback visual states
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var optionState by remember { mutableStateOf<String?>(null) } // "correct", "wrong"
    var explorerEyes by remember { mutableStateOf("normal") }
    var explorerKick by remember { mutableStateOf(false) }
    var monsterState by remember { mutableStateOf("idle") }
    var toastText by remember { mutableStateOf<String?>(null) }
    var comboBannerText by remember { mutableStateOf<String?>(null) }

    // Defeat & Game Over Modal
    var isDefeatScene by remember { mutableStateOf(false) }
    var showGameOverModal by remember { mutableStateOf(false) }

    // Auto-hide combo banner after 0.5s (500ms)
    LaunchedEffect(comboBannerText) {
        if (comboBannerText != null) {
            delay(500)
            comboBannerText = null
        }
    }

    val progressFraction = (timeLeft / QUESTION_TIME_LIMIT).coerceIn(0f, 1f)
    val normalMonsterOffsetX = -80f * (1f - progressFraction)
    val monsterTargetX = if (isDefeatScene) -195f else normalMonsterOffsetX
    val animatedMonsterOffsetX by animateFloatAsState(
        targetValue = monsterTargetX,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = if (isDefeatScene) 500 else 250,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "monster_offset_x"
    )

    // Timer loop (keyed only on questionIndex, isDefeatScene, showGameOverModal)
    LaunchedEffect(questionIndex, isDefeatScene, showGameOverModal) {
        if (isDefeatScene || showGameOverModal) return@LaunchedEffect
        timeLeft = QUESTION_TIME_LIMIT
        isTimerActive = true
        while (timeLeft > 0 && isTimerActive && !isDefeatScene && !showGameOverModal) {
            delay(100)
            timeLeft -= 0.1f
        }
        if (timeLeft <= 0 && isTimerActive && !isDefeatScene && !showGameOverModal) {
            // Time out treated as unanswered/wrong
            isTimerActive = false
            handleAnswer(
                chosenOption = null,
                question = currentQuestion,
                soundManager = soundManager,
                onRecordQuestion = { c ->
                    onRecordQuestion(currentQuestion.a, currentQuestion.b, c)
                },
                onStateChange = { sOpt, optSt, expE, expK, monS, tTxt, cTxt ->
                    selectedOption = sOpt
                    optionState = optSt
                    explorerEyes = expE
                    explorerKick = expK
                    monsterState = monS
                    toastText = tTxt
                    comboBannerText = cTxt
                },
                onLivesChange = { newLives ->
                    lives = newLives
                    if (newLives <= 0) {
                        isDefeatScene = true
                    }
                },
                onStreakChange = { newStreak ->
                    streak = newStreak
                },
                onDiamondsChange = { d ->
                    diamondsEarned += d
                },
                onNextQuestion = {
                    if (lives > 0) {
                        advanceNextQuestion(
                            session = session,
                            saveState = saveState,
                            currentQIdx = questionIndex,
                            onUpdate = { nextIdx, nextQ ->
                                questionIndex = nextIdx
                                currentQuestion = nextQ
                                selectedOption = null
                                optionState = null
                                explorerEyes = "normal"
                                explorerKick = false
                                monsterState = "idle"
                                toastText = null
                                comboBannerText = null
                                isTimerActive = true
                            },
                            onFinish = {
                                val stars = if (lives >= 3) 3 else if (lives == 2) 2 else 1
                                onGameFinished(GameResult(stars = stars, diamonds = diamondsEarned, bestStreak = maxStreak))
                            }
                        )
                    }
                },
                currentLives = lives,
                currentStreak = streak
            )
        }
    }

    // Defeat Scene to Game Over Modal Transition: Monster approaches and bites
    LaunchedEffect(isDefeatScene) {
        if (isDefeatScene) {
            monsterState = "attack"
            delay(480)
            monsterState = "eat"
            explorerEyes = "x"
            soundManager.gameOver()
            delay(2500)
            showGameOverModal = true
        }
    }

    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        val screenHeight = maxHeight
        val isCompact = screenHeight < 650.dp
        val arenaHeight = if (isCompact) 125.dp else 150.dp
        val explorerSize = if (isCompact) 90.dp else 105.dp
        val monsterSize = if (isCompact) 85.dp else 100.dp
        val buttonHeight = if (isCompact) 50.dp else 56.dp
        val promptPadding = if (isCompact) 10.dp else 14.dp

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar (Lives, Question count, Combo & Exit)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hearts Lives
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 1..3) {
                        Text(
                            text = if (i <= lives) "❤️" else "🖤",
                            fontSize = if (isCompact) 20.sp else 22.sp
                        )
                    }
                }

                // Question Counter
                Box(
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Soru ${questionIndex + 1} / $TOTAL_QUESTIONS",
                        fontSize = if (isCompact) 13.5.sp else 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                }

                // Exit Button
                Box(
                    modifier = Modifier
                        .testTag("game_close_button")
                        .size(if (isCompact) 36.dp else 40.dp)
                        .shadow(2.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable(onClick = onExitToMap),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Çıkış",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Question Timer Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        progressFraction > 0.5f -> Color(0xFF10B981)
                        progressFraction > 0.25f -> Color(0xFFF59E0B)
                        else -> Color(0xFFEF4444)
                    },
                    trackColor = Color.White.copy(alpha = 0.4f)
                )
            }

            // Battle Arena (Explorer on left, Monster approaching on right)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(arenaHeight)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.25f))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Combo Banner (shows temporarily for 0.5s on combo)
                if (comboBannerText != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 4.dp)
                            .shadow(6.dp, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comboBannerText ?: "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Explorer
                    ExplorerView(
                        avatar = saveState.equipped.avatar,
                        color = saveState.equipped.color,
                        hat = saveState.equipped.hat,
                        pet = saveState.equipped.pet,
                        size = explorerSize,
                        animate = !isDefeatScene,
                        kick = explorerKick,
                        eyes = explorerEyes
                    )

                    // Monster
                    MonsterView(
                        variant = session.table,
                        size = monsterSize,
                        state = monsterState,
                        modifier = Modifier.offset(x = animatedMonsterOffsetX.dp)
                    )
                }
            }

            // Question Prompt Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.94f))
                    .padding(vertical = promptPadding, horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentQuestion.prompt,
                        fontSize = if (currentQuestion.subPrompt != null) (if (isCompact) 16.sp else 18.sp) else (if (isCompact) 28.sp else 32.sp),
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    if (currentQuestion.subPrompt != null) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = currentQuestion.subPrompt!!,
                            fontSize = if (isCompact) 21.sp else 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0284C7),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 4 Multiple-Choice Option Buttons (2x2 Grid)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 10.dp)
            ) {
                for (rowIndex in 0..1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 10.dp)
                    ) {
                        for (colIndex in 0..1) {
                            val optIndex = rowIndex * 2 + colIndex
                            val optionValue = currentQuestion.options.getOrNull(optIndex) ?: 0

                            val isSelected = selectedOption == optionValue
                            val isCorrectAns = optionValue == currentQuestion.answer

                            val (gradColors, shadowCol) = when {
                                optionState != null && isCorrectAns -> listOf(Color(0xFF34D399), Color(0xFF059669)) to Color(0xFF047857)
                                optionState == "wrong" && isSelected -> listOf(Color(0xFFF87171), Color(0xFFDC2626)) to Color(0xFFB91C1C)
                                else -> listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9)) to Color(0xFFCBD5E1)
                            }
                            val textCol = if (optionState != null && (isCorrectAns || isSelected)) Color.White else Color(0xFF0F172A)

                            Big3DButton(
                                text = "$optionValue",
                                testTag = "option_button_$optIndex",
                                modifier = Modifier.weight(1f),
                                height = buttonHeight,
                                fontSize = if (isCompact) 22 else 24,
                                gradientColors = gradColors,
                                shadowColor = shadowCol,
                                textColor = textCol,
                                enabled = isTimerActive && !isDefeatScene && !showGameOverModal,
                                onClick = {
                                    isTimerActive = false
                                    val isCorrect = optionValue == currentQuestion.answer
                                    onRecordQuestion(currentQuestion.a, currentQuestion.b, isCorrect)

                                    coroutineScope.launch {
                                        handleAnswer(
                                            chosenOption = optionValue,
                                            question = currentQuestion,
                                            soundManager = soundManager,
                                            onRecordQuestion = { /* already recorded above */ },
                                            onStateChange = { sOpt, optSt, expE, expK, monS, tTxt, cTxt ->
                                                selectedOption = sOpt
                                                optionState = optSt
                                                explorerEyes = expE
                                                explorerKick = expK
                                                monsterState = monS
                                                toastText = tTxt
                                                comboBannerText = cTxt
                                            },
                                            onLivesChange = { newLives ->
                                                lives = newLives
                                                if (newLives <= 0) {
                                                    isDefeatScene = true
                                                }
                                            },
                                            onStreakChange = { newStreak ->
                                                streak = newStreak
                                                maxStreak = maxOf(maxStreak, newStreak)
                                            },
                                            onDiamondsChange = { d ->
                                                diamondsEarned += d
                                            },
                                            onNextQuestion = {
                                                if (lives > 0) {
                                                    advanceNextQuestion(
                                                        session = session,
                                                        saveState = saveState,
                                                        currentQIdx = questionIndex,
                                                        onUpdate = { nextIdx, nextQ ->
                                                            questionIndex = nextIdx
                                                            currentQuestion = nextQ
                                                            selectedOption = null
                                                            optionState = null
                                                            explorerEyes = "normal"
                                                            explorerKick = false
                                                            monsterState = "idle"
                                                            toastText = null
                                                            comboBannerText = null
                                                            isTimerActive = true
                                                        },
                                                        onFinish = {
                                                            val stars = if (lives >= 3) 3 else if (lives == 2) 2 else 1
                                                            onGameFinished(GameResult(stars = stars, diamonds = diamondsEarned, bestStreak = maxStreak))
                                                        }
                                                    )
                                                }
                                            },
                                            currentLives = lives,
                                            currentStreak = streak
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Defeat / Game Over Modal
        if (showGameOverModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .shadow(12.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "💔",
                            fontSize = 48.sp
                        )

                        Text(
                            text = "OYUN BİTTİ",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFEF4444)
                        )

                        Text(
                            text = "Canların tükendi ama pes etmek yok! Tekrar deneyerek ustalaşabilirsin.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center
                        )

                        Big3DButton(
                            text = "Tekrar Dene",
                            emoji = "🔄",
                            testTag = "retry_button",
                            gradientColors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                            shadowColor = Color(0xFF0369A1),
                            onClick = {
                                // Reset game state
                                questionIndex = 0
                                lives = 3
                                streak = 0
                                maxStreak = 0
                                diamondsEarned = 0
                                isDefeatScene = false
                                showGameOverModal = false
                                selectedOption = null
                                optionState = null
                                explorerEyes = "normal"
                                explorerKick = false
                                monsterState = "idle"
                                toastText = null
                                comboBannerText = null
                                val wrongs = saveState.sessions.flatMap { it.wrongRecords }
                                currentQuestion = QuestionGenerator.makeQuestion(operation = session.operation, table = session.table, level = session.levelIndex + 1, wrongs = wrongs, questionIndex = 0)
                                isTimerActive = true
                            }
                        )

                        Big3DButton(
                            text = "Adalara Dön",
                            emoji = "🏝️",
                            testTag = "defeat_map_button",
                            gradientColors = listOf(Color(0xFF94A3B8), Color(0xFF64748B)),
                            shadowColor = Color(0xFF475569),
                            fontSize = 16,
                            height = 50.dp,
                            onClick = onExitToMap
                        )
                    }
                }
            }
        }
    }
}

private fun advanceNextQuestion(
    session: GameSession,
    saveState: SaveState,
    currentQIdx: Int,
    onUpdate: (Int, Question) -> Unit,
    onFinish: () -> Unit
) {
    if (currentQIdx + 1 >= TOTAL_QUESTIONS) {
        onFinish()
    } else {
        val nextIdx = currentQIdx + 1
        val wrongs = saveState.sessions.flatMap { it.wrongRecords }
        val nextQ = QuestionGenerator.makeQuestion(operation = session.operation, table = session.table, level = session.levelIndex + 1, wrongs = wrongs, questionIndex = nextIdx)
        onUpdate(nextIdx, nextQ)
    }
}

private suspend fun handleAnswer(
    chosenOption: Int?,
    question: Question,
    soundManager: SoundManager,
    onRecordQuestion: (Boolean) -> Unit,
    onStateChange: (Int?, String, String, Boolean, String, String?, String?) -> Unit,
    onLivesChange: (Int) -> Unit,
    onStreakChange: (Int) -> Unit,
    onDiamondsChange: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    currentLives: Int,
    currentStreak: Int
) {
    val isCorrect = chosenOption == question.answer
    onRecordQuestion(isCorrect)

    if (isCorrect) {
        val newStreak = currentStreak + 1
        onStreakChange(newStreak)

        val multiplier = when {
            newStreak >= 5 -> 5
            newStreak >= 3 -> 3
            newStreak >= 2 -> 2
            else -> 1
        }
        val gainedDiamonds = 5 * multiplier
        onDiamondsChange(gainedDiamonds)

        if (newStreak >= 2) {
            soundManager.combo(multiplier)
        } else {
            soundManager.correct()
        }
        soundManager.diamond()

        val comboMsg = if (newStreak >= 2) "🔥 x$multiplier COMBO!" else null

        onStateChange(chosenOption, "correct", "happy", true, "flee", null, comboMsg)
        delay(900)
        onNextQuestion()
    } else {
        val newLives = currentLives - 1
        onLivesChange(newLives)
        onStreakChange(0)

        soundManager.wrong()
        onStateChange(chosenOption, "wrong", "x", false, "attack", null, null)

        if (newLives > 0) {
            delay(1100)
            onNextQuestion()
        }
    }
}
