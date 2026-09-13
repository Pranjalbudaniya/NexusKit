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

    val defaultTodos = listOf(
        TodoItem("1", "Review project architecture and specs", "High", true),
        TodoItem("2", "Test offline utility tools on device", "High", false),
        TodoItem("3", "Organize home screen widgets", "Medium", false),
        TodoItem("4", "Drink 2.5L water today", "Low", false)
    )

    val defaultHabits = listOf(
        HabitItem("1", "Morning Workout / Walk", 7, true),
        HabitItem("2", "Read 20 Pages", 14, false),
        HabitItem("3", "Drink 2.5L Water", 5, true),
        HabitItem("4", "Code / Learn New Skill", 21, true)
    )
}
