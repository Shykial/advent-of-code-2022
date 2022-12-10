import kotlin.math.abs
import kotlin.properties.Delegates

private val STARTING_POINT: Point = Point()

fun main() {
    fun part1(input: List<String>): Int {
        val visitedByTail = mutableSetOf(STARTING_POINT)
        var head = STARTING_POINT
        var tail by Delegates.observable(STARTING_POINT) { _, _, newPoint -> visitedByTail += newPoint }

        parseMoves(input).forEach { (direction, length) ->
            repeat(length) {
                head = head movedTo direction
                tail = tail.alignedWithIfNeeded(head, direction)
            }
        }
        return visitedByTail.size
    }

    fun part2(input: List<String>): Int {
        val visitedByTail = mutableSetOf(STARTING_POINT)
        var head = STARTING_POINT
        var knots by Delegates.observable(List(9) { STARTING_POINT }) { _, _, newKnots ->
            visitedByTail += newKnots.last()
        }
        parseMoves(input).forEach { (direction, length) ->
            repeat(length) {
                head = head movedTo direction
                knots = buildList {
                    add(knots[0].alignedWithIfNeeded(head, direction))
                    knots.drop(1).forEach {
                        add(it.alignedWithIfNeeded(this.last(), direction))
                    }
                }
            }
        }
        return visitedByTail.size
    }

    val input = readInput("Day09")
    val testInput = readInput("Day09_test2")
    println(part1(input))
    println(part2(testInput))
}

private enum class Direction(val isForwardMultiplier: Int) {
    R(1), L(-1), U(1), D(-1)
}

private fun parseMoves(input: List<String>) = input
    .map { line -> line.cutExcluding(" ").let { Direction.valueOf(it.first) to it.second.toInt() } }

private data class Point(val x: Int = 0, val y: Int = 0) {
    infix fun movedTo(direction: Direction) = when (direction) {
        Direction.R -> copy(x = x + 1)
        Direction.L -> copy(x = x - 1)
        Direction.U -> copy(y = y + 1)
        Direction.D -> copy(y = y - 1)
    }
}

private fun Point.alignedWithIfNeeded(other: Point, direction: Direction) =
    this.takeIf { it isTouching other } ?: when (direction) {
        Direction.R, Direction.L -> this.copy(y = y.oneCloserTo(other.y), x = x + direction.isForwardMultiplier)
        Direction.U, Direction.D -> this.copy(x = x.oneCloserTo(other.x), y = y + direction.isForwardMultiplier)
    }

private infix fun Point.isTouching(other: Point) = abs(x - other.x) <= 1 && abs(y - other.y) <= 1

private fun Int.oneCloserTo(other: Int) = when {
    other > this -> this + 1
    other < this -> this - 1
    else -> this
}