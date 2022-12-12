private val SIGNAL_REGEX = Regex("""(\w+)(?:\s(-?\d+))?""")

fun main() {
    fun part1(input: List<String>): Int {
        val searchedCycles = (20..220 step 40).toList().run(::ArrayDeque)
        return input
            .map { SIGNAL_REGEX.find(it)?.groups?.get(2)?.value }
            .fold(Triple(1, 1, 0)) { (cycle, xValue, sum), xMatch ->
                val (newCycle, newX) = xMatch
                    ?.let { number -> cycle + 2 to xValue + number.toInt() }
                    ?: (cycle + 1 to xValue)
                val newSum = if (newCycle > searchedCycles.first()) {
                    (sum + xValue * searchedCycles.removeFirst()).also { if (searchedCycles.isEmpty()) return it }
                } else sum
                Triple(newCycle, newX, newSum)
            }.third
    }

    fun part2(input: List<String>): String {
        val drawnLines = mutableListOf<String>()
        val lineBuilder = StringBuilder()
        fun appendCharCheckingOverflow(char: Char, cycle: Int) {
            lineBuilder.append(char)
            if (cycle % 40 == 0) {
                drawnLines += lineBuilder.toString()
                lineBuilder.clear()
            }
        }
        input.map { SIGNAL_REGEX.find(it)?.groups?.get(2)?.value }
            .fold(1 to 1) { (cycle, spritePosition), xMatch ->
                appendCharCheckingOverflow(crtChar(spritePosition, cycle), cycle)
                xMatch?.toInt()?.let { xValue ->
                    appendCharCheckingOverflow(crtChar(spritePosition, cycle + 1), cycle + 1)
                    cycle + 2 to spritePosition + xValue
                } ?: (cycle + 1 to spritePosition)
            }
        return drawnLines.joinToString("\n")
    }

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

private fun crtChar(spritePosition: Int, cycle: Int) =
    if ((cycle % 40).let { if (it == 0) 40 else it } in spritePosition..spritePosition + 2) '#' else '.'
