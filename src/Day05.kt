private val DIGIT_REGEX = Regex("""\D+""")

fun main() {
    fun part1(input: List<String>): String {
        val parsedInput = parseInput(input)
        val stacks = parsedInput.initialState.map(::ArrayDeque)
        parsedInput.instructions.forEach { instruction ->
            repeat(instruction.numberOfCrates) {
                stacks[instruction.to - 1].addLast(stacks[instruction.from - 1].removeLast())
            }
        }
        return stacks.map { it.last() }.joinToString("")
    }

    fun part2(input: List<String>): String {
        val parsedInput = parseInput(input)
        val stacks = parsedInput.initialState.map(::ArrayDeque)
        parsedInput.instructions.forEach { instruction ->
            stacks[instruction.to - 1] += stacks[instruction.from - 1].removeLast(instruction.numberOfCrates)
        }
        return stacks.map { it.last() }.joinToString("")
    }

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}


private fun parseInput(input: List<String>): ParsedInput {
    val (stacksInput, movesInput) = input.chunkedBy { it.isEmpty() }
    return ParsedInput(
        initialState = parseStacks(stacksInput),
        instructions = parseMoves(movesInput)
    )
}

private fun parseMoves(movesInput: List<String>) = movesInput.asSequence()
    .map { it.split(DIGIT_REGEX).filter(String::isNotBlank).map(String::toInt) }
    .map { MoveInstruction(it[0], it[1], it[2]) }
    .toList()

private fun parseStacks(stacksInput: List<String>): List<List<Char>> {
    val numberOfContainers = stacksInput.last().trimEnd().last().digitToInt()
    val stacks = List(numberOfContainers) { mutableListOf<Char>() }

    stacksInput
        .dropLast(1)
        .map { line -> line.chunked(4).map { it[1] } }
        .asReversed()
        .forEach { line ->
            line.forEachIndexed { index, c -> if (c.isLetter()) stacks[index] += c }
        }

    return stacks
}

private data class ParsedInput(
    val initialState: List<List<Char>>,
    val instructions: List<MoveInstruction>
)

private data class MoveInstruction(
    val numberOfCrates: Int,
    val from: Int,
    val to: Int
)