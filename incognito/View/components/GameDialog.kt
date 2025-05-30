package com.example.incognito.View.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun GameDialog(
    onDismissRequest: () -> Unit,
    title: String,
    content: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Box(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}

@Composable
fun IncognitoPredictionDialog(
    currentPrediction: String,
    onPredictionChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    GameDialog(
        onDismissRequest = onDismiss,
        title = "Make Your Prediction",
        content = {
            OutlinedTextField(
                value = currentPrediction,
                onValueChange = onPredictionChange,
                label = { Text("Enter your word prediction") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = currentPrediction.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun GameOverDialog(
    title: String,
    message: String,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    GameDialog(
        onDismissRequest = { /* Do nothing, force user to choose */ },
        title = title,
        content = {
            Text(message)
        },
        confirmButton = {
            Button(onClick = onPlayAgain) {
                Text("Play Again")
            }
        },
        dismissButton = {
            TextButton(onClick = onExit) {
                Text("Exit")
            }
        }
    )
} 