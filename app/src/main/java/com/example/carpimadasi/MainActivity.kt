package com.example.carpimadasi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.carpimadasi.model.GameResult
import com.example.carpimadasi.model.Screen
import com.example.carpimadasi.ui.MainViewModel
import com.example.carpimadasi.ui.components.AtmosphereBackground
import com.example.carpimadasi.ui.screens.AchievementsScreen
import com.example.carpimadasi.ui.screens.ChestScreen
import com.example.carpimadasi.ui.screens.GameScreen
import com.example.carpimadasi.ui.screens.HomeScreen
import com.example.carpimadasi.ui.screens.MapScreen
import com.example.carpimadasi.ui.screens.ParentScreen
import com.example.carpimadasi.ui.screens.SettingsScreen
import com.example.carpimadasi.ui.screens.ShopScreen
import com.example.carpimadasi.ui.screens.SplashScreen
import com.example.carpimadasi.ui.screens.WorldsScreen
import com.example.carpimadasi.ui.theme.CarpimAdasiTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarpimAdasiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    var showSplash by remember { mutableStateOf(true) }

                    AnimatedContent(
                        targetState = showSplash,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "splash_transition"
                    ) { isSplash ->
                        if (isSplash) {
                            SplashScreen(onSplashFinished = { showSplash = false })
                        } else {
                            CarpimAdasiApp(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CarpimAdasiApp(viewModel: MainViewModel) {
    val saveState by viewModel.saveState.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedOperation by viewModel.selectedOperation.collectAsState()
    val activeSession by viewModel.activeSession.collectAsState()
    val lastResult by viewModel.lastGameResult.collectAsState()
    val newBadge by viewModel.newUnlockedBadge.collectAsState()

    // Handle System Back Button
    BackHandler(enabled = currentScreen != Screen.HOME) {
        when (currentScreen) {
            Screen.GAME -> viewModel.navigateTo(Screen.MAP)
            Screen.CHEST -> viewModel.navigateTo(Screen.WORLDS)
            Screen.MAP -> viewModel.navigateTo(Screen.WORLDS)
            Screen.WORLDS -> viewModel.navigateTo(Screen.HOME)
            Screen.SHOP -> viewModel.navigateTo(Screen.HOME)
            Screen.ACHIEVEMENTS -> viewModel.navigateTo(Screen.HOME)
            Screen.PARENT -> viewModel.navigateTo(Screen.HOME)
            Screen.SETTINGS -> viewModel.navigateTo(Screen.HOME)
            Screen.HOME -> { /* Do nothing / default exit */ }
        }
    }

    AtmosphereBackground(themeId = saveState.equipped.theme) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    Screen.HOME -> {
                        HomeScreen(
                            saveState = saveState,
                            onNavigateToWorlds = { viewModel.navigateTo(Screen.WORLDS) },
                            onNavigateToShop = { viewModel.navigateTo(Screen.SHOP) },
                            onNavigateToAchievements = { viewModel.navigateTo(Screen.ACHIEVEMENTS) },
                            onNavigateToParent = { viewModel.navigateTo(Screen.PARENT) },
                            onNavigateToSettings = { viewModel.navigateTo(Screen.SETTINGS) }
                        )
                    }

                    Screen.WORLDS -> {
                        WorldsScreen(
                            saveState = saveState,
                            onBack = { viewModel.navigateTo(Screen.HOME) },
                            onSelectOperation = { op ->
                                viewModel.selectOperation(op)
                            }
                        )
                    }

                    Screen.MAP -> {
                        MapScreen(
                            operation = selectedOperation,
                            saveState = saveState,
                            onBackToWorlds = { viewModel.navigateTo(Screen.WORLDS) },
                            onStartGame = { op, table, levelIndex ->
                                viewModel.startGame(op, table, levelIndex)
                            }
                        )
                    }

                    Screen.GAME -> {
                        if (activeSession != null) {
                            val session = activeSession!!
                            GameScreen(
                                session = session,
                                saveState = saveState,
                                soundManager = viewModel.soundManager,
                                onGameFinished = { result ->
                                    viewModel.finishGame(result)
                                },
                                onExitToMap = {
                                    viewModel.navigateTo(Screen.MAP)
                                },
                                onRecordQuestion = { a, b, correct ->
                                    viewModel.recordQuestion(session.operation, a, b, correct)
                                }
                            )
                        } else {
                            viewModel.navigateTo(Screen.MAP)
                        }
                    }

                    Screen.CHEST -> {
                        ChestScreen(
                            result = lastResult ?: GameResult(3, 15, 5),
                            newBadgeKey = newBadge,
                            soundManager = viewModel.soundManager,
                            onContinue = { viewModel.continueFromChest() }
                        )
                    }

                    Screen.SHOP -> {
                        ShopScreen(
                            saveState = saveState,
                            onBack = { viewModel.navigateTo(Screen.HOME) },
                            onBuy = { item -> viewModel.buyCosmetic(item) },
                            onEquip = { item -> viewModel.equipCosmetic(item) },
                            onUnequip = { item -> viewModel.unequipCosmetic(item) }
                        )
                    }

                    Screen.ACHIEVEMENTS -> {
                        AchievementsScreen(
                            saveState = saveState,
                            onBack = { viewModel.navigateTo(Screen.HOME) }
                        )
                    }

                    Screen.PARENT -> {
                        ParentScreen(
                            saveState = saveState,
                            parentStats = viewModel.getParentStats(),
                            onBack = { viewModel.navigateTo(Screen.HOME) },
                            onUnlockAll = { op -> viewModel.unlockAllIslands(op) },
                            onLockAll = { op -> viewModel.lockAllIslands(op) },
                            onToggleIsland = { op, table -> viewModel.toggleIslandUnlock(op, table) },
                            onChangePin = { newPin -> viewModel.changeParentPin(newPin) },
                            onResetProgress = { viewModel.resetProgress() }
                        )
                    }

                    Screen.SETTINGS -> {
                        SettingsScreen(
                            saveState = saveState,
                            onBack = { viewModel.navigateTo(Screen.HOME) },
                            onToggleSound = { viewModel.toggleSound() },
                            onToggleMusic = { viewModel.toggleMusic() },
                            onResetProgress = { viewModel.resetProgress() }
                        )
                    }
                }
            }
        }
    }
}

