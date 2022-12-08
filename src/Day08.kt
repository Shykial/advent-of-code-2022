fun main() {
    fun part1(input: List<String>): Int {
        val mapped = input.mapInner(Char::digitToInt)
        return mapped.borderSize + mapped
            .subList(1, input.lastIndex)
            .sumOfIndexed { lineIndex, line ->
                line.subList(1, line.lastIndex).countIndexed { digitIndex, d ->
                    d > minOf(
                        line.subList(digitIndex + 2, line.size).max(),
                        line.subList(0, digitIndex + 1).max(),
                        mapped.subList(0, lineIndex + 1).maxOf { it[digitIndex + 1] },
                        mapped.subList(lineIndex + 2, mapped.size).maxOf { it[digitIndex + 1] }
                    )
                }
            }
    }

    fun part2(input: List<String>): Int {
        val mapped = input.mapInner(Char::digitToInt)
        return mapped
            .subList(1, input.lastIndex)
            .flatMapIndexed { lineIndex, line ->
                line.subList(1, line.lastIndex).mapIndexed { digitIndex: Int, d: Int ->
                    productOf(
                        line.subList(digitIndex + 2, line.size).viewingDistanceFrom(d),
                        line.subList(0, digitIndex + 1).asReversed().viewingDistanceFrom(d),
                        mapped.subList(0, lineIndex + 1).asReversed().map { it[digitIndex + 1] }.viewingDistanceFrom(d),
                        mapped.subList(lineIndex + 2, mapped.size).map { it[digitIndex + 1] }.viewingDistanceFrom(d)
                    )
                }
            }.max()
    }

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

private val <T> Collection<Collection<T>>.borderSize: Int
    get() = first().size * 2 + 2 * (size - 2)

private fun List<Int>.viewingDistanceFrom(digit: Int) =
    1 + (indexOfFirst { it >= digit }.takeIf { it != -1 } ?: lastIndex)

