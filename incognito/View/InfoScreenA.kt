package com.example.incognito.View

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.PlayArrow

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.key
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.incognito.Model.Player
import com.example.incognito.Model.assignment
import kotlinx.coroutines.delay
import words


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreenA(playerCount: Int, onNavigateToStart: () -> Unit) {
    var tie by remember { mutableStateOf(false) }
    var policeWon by remember { mutableStateOf(false) }
    var players by remember { mutableStateOf(assignment(playerCount)) }
    var eliminatedPlayers by remember { mutableStateOf(mutableMapOf<Int, String>()) }
    var hasSeenWord by remember { mutableStateOf(MutableList(playerCount) { false }) }
    var roundText by remember { mutableStateOf("Incognito") }
    val randomNumber by remember { mutableStateOf((0..29).random()) }
    var openDialog0 by remember { mutableStateOf(false) }
    var undercoverWon by remember { mutableStateOf(false) }
    var showIncognitoPredictionDialog by remember { mutableStateOf(false) }
    var incognitoIndex by remember { mutableStateOf(-1) }
    var prediction by remember { mutableStateOf("") }
    var incognitoWon by remember { mutableStateOf(false) }
    var showIncognitoRoleDialog by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    //val focusManager = LocalFocusManager.current
    //focusManager.clearFocus()

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



    // Function to check game win conditions - unchanged
    fun checkWinConditions() {
        // Count active players (non-eliminated)
        val activePlayerCount = playerCount - eliminatedPlayers.size

        // Count active player types
        var activePoliceCount = 0
        var activeUndercoverCount = 0
        var incognitoStillActive = false

        players.forEachIndexed { index, player ->
            if (!eliminatedPlayers.containsKey(index)) {
                when {
                    player.isPolice -> activePoliceCount++
                    player.isUndercover -> activeUndercoverCount++
                    player.isIncognito -> incognitoStillActive = true
                }
            }
        }

        // Check win condition for Police
        if (!incognitoStillActive && activeUndercoverCount == 0) {
            // Police wins if both Undercovers and Incognito are eliminated
            policeWon = true
            return
        }

        // Check win conditions for Impostors (Undercovers and Incognito)
        if (activeUndercoverCount >= 1 && activePoliceCount == 0 && !incognitoStillActive) {
            // Undercovers win if all police are eliminated
            undercoverWon = true
            return
        }

        // Check tie conditions
        if (activeUndercoverCount == 1 && activePoliceCount == 1 && activePlayerCount == 2) {
            tie = true
            return
        }

        if(incognitoStillActive && activePoliceCount == 1 && activePlayerCount == 2){
            incognitoWon = true
        }

        if(incognitoStillActive && activeUndercoverCount == 1 && activePlayerCount == 2){
            incognitoWon = true
        }
    }



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
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                keyboardController?.hide()
            }
    ) {
        // Game status indicator - new!
        AnimatedVisibility(
            visible = hasSeenWord.all { it },
            enter = fadeIn() + expandVertically(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val activeCount = playerCount - eliminatedPlayers.size
                    Text(
                        text = "$activeCount",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "active",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 24.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .imePadding()
        ) {
            // Game Header with enhanced styling
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .shadow(
                        elevation = 16.dp,
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(28.dp)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 16.dp)
                ) {
                    // Animated title
                    val infiniteTitleTransition = rememberInfiniteTransition()
                    val titleScale by infiniteTitleTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.05f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = EaseInOutQuad),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Text(
                        text = roundText,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(8.dp)
                            .scale(titleScale)
                            .graphicsLayer {
                                shadowElevation = 12f
                                shape = RoundedCornerShape(12.dp)
                                clip = true
                            }
                    )

                    // Improved progress indicator
                    val seenCount = hasSeenWord.count { it }
                    if (seenCount < playerCount) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Players ready: $seenCount/$playerCount",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                AnimatedVisibility(visible = seenCount > 0 && seenCount < playerCount) {
                                    Icon(
                                        imageVector = Icons.Filled.Face,
                                        contentDescription = "Waiting",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .size(18.dp)
                                    )
                                }
                            }

                            // Animated progress indicator
                            val progressAnimation by animateFloatAsState(
                                targetValue = seenCount.toFloat() / playerCount,
                                animationSpec = tween(500, easing = EaseOutCubic)
                            )

                            LinearProgressIndicator(
                                progress = progressAnimation,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .height(12.dp)
                                    .fillMaxWidth(0.8f)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                            )
                        }
                    } else {
                        // Game is in elimination phase
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ExitToApp,
                                contentDescription = "Elimination Phase",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Elimination Phase",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            // Improved Player grid with enhanced styling and animations
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 175.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(players.indices.toList()) { index ->
                    var showWordDialog by remember { mutableStateOf(false) }
                    var showRoleDialog by remember { mutableStateOf(false) }

                    // Card animation for player elimination
                    val isEliminated = eliminatedPlayers.containsKey(index)
                    val cardElevation by animateFloatAsState(
                        targetValue = if (isEliminated) 2f else 8f,
                        animationSpec = tween(500)
                    )

                    val cardRotation by animateFloatAsState(
                        targetValue = if (isEliminated) -2f else 0f,
                        animationSpec = tween(500)
                    )

                    val cardScale by animateFloatAsState(
                        targetValue = if (isEliminated) 0.97f else 1f,
                        animationSpec = tween(500)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                translationY = if (isEliminated) 4f else 0f
                                rotationZ = cardRotation
                                scaleX = cardScale
                                scaleY = cardScale
                                shadowElevation = cardElevation
                            }
                            .shadow(
                                elevation = if (isEliminated) 2.dp else 8.dp,
                                shape = RoundedCornerShape(28.dp),
                                spotColor = if (isEliminated)
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                                else
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEliminated)
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                        ),
                        border = if (isEliminated)
                            BorderStroke(2.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                        else
                            null
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Enhanced player avatar with animations
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isEliminated)
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                        else if (hasSeenWord[index])
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                        else
                                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                                    )
                                    .border(
                                        width = 3.dp,
                                        color = if (isEliminated)
                                            MaterialTheme.colorScheme.onError.copy(alpha = 0.3f)
                                        else if (hasSeenWord[index])
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                        else
                                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val playerIcon = if (isEliminated) {
                                    Icons.Filled.Face
                                } else if (hasSeenWord[index]) {
                                    Icons.Filled.AccountBox
                                } else {
                                    Icons.Filled.Person
                                }

                                Icon(
                                    imageVector = playerIcon,
                                    contentDescription = "Player",
                                    tint = if (isEliminated)
                                        MaterialTheme.colorScheme.onError
                                    else if (hasSeenWord[index])
                                        MaterialTheme.colorScheme.onSecondary
                                    else
                                        MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Improved player name field
                            OutlinedTextField(
                                value = players[index].name,
                                onValueChange = { newName ->
                                    players = players.toMutableList().apply {
                                        this[index] = this[index].copy(name = newName)
                                    }
                                },
                                label = {
                                    Text("Player ${index + 1}", fontWeight = FontWeight.Medium)
                                },
                                enabled = !isEliminated,
                                shape = RoundedCornerShape(20.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.ime)
                                    .onKeyEvent { keyEvent ->
                                        if (keyEvent.key == Key.Enter) {
                                            // Hide the keyboard
                                            keyboardController?.hide()
                                            // Optionally, you can also perform any additional actions here
                                            true // Indicate that the key event has been handled
                                        } else {
                                            false // Indicate that the key event has not been handled
                                        }
                                    }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Enhanced action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // See word button with enhanced design
                                ElevatedButton(
                                    onClick = {
                                        hasSeenWord[index] = true
                                        showWordDialog = true

                                        // Check if all players have seen their words
                                        if (hasSeenWord.all { it }) {
                                            roundText = "Incognito - Elimination Round"
                                        }
                                    },
                                    enabled = !isEliminated && !incognitoWon && !policeWon && !tie,
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = if (hasSeenWord[index])
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                        contentColor = if (hasSeenWord[index])
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onPrimary
                                    ),
                                    elevation = ButtonDefaults.elevatedButtonElevation(
                                        defaultElevation = 6.dp,
                                        pressedElevation = 10.dp
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (hasSeenWord[index])
                                                Icons.Default.AccountCircle
                                            else
                                                Icons.Rounded.PlayArrow,
                                            contentDescription = "See word",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            if (hasSeenWord[index]) "View Again" else "View",
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Eliminate button with enhanced design
                                FilledTonalButton(
                                    onClick = {
                                        // Check if all players have seen their words
                                        if (hasSeenWord.all { it }) {
                                            if (players[index].isIncognito) {
                                                incognitoIndex = index
                                                showIncognitoPredictionDialog = true
                                            } else {
                                                // For non-Incognito players, eliminate immediately
                                                val role = when {
                                                    players[index].isUndercover -> "Undercover"
                                                    players[index].isPolice -> "Police"
                                                    else -> "Unknown"
                                                }
                                                eliminatedPlayers = eliminatedPlayers.toMutableMap().apply {
                                                    put(index, role)
                                                }
                                                showRoleDialog = true
                                                checkWinConditions()
                                            }
                                        } else {
                                            openDialog0 = true
                                        }
                                    },
                                    enabled = !isEliminated && !incognitoWon && !policeWon && !tie,
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Clear,
                                            contentDescription = "Eliminate",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "Eliminate",
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            // Enhanced eliminated status
                            AnimatedVisibility(
                                visible = isEliminated,
                                enter = fadeIn() + expandVertically(
                                    expandFrom = Alignment.Top,
                                    animationSpec = tween(500, easing = EaseOutQuad)
                                ),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = when (eliminatedPlayers[index]) {
                                                "Police" -> Icons.Default.AccountBox
                                                "Undercover" -> Icons.Default.AccountCircle
                                                "Incognito" -> Icons.Default.Face
                                                else -> Icons.Default.Close
                                            },
                                            contentDescription = "Role",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier
                                                .size(20.dp)
                                                .padding(end = 8.dp)
                                        )
                                        Text(
                                            "Eliminated - ${eliminatedPlayers[index]}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.error,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            // Word dialog - enhanced
                            if (showWordDialog) {
                                AlertDialog(
                                    onDismissRequest = { showWordDialog = false },
                                    title = {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            if (players[index].isIncognito) {
                                                Icon(
                                                    imageVector = Icons.Filled.Star,
                                                    contentDescription = "Incognito",
                                                    tint = MaterialTheme.colorScheme.tertiary,
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .padding(end = 8.dp)
                                                )
                                            }
                                            Text(
                                                "Your Secret Word",
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontWeight = FontWeight.ExtraBold
                                                ),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    },
                                    text = {
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = when {
                                                    players[index].isIncognito -> MaterialTheme.colorScheme.tertiaryContainer
                                                    players[index].isPolice -> MaterialTheme.colorScheme.primaryContainer
                                                    players[index].isUndercover -> MaterialTheme.colorScheme.secondaryContainer
                                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                                }
                                            ),
                                            shape = RoundedCornerShape(24.dp),
                                            border = BorderStroke(
                                                width = 2.dp,
                                                color = when {
                                                    players[index].isIncognito -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                                                    players[index].isPolice -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                                    players[index].isUndercover -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                                }
                                            ),
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = 8.dp
                                            ),
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(24.dp)
                                            ) {
                                                if (players[index].isIncognito) {
                                                    // Animated Incognito Icon
                                                    val iconRotation by infiniteTransition.animateFloat(
                                                        initialValue = -5f,
                                                        targetValue = 5f,
                                                        animationSpec = infiniteRepeatable(
                                                            animation = tween(2000, easing = EaseInOutQuad),
                                                            repeatMode = RepeatMode.Reverse
                                                        )
                                                    )

                                                    Icon(
                                                        imageVector = Icons.Filled.Star,
                                                        contentDescription = "Incognito",
                                                        tint = MaterialTheme.colorScheme.tertiary,
                                                        modifier = Modifier
                                                            .size(64.dp)
                                                            .padding(bottom = 16.dp)
                                                            .graphicsLayer {
                                                                rotationZ = iconRotation
                                                            }
                                                    )
                                                    Text(
                                                        "You are the Incognito!",
                                                        style = MaterialTheme.typography.titleLarge.copy(
                                                            fontWeight = FontWeight.ExtraBold
                                                        ),
                                                        color = MaterialTheme.colorScheme.tertiary,
                                                        textAlign = TextAlign.Center
                                                    )

                                                    Spacer(modifier = Modifier.height(16.dp))

                                                    Card(
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                                        ),
                                                        shape = RoundedCornerShape(16.dp)
                                                    ) {
                                                        Text(
                                                            "Listen carefully to figure out the word!",
                                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                                fontWeight = FontWeight.Medium
                                                            ),
                                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.padding(16.dp)
                                                        )
                                                    }
                                                }
                                                if (players[index].isPolice) {
                                                    Text(
                                                        words[randomNumber].first,
                                                        style = MaterialTheme.typography.headlineMedium.copy(
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                        textAlign = TextAlign.Center
                                                    )
                                                    /*Text(
                                                        "You are Police",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.padding(top = 8.dp)
                                                    )*/
                                                }
                                                if (players[index].isUndercover) {
                                                    Text(
                                                        words[randomNumber].second,
                                                        style = MaterialTheme.typography.headlineMedium.copy(
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                        textAlign = TextAlign.Center
                                                    )
                                                    /*Text(
                                                        "You are Undercover",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.padding(top = 8.dp)
                                                    )*/
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = { showWordDialog = false },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier.fillMaxWidth(0.7f)
                                        ) {
                                            Text("Got it", fontWeight = FontWeight.Medium)
                                        }
                                    },
                                    shape = RoundedCornerShape(28.dp),
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 8.dp
                                )
                            }

                            // Dialog to show the role after elimination
                            if (showRoleDialog) {
                                AlertDialog(
                                    onDismissRequest = { showRoleDialog = false },
                                    title = {
                                        Text(
                                            "Player Eliminated",
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    },
                                    text = {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.errorContainer)
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = "Eliminated",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(48.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))

                                            Text(
                                                "${players[index].name} was a",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                                textAlign = TextAlign.Center
                                            )

                                            Text(
                                                "${eliminatedPlayers[index]}",
                                                style = MaterialTheme.typography.headlineMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.error,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = { showRoleDialog = false },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier.fillMaxWidth(0.7f)
                                        ) {
                                            Text("Continue", fontWeight = FontWeight.Medium)
                                        }
                                    },
                                    shape = RoundedCornerShape(28.dp),
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 8.dp
                                )
                            }
                        }
                    }

                    // Show role dialog for incognito after incorrect prediction
                    if (index == incognitoIndex && showIncognitoRoleDialog) {
                        AlertDialog(
                            onDismissRequest = { showIncognitoRoleDialog = false },
                            title = {
                                Text(
                                    "Player Eliminated",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            text = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "Incognito",
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        "${players[index].name} was the",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        "Incognito",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.tertiary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )

                                    Text(
                                        "Their guess was incorrect!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = { showIncognitoRoleDialog = false },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.fillMaxWidth(0.7f)
                                ) {
                                    Text("Continue", fontWeight = FontWeight.Medium)
                                }
                            },
                            shape = RoundedCornerShape(28.dp),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        )
                    }
                }
            }
        }
    }

    // Game state dialogs - always check these at the end to make sure they show up on top

    if (tie) {
        AlertDialog(
            onDismissRequest = { /* Do nothing to prevent dismissal */ },
            title = {
                Text(
                    "Deadlock - No Winner",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Tie Game",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "The game ended in a tie!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "One police officer and one undercover agent remain.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Text(
                        "The correct word was: ${words[randomNumber].first}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onNavigateToStart() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("New Game", fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }

    if (incognitoWon) {
        AlertDialog(
            onDismissRequest = { /* Do nothing to prevent dismissal */ },
            title = {
                Text(
                    "Incognito Wins!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Incognito Victory",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "The Incognito player has won!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "Only two players remain and one is the Incognito.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Text(
                        "The correct word was: ${words[randomNumber].first}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onNavigateToStart() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("New Game", fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }

    if (policeWon) {
        AlertDialog(
            onDismissRequest = { /* Do nothing to prevent dismissal */ },
            title = {
                Text(
                    "Police Win!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Police Victory",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "The Police have won!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "All Undercover agents and the Incognito player have been eliminated.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Text(
                        "The correct word was: ${words[randomNumber].first}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onNavigateToStart() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("New Game", fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }

    if (undercoverWon) {
        AlertDialog(
            onDismissRequest = { /* Do nothing to prevent dismissal */ },
            title = {
                Text(
                    "Undercover Win!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Undercover Victory",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "The Undercover agents have won!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "All Police officers have been eliminated.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Text(
                        "The undercover word was: ${words[randomNumber].second}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onNavigateToStart() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("New Game", fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }

    // Dialog for when players try to eliminate before everyone has seen their word
    if (openDialog0) {
        AlertDialog(
            onDismissRequest = { openDialog0 = false },
            title = {
                Text(
                    "Wait!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Warning",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "All players must view their word before eliminations can begin.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { openDialog0 = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Got it", fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }

    // Incognito prediction dialog
    if (showIncognitoPredictionDialog) {
        AlertDialog(
            onDismissRequest = { showIncognitoPredictionDialog = false },
            title = {
                Text(
                    "Incognito Prediction",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Incognito",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "${players[incognitoIndex].name} is the Incognito!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "The Incognito must now guess the secret word.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    OutlinedTextField(
                        value = prediction,
                        onValueChange = { prediction = it },
                        label = { Text("Enter your guess") },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedLabelColor = MaterialTheme.colorScheme.tertiary
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp).imePadding()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Check if prediction is correct (case insensitive)
                        if (prediction.trim().equals(words[randomNumber].first, ignoreCase = true)) {
                            // Incognito wins immediately
                            incognitoWon = true
                        } else {
                            // Incognito is eliminated
                            eliminatedPlayers = eliminatedPlayers.toMutableMap().apply {
                                put(incognitoIndex, "Incognito")
                            }
                            // Show dialog to reveal the role
                            showIncognitoRoleDialog = true
                            // Check win conditions
                            checkWinConditions()
                        }
                        showIncognitoPredictionDialog = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Submit Guess", fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showIncognitoPredictionDialog = false },
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.tertiary)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }
}
