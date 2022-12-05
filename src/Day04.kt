fun main() {
    fun part1(input: List<String>) = input.count { line ->
        line.cutExcluding(",")
            .map { it.parseIntRange() }
            .let { it.first hasFullOverlapWith it.second }
    }


    fun part2(input: List<String>) = input.count { line ->
        line.cutExcluding(",")
            .map { it.parseIntRange() }
            .let { it.first hasAnyOverlapWith it.second }
    }

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

private fun String.parseIntRange() = cutExcluding("-").let { it.first.toInt()..it.second.toInt() }

private infix fun <T> Iterable<T>.hasFullOverlapWith(other: Iterable<T>): Boolean {
    val firstSet = this.toSet()
    val secondSet = other.toSet()
    return firstSet.containsAll(secondSet) || secondSet.containsAll(firstSet)
}

private infix fun <T> Iterable<T>.hasAnyOverlapWith(other: Iterable<T>): Boolean =
    (this.toSet() intersect other.toSet()).isNotEmpty()