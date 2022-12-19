fun main() {

    fun part1(input: List<String>): Int {
        lateinit var start: GeoPoint
        lateinit var end: GeoPoint
        val geoMap = input.mapIndexed { lineIndex, line ->
            line.mapIndexed { charIndex, char ->
                when (char) {
                    'S' -> GeoPoint(y = lineIndex, x = charIndex, height = 'a'.code).also { start = it }
                    'E' -> GeoPoint(y = lineIndex, x = charIndex, height = 'z'.code).also { end = it }
                    else -> GeoPoint(y = lineIndex, x = charIndex, height = char.code)
                }
            }
        }
        return findShortestPath(geoMap = geoMap, start = start, endPredicate = end::equals)
    }

    fun part2(input: List<String>): Int {
        lateinit var start: GeoPoint

        val geoMap = input.mapIndexed { lineIndex, line ->
            line.mapIndexed { charIndex, char ->
                when (char) {
                    'S' -> GeoPoint(y = lineIndex, x = charIndex, height = 'a'.code)
                    'E' -> GeoPoint(y = lineIndex, x = charIndex, height = 'z'.code).also { start = it }
                    else -> GeoPoint(y = lineIndex, x = charIndex, height = char.code)
                }
            }
        }
        return findShortestPath(geoMap = geoMap, start = start, inverseHeightFilter = true) { it.height == 'a'.code }
    }

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

private data class GeoPoint(
    val x: Int,
    val y: Int,
    val height: Int,
)

private fun GeoPoint.getAvailableNeighbors(
    geoMap: List<List<GeoPoint>>,
    visitedPoints: Set<GeoPoint>,
    inverseHeightFilter: Boolean
) = sequenceOf(
    geoMap[(y - 1).coerceAtLeast(0)][x],
    geoMap[y][(x - 1).coerceAtLeast(0)],
    geoMap[y][(x + 1).coerceAtMost(geoMap[y].lastIndex)],
    geoMap[(y + 1).coerceAtMost(geoMap.lastIndex)][x]
).filter { p ->
    p !in visitedPoints && if (!inverseHeightFilter) (p.height <= this.height + 1) else (this.height <= p.height + 1)
}.toList()

private inline fun findShortestPath(
    geoMap: List<List<GeoPoint>>,
    start: GeoPoint,
    inverseHeightFilter: Boolean = false,
    endPredicate: (GeoPoint) -> Boolean,
): Int {
    val visitedNodes = mutableSetOf<GeoPoint>()
    val unvisitedNodes = geoMap.flatten().toMutableList()
    val pointsMap = mutableMapOf(start to 0)

    while (unvisitedNodes.isNotEmpty()) {
        val currentNode = unvisitedNodes.minBy { pointsMap[it] ?: Int.MAX_VALUE }
        unvisitedNodes -= currentNode
        visitedNodes += currentNode
        val currentLength = pointsMap[currentNode] ?: return Int.MAX_VALUE
        currentNode
            .getAvailableNeighbors(geoMap, visitedNodes, inverseHeightFilter)
            .forEach { point ->
                if (endPredicate(point)) {
                    return currentLength + 1
                }
                pointsMap.merge(point, currentLength + 1) { oldValue, newValue ->
                    oldValue.takeIf { it <= currentLength + 1 } ?: newValue
                }
            }
    }
    error("no proper path found")
}