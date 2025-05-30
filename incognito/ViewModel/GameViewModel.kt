package com.example.incognito.ViewModel

import androidx.lifecycle.ViewModel
import com.example.incognito.Model.Assignment
import com.example.incognito.Model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GameState(
    val playerCount: Int = 0,
    val players: Assignment = Assignment(emptyList()),
    val eliminatedPlayers: Map<Int, String> = emptyMap(),
    val hasSeenWord: List<Boolean> = emptyList(),
    val hasSeenTimes: List<Int> = emptyList(),
    val roundText: String = "Incognito",
    val isPoliceWon: Boolean = false,
    val isUndercoverWon: Boolean = false,
    val isIncognitoWon: Boolean = false,
    val isTie: Boolean = false,
    val showIncognitoPredictionDialog: Boolean = false,
    val showIncognitoRoleDialog: Boolean = false,
    val incognitoIndex: Int = -1,
    val prediction: String = ""
)

sealed class GameEvent {
    data class PlayerEliminated(val index: Int, val reason: String) : GameEvent()
    data class WordSeen(val playerIndex: Int) : GameEvent()
    data class UpdatePrediction(val prediction: String) : GameEvent()
    data class SetIncognitoIndex(val index: Int) : GameEvent()
    object ShowIncognitoPredictionDialog : GameEvent()
    object HideIncognitoPredictionDialog : GameEvent()
    object ShowIncognitoRoleDialog : GameEvent()
    object HideIncognitoRoleDialog : GameEvent()
    object ResetGame : GameEvent()
}

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    fun initializeGame(playerCount: Int) {
        _gameState.update { currentState ->
            currentState.copy(
                playerCount = playerCount,
                players = Assignment(playerCount),
                hasSeenWord = List(playerCount) { false },
                hasSeenTimes = List(playerCount) { 0 }
            )
        }
    }

    fun handleEvent(event: GameEvent) {
        when (event) {
            is GameEvent.PlayerEliminated -> eliminatePlayer(event.index, event.reason)
            is GameEvent.WordSeen -> markWordAsSeen(event.playerIndex)
            is GameEvent.UpdatePrediction -> updatePrediction(event.prediction)
            is GameEvent.SetIncognitoIndex -> setIncognitoIndex(event.index)
            is GameEvent.ShowIncognitoPredictionDialog -> showIncognitoPredictionDialog()
            is GameEvent.HideIncognitoPredictionDialog -> hideIncognitoPredictionDialog()
            is GameEvent.ShowIncognitoRoleDialog -> showIncognitoRoleDialog()
            is GameEvent.HideIncognitoRoleDialog -> hideIncognitoRoleDialog()
            is GameEvent.ResetGame -> resetGame()
        }
        checkWinConditions()
    }

    private fun eliminatePlayer(index: Int, reason: String) {
        _gameState.update { currentState ->
            currentState.copy(
                eliminatedPlayers = currentState.eliminatedPlayers + (index to reason)
            )
        }
    }

    private fun markWordAsSeen(playerIndex: Int) {
        _gameState.update { currentState ->
            val newHasSeenWord = currentState.hasSeenWord.toMutableList().apply {
                set(playerIndex, true)
            }
            val newHasSeenTimes = currentState.hasSeenTimes.toMutableList().apply {
                set(playerIndex, this[playerIndex] + 1)
            }
            currentState.copy(
                hasSeenWord = newHasSeenWord,
                hasSeenTimes = newHasSeenTimes
            )
        }
    }

    private fun updatePrediction(newPrediction: String) {
        _gameState.update { it.copy(prediction = newPrediction) }
    }

    private fun setIncognitoIndex(index: Int) {
        _gameState.update { it.copy(incognitoIndex = index) }
    }

    private fun showIncognitoPredictionDialog() {
        _gameState.update { it.copy(showIncognitoPredictionDialog = true) }
    }

    private fun hideIncognitoPredictionDialog() {
        _gameState.update { it.copy(showIncognitoPredictionDialog = false) }
    }

    private fun showIncognitoRoleDialog() {
        _gameState.update { it.copy(showIncognitoRoleDialog = true) }
    }

    private fun hideIncognitoRoleDialog() {
        _gameState.update { it.copy(showIncognitoRoleDialog = false) }
    }

    private fun resetGame() {
        _gameState.update { currentState ->
            GameState(playerCount = currentState.playerCount)
        }
    }

    private fun checkWinConditions() {
        val currentState = _gameState.value
        val activePlayerCount = currentState.playerCount - currentState.eliminatedPlayers.size

        var activePoliceCount = 0
        var activeUndercoverCount = 0
        var incognitoStillActive = false

        currentState.players.players.forEachIndexed { index, player ->
            if (!currentState.eliminatedPlayers.containsKey(index)) {
                when {
                    player.isPolice -> activePoliceCount++
                    player.isUndercover -> activeUndercoverCount++
                    player.isIncognito -> incognitoStillActive = true
                }
            }
        }

        _gameState.update { state ->
            state.copy(
                isPoliceWon = !incognitoStillActive && activeUndercoverCount == 0,
                isUndercoverWon = activeUndercoverCount >= 1 && activePoliceCount == 0 && !incognitoStillActive,
                isIncognitoWon = incognitoStillActive && (
                    (activePoliceCount == 1 && activePlayerCount == 2) ||
                    (activeUndercoverCount == 1 && activePlayerCount == 2)
                ),
                isTie = activeUndercoverCount == 1 && activePoliceCount == 1 && activePlayerCount == 2
            )
        }
    }
} 