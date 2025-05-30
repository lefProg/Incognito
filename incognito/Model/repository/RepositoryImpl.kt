package com.example.incognito.Model.repository

import com.example.incognito.Model.Assignment
import com.example.incognito.Model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RepositoryImpl : Repository {
    private val assignments = MutableStateFlow<List<Assignment>>(emptyList())
    private val players = MutableStateFlow<List<Player>>(emptyList())

    override fun getAssignments(): Flow<List<Assignment>> = assignments.asStateFlow()

    override fun getPlayers(): Flow<List<Player>> = players.asStateFlow()

    override suspend fun addAssignment(assignment: Assignment) {
        assignments.value = assignments.value + assignment
    }

    override suspend fun addPlayer(player: Player) {
        players.value = players.value + player
    }

    override suspend fun updateAssignment(assignment: Assignment) {
        assignments.value = assignments.value.map { 
            if (it.id == assignment.id) assignment else it 
        }
    }

    override suspend fun deleteAssignment(assignmentId: String) {
        assignments.value = assignments.value.filter { it.id != assignmentId }
    }
} 