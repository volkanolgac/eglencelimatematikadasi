package com.example.carpimadasi.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpimadasi.logic.SaveRepository
import com.example.carpimadasi.logic.SoundManager
import com.example.carpimadasi.model.CosmeticItem
import com.example.carpimadasi.model.GameResult
import com.example.carpimadasi.model.GameSession
import com.example.carpimadasi.model.OperationType
import com.example.carpimadasi.model.ParentStats
import com.example.carpimadasi.model.SaveState
import com.example.carpimadasi.model.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SaveRepository(application)
    val soundManager = SoundManager(application)

    val saveState: StateFlow<SaveState> = repository.saveState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.saveState.value
    )

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedOperation = MutableStateFlow(OperationType.MULTIPLICATION)
    val selectedOperation: StateFlow<OperationType> = _selectedOperation.asStateFlow()

    private val _activeSession = MutableStateFlow<GameSession?>(null)
    val activeSession: StateFlow<GameSession?> = _activeSession.asStateFlow()

    private val _lastGameResult = MutableStateFlow<GameResult?>(null)
    val lastGameResult: StateFlow<GameResult?> = _lastGameResult.asStateFlow()

    private val _newUnlockedBadge = MutableStateFlow<String?>(null)
    val newUnlockedBadge: StateFlow<String?> = _newUnlockedBadge.asStateFlow()

    init {
        repository.updateStreak()
        val s = repository.saveState.value
        soundManager.soundEnabled = s.soundEnabled
        soundManager.musicEnabled = s.musicEnabled
    }

    fun navigateTo(screen: Screen) {
        soundManager.click()
        _currentScreen.value = screen
    }

    fun selectOperation(operation: OperationType) {
        soundManager.click()
        _selectedOperation.value = operation
        _currentScreen.value = Screen.MAP
    }

    fun startGame(operation: OperationType, table: Int, levelIndex: Int) {
        soundManager.click()
        _selectedOperation.value = operation
        _activeSession.value = GameSession(operation, table, levelIndex)
        _currentScreen.value = Screen.GAME
    }

    fun finishGame(result: GameResult) {
        val sess = _activeSession.value ?: return
        _lastGameResult.value = result
        _newUnlockedBadge.value = null

        repository.handleGameComplete(sess, result) { newBadge ->
            _newUnlockedBadge.value = newBadge
        }

        _currentScreen.value = Screen.CHEST
    }

    fun continueFromChest() {
        soundManager.click()
        _currentScreen.value = Screen.MAP
    }

    fun recordQuestion(operation: OperationType, a: Int, b: Int, correct: Boolean) {
        repository.recordQuestionResult(operation, a, b, correct)
    }

    fun buyCosmetic(item: CosmeticItem) {
        soundManager.diamond()
        repository.buyCosmetic(item)
    }

    fun equipCosmetic(item: CosmeticItem) {
        soundManager.click()
        repository.equipCosmetic(item)
    }

    fun unequipCosmetic(item: CosmeticItem) {
        soundManager.click()
        repository.unequipCosmetic(item)
    }

    fun toggleSound() {
        val newState = repository.toggleSound()
        soundManager.soundEnabled = newState
        if (newState) soundManager.click()
    }

    fun toggleMusic() {
        val newState = repository.toggleMusic()
        soundManager.musicEnabled = newState
        if (newState) soundManager.click()
    }

    fun changeParentPin(pin: String): Boolean {
        soundManager.click()
        return repository.changeParentPin(pin)
    }

    fun toggleIslandUnlock(operation: OperationType, table: Int) {
        soundManager.click()
        repository.toggleIslandUnlock(operation, table)
    }

    fun unlockAllIslands(operation: OperationType? = null) {
        soundManager.click()
        repository.unlockAllIslands(operation)
    }

    fun lockAllIslands(operation: OperationType? = null) {
        soundManager.click()
        repository.lockAllIslands(operation)
    }

    fun resetToInitialLocks() {
        soundManager.click()
        repository.resetToInitialLocks()
    }

    fun resetProgress() {
        soundManager.click()
        repository.resetProgress()
    }

    fun getParentStats(): ParentStats {
        return repository.computeParentStats()
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}

