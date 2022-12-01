fun main() {
    fun part1(input: String): Int = input
        .split("\n\n")
        .maxOf { it.lines().sumOf(String::toInt) }

    fun part2(input: String): Int = input
        .split("\n\n")
        .map { it.lines().sumOf(String::toInt) }
        .sortedDescending()
        .take(3)
        .sum()

    val input = readInputAsString("Day01")
    println(part1(input))
    println(part2(input))
}
