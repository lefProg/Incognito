package com.example.incognito.ViewModel.state

import com.example.incognito.Model.Assignment
import com.example.incognito.Model.Player

data class UiState(
    val assignments: List<Assignment> = emptyList(),
    val players: List<Player> = emptyList(),
    val isDarkMode: Boolean = true,
    val scrollPosition: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class NavigateTo(val route: String) : UiEvent()
    object NavigateBack : UiEvent()
}

sealed class UiAction {
    data class AddAssignment(val assignment: Assignment) : UiAction()
    data class UpdateAssignment(val assignment: Assignment) : UiAction()
    data class DeleteAssignment(val assignmentId: String) : UiAction()
    data class AddPlayer(val player: Player) : UiAction()
    object ToggleTheme : UiAction()
    data class UpdateScrollPosition(val position: Int) : UiAction()
} 