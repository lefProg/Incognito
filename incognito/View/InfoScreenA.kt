package com.example.incognito.View

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incognito.Model.assignment
import com.example.incognito.ViewModel.GameEvent
import com.example.incognito.ViewModel.GameViewModel
import com.example.incognito.ViewModel.MainViewModel
import words

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreenA(
    playerCount: Int,
    onNavigateToStart: () -> Unit,
    mainViewModel: MainViewModel
) {
    val gameViewModel: GameViewModel = viewModel()
    val gameState by gameViewModel.gameState.collectAsState()
    val gridState = rememberLazyGridState()
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Initialize game if needed
    LaunchedEffect(playerCount) {
        gameViewModel.initializeGame(playerCount)
    }

    // Background animation
    val infiniteTransition = rememberInfiniteTransition()
    val backgroundAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Dynamic colors based on game state
    val primaryGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface.copy(alpha = backgroundAlpha)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryGradient)
            .imePadding()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                keyboardController?.hide()
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Game status indicator
            AnimatedVisibility(
                visible = gameState.hasSeenWord.all { it },
                enter = fadeIn() + expandVertically(),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "All players have seen their words",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Player grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(gameState.players.players.indices.toList()) { index ->
                    PlayerCard(
                        player = gameState.players.players[index],
                        playerIndex = index,
                        isEliminated = gameState.eliminatedPlayers.containsKey(index),
                        hasSeenWord = gameState.hasSeenWord[index],
                        timesWordSeen = gameState.hasSeenTimes[index],
                        onViewWord = {
                            gameViewModel.handleEvent(GameEvent.WordSeen(index))
                            if (gameState.players.players[index].isIncognito) {
                                gameViewModel.handleEvent(GameEvent.ShowIncognitoPredictionDialog)
                                gameViewModel.handleEvent(GameEvent.SetIncognitoIndex(index))
                            }
                        },
                        onEliminate = {
                            gameViewModel.handleEvent(GameEvent.PlayerEliminated(index, "Eliminated by vote"))
                        }
                    )
                }
            }
        }

        // Game over dialogs
        when {
            gameState.isPoliceWon -> {
                GameOverDialog(
                    title = "Police Win!",
                    message = "The police have successfully eliminated all impostors!",
                    onPlayAgain = { gameViewModel.handleEvent(GameEvent.ResetGame) },
                    onExit = onNavigateToStart
                )
            }
            gameState.isUndercoverWon -> {
                GameOverDialog(
                    title = "Undercover Win!",
                    message = "The undercover agents have successfully eliminated all police!",
                    onPlayAgain = { gameViewModel.handleEvent(GameEvent.ResetGame) },
                    onExit = onNavigateToStart
                )
            }
            gameState.isIncognitoWon -> {
                GameOverDialog(
                    title = "Incognito Win!",
                    message = "The incognito player has successfully survived until the end!",
                    onPlayAgain = { gameViewModel.handleEvent(GameEvent.ResetGame) },
                    onExit = onNavigateToStart
                )
            }
            gameState.isTie -> {
                GameOverDialog(
                    title = "Game Tied!",
                    message = "The game has ended in a tie!",
                    onPlayAgain = { gameViewModel.handleEvent(GameEvent.ResetGame) },
                    onExit = onNavigateToStart
                )
            }
        }

        // Incognito prediction dialog
        if (gameState.showIncognitoPredictionDialog) {
            IncognitoPredictionDialog(
                currentPrediction = gameState.prediction,
                onPredictionChange = { gameViewModel.handleEvent(GameEvent.UpdatePrediction(it)) },
                onConfirm = { gameViewModel.handleEvent(GameEvent.HideIncognitoPredictionDialog) },
                onDismiss = { gameViewModel.handleEvent(GameEvent.HideIncognitoPredictionDialog) }
            )
        }
    }
}