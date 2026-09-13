package com.nexuskit.app.feature.tools.randomizer

import java.security.SecureRandom

enum class DiceType(val sides: Int, val label: String) {
    D4(4, "d4"),
    D6(6, "d6"),
    D8(8, "d8"),
    D10(10, "d10"),
    D12(12, "d12"),
    D20(20, "d20"),
    D100(100, "d100")
}

data class DiceRollResult(
    val rolls: List<Int>,
    val totalSum: Int,
    val diceType: DiceType
)

data class NumberRangeResult(
    val numbers: List<Int>,
    val min: Int,
    val max: Int
)

object RandomizerEngine {

    private val random = SecureRandom()

    fun generateNumbers(min: Int, max: Int, count: Int = 1, unique: Boolean = true): NumberRangeResult {
        val low = kotlin.math.min(min, max)
        val high = kotlin.math.max(min, max)
        val safeCount = count.coerceIn(1, 100)

        val results = if (unique && (high - low + 1) >= safeCount) {
            val pool = (low..high).toMutableList()
            val list = mutableListOf<Int>()
            repeat(safeCount) {
                val idx = random.nextInt(pool.size)
                list.add(pool.removeAt(idx))
            }
            list
        } else {
            (1..safeCount).map { random.nextInt(high - low + 1) + low }
        }

        return NumberRangeResult(results, low, high)
    }

    fun rollDice(type: DiceType, count: Int = 1): DiceRollResult {
        val safeCount = count.coerceIn(1, 10)
        val rolls = (1..safeCount).map { random.nextInt(type.sides) + 1 }
        return DiceRollResult(rolls, rolls.sum(), type)
    }

    fun flipCoin(): String {
        return if (random.nextBoolean()) "Heads" else "Tails"
    }

    fun pickFromList(items: List<String>): String? {
        val valid = items.filter { it.isNotBlank() }
        if (valid.isEmpty()) return null
        return valid[random.nextInt(valid.size)]
    }

    fun shuffleList(items: List<String>): List<String> {
        val valid = items.filter { it.isNotBlank() }.toMutableList()
        valid.shuffle(java.util.Random(random.nextLong()))
        return valid
    }
}
