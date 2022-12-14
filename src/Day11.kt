fun main() {
    fun part1(input: List<String>): Int {
        val monkeys = parseInput(input)
        val monkeysInspects = monkeys.associate { it.number to 0 }.toMutableMap()
        repeat(20) { _ ->
            monkeys.forEach { monkey ->
                monkeysInspects.computeIfPresent(monkey.number) { _, count -> count + monkey.items.size }
                monkey.items.map { monkey.operation(it) / 3 }.forEach { worryLevel ->
                    if (monkey.test(worryLevel)) monkeys[monkey.monkeyIfTrue].items += worryLevel
                    else monkeys[monkey.monkeyIfFalse].items += worryLevel
                }
                monkey.items.clear()
            }
        }
        return monkeysInspects.values.sortedDescending().take(2).product()
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseInput(input)
        val itemsPerRound = mutableListOf<Map<Int, Int>>()
//        var monkeysInspects = monkeys.associate { it.number to 0L }.toMutableMap()
        repeat(10_000) { round ->
            val itemsThisRound = mutableMapOf<Int, Int>()
            val monkeysInspects = monkeys.associate { it.number to 0L }.toMutableMap()
            if (round % 500 == 0) println("round $round")
            monkeys.forEach { monkey ->
                itemsThisRound[monkey.number] = monkey.items.size
                monkeysInspects.computeIfPresent(monkey.number) { _, count -> count + monkey.items.size }
                monkey.items.map(monkey.operation).forEach { worryLevel ->
                    if (monkey.test(worryLevel)) {
                        monkeys[monkey.monkeyIfTrue].items += worryLevel
                    } else monkeys[monkey.monkeyIfFalse].items += worryLevel
                }
                monkey.items.clear()
            }
            itemsPerRound += itemsThisRound
        }
        TODO()
//        return monkeysInspects.values.sortedDescending().take(2).product()
    }

    val input = readInput("Day11")
    println(part1(input))
//    println(part2(input))
}

private fun parseInput(input: List<String>): List<Monkey> = input
    .chunkedBy { it.isBlank() }
    .map { line ->
        val number = Regex("""\d+""").find(line[0])!!.value.toInt()
        val startingItems = line[1].substringAfter(": ").split(", ").map(String::toLong)
        val operation = line[2].substringAfter("new = ").split(" ").let { (first, second, third) ->
            val firstNumber = first.toLongOrNull()
            val secondNumber = third.toLongOrNull()
            val op: Long.(Long) -> Long = when (second) {
                "+" -> { b: Long -> this + b }
                "*" -> { b: Long -> this * b }
                else -> error("unsupported operation: $second")
            }
            { old: Long -> (firstNumber ?: old).op(secondNumber ?: old) }
        }
        val test: (Long) -> Boolean = { it % line[3].substringAfterLast(" ").toLong() == 0L }
        val monkeyIfTrue = line[4].substringAfterLast(" ").toInt()
        val monkeyIfFalse = line[5].substringAfterLast(" ").toInt()
        Monkey(
            number = number,
            items = startingItems.toMutableList(),
            operation = operation,
            test = test,
            monkeyIfTrue = monkeyIfTrue,
            monkeyIfFalse = monkeyIfFalse
        )
    }

private class Monkey(
    val number: Int,
    var items: MutableList<Long>,
    val operation: (Long) -> Long,
    val test: (Long) -> Boolean,
    val monkeyIfTrue: Int,
    val monkeyIfFalse: Int
)