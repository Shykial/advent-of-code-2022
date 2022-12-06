fun main() {
    fun part1(input: String) = markerPosition(input, 4)

    fun part2(input: String) = markerPosition(input, 14)

    val input = readInputAsString("Day06")
    println(part1(input))
    println(part2(input))
}

private fun markerPosition(dataStreamBuffer: String, markerLength: Int) = dataStreamBuffer
    .windowedSequence(markerLength)
    .indexOfFirst { it.toSet().size == markerLength } + markerLength