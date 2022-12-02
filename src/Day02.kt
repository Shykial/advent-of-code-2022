fun main() {
    fun part1(input: List<String>): Int = input
        .map { it.cutExcluding(" ") }
        .sumOf { calculateScore(Shape.forLetter(it.first), Shape.forLetter(it.second)) }

    fun part2(input: List<String>) = input
        .map { it.cutExcluding(" ") }
        .map { Shape.forLetter(it.first) to ExpectedResult.forLetter(it.second) }
        .sumOf { calculateScore(it.first, it.first.getShapeForResult(it.second)) }


    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}


fun calculateScore(opponentShape: Shape, yourShape: Shape) = yourShape.score + yourShape.scoreAgainst(opponentShape)

enum class ExpectedResult(val letter: String) {
    LOSE("X"), DRAW("Y"), WIN("Z");

    companion object {
        private val lettersMap = values().associateBy { it.letter }

        fun forLetter(letter: String) = lettersMap[letter] ?: error("Invalid letter passed")
    }
}

enum class Shape(
    val letters: List<String>,
    val score: Int,
) {
    ROCK(listOf("A", "X"), 1),
    PAPER(listOf("B", "Y"), 2),
    SCISSORS(listOf("C", "Z"), 3);

    fun scoreAgainst(other: Shape) = when {
        this == other -> 3
        (this.score - other.score).mod(3) == 1 -> 6
        else -> 0
    }

    fun getShapeForResult(expectedResult: ExpectedResult) = when (expectedResult) {
        ExpectedResult.LOSE -> forScore((this.score - 1).takeUnless { it == 0 } ?: 3)
        ExpectedResult.DRAW -> this
        ExpectedResult.WIN -> forScore(this.score % 3 + 1)
    }

    companion object {
        private val lettersMap = values()
            .flatMap { it.letters.map { letter -> letter to it } }
            .toMap()

        private val scoresMap = values().associateBy { it.score }

        fun forLetter(letter: String) = lettersMap[letter] ?: error("Invalid letter passed")

        fun forScore(score: Int) = scoresMap[score] ?: error("Invalid score passed")
    }
}