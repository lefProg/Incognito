package com.example.incognito.View.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.incognito.Model.Player

@Composable
fun PlayerCard(
    player: Player,
    playerIndex: Int,
    isEliminated: Boolean,
    hasSeenWord: Boolean,
    timesWordSeen: Int,
    onViewWord: () -> Unit,
    onEliminate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardScale by animateFloatAsState(
        targetValue = if (isEliminated) 0.95f else 1f,
        label = "cardScale"
    )

    Card(
        modifier = modifier
            .padding(8.dp)
            .scale(cardScale),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isEliminated -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                isEliminated -> MaterialTheme.colorScheme.error
                hasSeenWord -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outline
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Player icon",
                tint = if (isEliminated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Player ${playerIndex + 1}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            if (timesWordSeen > 0) {
                Text(
                    text = "Word seen $timesWordSeen times",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalButton(
                    onClick = onViewWord,
                    enabled = !isEliminated
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "View word"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Word")
                }

                Button(
                    onClick = onEliminate,
                    enabled = !isEliminated,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminate")
                }
            }
        }
    }
} 