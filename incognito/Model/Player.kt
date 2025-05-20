package com.example.incognito.Model

data class Player(
    val name: String = "",
    val id: Int = 0,
    val isPolice: Boolean = false,
    val isUndercover: Boolean = false,
    val isIncognito: Boolean = false,
    val word: String = ""  // Add this field
)