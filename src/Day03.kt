fun main() {
    fun part1(input: List<String>) = input.asSequence()
        .map { it.chunked(it.length / 2, CharSequence::toSet) }
        .map { (first, second) -> (first intersect second).single() }
        .sumOf { it.itemPriority() }

    fun part2(input: List<String>) = input.asSequence()
        .chunked(3) { it.map(String::toSet) }
        .map { it.reduce { a, b -> a intersect b }.single() }
        .sumOf { it.itemPriority() }


    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

fun Char.itemPriority() = when {
    isLowerCase() -> code - 'a'.code + 1
    isUpperCase() -> code - 'A'.code + 27
    else -> error("invalid character")
}