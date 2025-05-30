package com.example.incognito.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.incognito.Model.Assignment
import com.example.incognito.Model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // UI State
    private val _assignments = MutableStateFlow<List<Assignment>>(emptyList())
    val assignments: StateFlow<List<Assignment>> = _assignments.asStateFlow()

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _scrollPosition = MutableStateFlow(0)
    val scrollPosition: StateFlow<Int> = _scrollPosition.asStateFlow()

    // UI Actions
    fun addAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _assignments.value = _assignments.value + assignment
        }
    }

    fun updateAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _assignments.value = _assignments.value.map { 
                if (it.id == assignment.id) assignment else it 
            }
        }
    }

    fun deleteAssignment(assignmentId: String) {
        viewModelScope.launch {
            _assignments.value = _assignments.value.filter { it.id != assignmentId }
        }
    }

    fun addPlayer(player: Player) {
        viewModelScope.launch {
            _players.value = _players.value + player
        }
    }

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun updateScrollPosition(position: Int) {
        _scrollPosition.value = position
    }
}
