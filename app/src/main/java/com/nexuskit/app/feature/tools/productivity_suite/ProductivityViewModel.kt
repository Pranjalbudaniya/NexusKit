package com.nexuskit.app.feature.tools.productivity_suite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ProductivityUiState(
    val selectedTab: ProdTab = ProdTab.POMODORO,
    // Pomodoro
    val isWorkSession: Boolean = true,
    val isRunning: Boolean = false,
    val remainingSeconds: Int = 25 * 60,
    val completedSessions: Int = 0,
    // To-Do
    val todos: List<TodoItem> = ProductivityEngine.defaultTodos,
    val newTodoText: String = "",
    val newTodoPriority: String = "Medium",
    // Habits
    val habits: List<HabitItem> = ProductivityEngine.defaultHabits,
    val newHabitName: String = "",
    // Scratchpad
    val scratchpadText: String = "Meeting notes:\n- Offline utility tools active\n- Instant response times\n- Zero battery drain"
)

@HiltViewModel
class ProductivityViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProductivityUiState())
    val uiState: StateFlow<ProductivityUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("productivity_suite"))
        }
    }

    fun onTabSelected(tab: ProdTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun togglePomodoroTimer() {
        if (_uiState.value.isRunning) {
            pausePomodoro()
        } else {
            startPomodoro()
        }
    }

    private fun startPomodoro() {
        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1000L)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            // Session finished
            val nextIsWork = !_uiState.value.isWorkSession
            val nextDuration = if (nextIsWork) 25 * 60 else 5 * 60
            val nextSessions = if (_uiState.value.isWorkSession) _uiState.value.completedSessions + 1 else _uiState.value.completedSessions
            _uiState.update {
                it.copy(
                    isRunning = false,
                    isWorkSession = nextIsWork,
                    remainingSeconds = nextDuration,
                    completedSessions = nextSessions
                )
            }
        }
    }

    fun pausePomodoro() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(isRunning = false) }
    }

    fun resetPomodoro() {
        pausePomodoro()
        val duration = if (_uiState.value.isWorkSession) 25 * 60 else 5 * 60
        _uiState.update { it.copy(remainingSeconds = duration) }
    }

    fun onNewTodoTextChanged(text: String) {
        _uiState.update { it.copy(newTodoText = text) }
    }

    fun onNewTodoPriorityChanged(pri: String) {
        _uiState.update { it.copy(newTodoPriority = pri) }
    }

    fun addTodo() {
        val t = _uiState.value.newTodoText.trim()
        if (t.isEmpty()) return
        val item = TodoItem(UUID.randomUUID().toString(), t, _uiState.value.newTodoPriority, false)
        _uiState.update { it.copy(todos = listOf(item) + it.todos, newTodoText = "") }
    }

    fun toggleTodo(id: String) {
        _uiState.update { state ->
            state.copy(
                todos = state.todos.map {
                    if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
                }
            )
        }
    }

    fun deleteTodo(id: String) {
        _uiState.update { state ->
            state.copy(todos = state.todos.filter { it.id != id })
        }
    }

    fun onNewHabitNameChanged(name: String) {
        _uiState.update { it.copy(newHabitName = name) }
    }

    fun addHabit() {
        val n = _uiState.value.newHabitName.trim()
        if (n.isEmpty()) return
        val habit = HabitItem(UUID.randomUUID().toString(), n, 1, true)
        _uiState.update { it.copy(habits = it.habits + habit, newHabitName = "") }
    }

    fun toggleHabit(id: String) {
        _uiState.update { state ->
            state.copy(
                habits = state.habits.map {
                    if (it.id == id) {
                        val nextDone = !it.isDoneToday
                        val nextStreak = if (nextDone) it.streakDays + 1 else (it.streakDays - 1).coerceAtLeast(0)
                        it.copy(isDoneToday = nextDone, streakDays = nextStreak)
                    } else it
                }
            )
        }
    }

    fun onScratchpadChanged(text: String) {
        _uiState.update { it.copy(scratchpadText = text) }
    }
}
