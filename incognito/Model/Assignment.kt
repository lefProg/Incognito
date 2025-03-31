package com.example.incognito.Model

import kotlin.random.Random

fun assignment(number: Int): List<Player> {
    // Create a list of players
    val players = MutableList(number) { Player("", it, false, false, false) }

    // Always have exactly 1 incognito
    val incognitoIndex = 0
    players[incognitoIndex] = players[incognitoIndex].copy(isIncognito = true)

    // Calculate the number of police and undercover based on the specified logic
    val remainingPlayers = number - 1  // Excluding the incognito

    var undercoverCount: Int
    var policeCount: Int

    // Revised logic for role distribution
    if (number <= 3) {
        // Special case for small player counts
        undercoverCount = 0
        policeCount = remainingPlayers
    } else {
        // Modified distribution to ensure more police than undercover
        undercoverCount = (remainingPlayers - 1) / 2  // Use integer division to floor the value
        policeCount = remainingPlayers - undercoverCount

        // Safety check to guarantee police > undercover
        if (policeCount <= undercoverCount) {
            undercoverCount -= 1
            policeCount += 1
        }
    }

    // Assign roles (starting after the incognito player)
    var currentIndex = 1

    // Assign undercover roles
    repeat(undercoverCount) {
        players[currentIndex] = players[currentIndex].copy(isUndercover = true)
        currentIndex++
    }

    // Assign police roles
    repeat(policeCount) {
        players[currentIndex] = players[currentIndex].copy(isPolice = true)
        currentIndex++
    }

    // Shuffle the list to randomize roles
    return players.shuffled(Random(System.currentTimeMillis()))
}