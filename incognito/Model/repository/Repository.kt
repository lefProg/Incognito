package com.example.incognito.Model.repository

import com.example.incognito.Model.Assignment
import com.example.incognito.Model.Player
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getAssignments(): Flow<List<Assignment>>
    fun getPlayers(): Flow<List<Player>>
    suspend fun addAssignment(assignment: Assignment)
    suspend fun addPlayer(player: Player)
    suspend fun updateAssignment(assignment: Assignment)
    suspend fun deleteAssignment(assignmentId: String)
} 