package com.nexuskit.app.feature.tools.productivity_suite

enum class ProdTab {
    POMODORO, TODO_LIST, HABIT_TRACKER, SCRATCHPAD
}

data class TodoItem(
    val id: String,
    val title: String,
    val priority: String, // "High", "Medium", "Low"
    val isCompleted: Boolean = false
)

data class HabitItem(
    val id: String,
    val name: String,
    val streakDays: Int,
    val isDoneToday: Boolean = false
)

object ProductivityEngine {

    val defaultTodos: List<TodoItem> = emptyList()

    val defaultHabits: List<HabitItem> = emptyList()
}

