package com.example.incognito.ViewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {


    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    private val _scrollPos = MutableStateFlow(0)
    val scrollPos: StateFlow<Int> = _scrollPos

    fun setScrollPos(pos: Int) {
        _scrollPos.value = pos
    }
}
