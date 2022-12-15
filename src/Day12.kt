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
        return findShortestPath(geoMap = geoMap, start = start, end = end)
    }

    fun part2(input: List<String>): Int {
        lateinit var end: GeoPoint
        val starts = mutableListOf<GeoPoint>()
        val geoMap = input.mapIndexed { lineIndex, line ->
            line.mapIndexed { charIndex, char ->
                when (char) {
                    'a', 'S' -> GeoPoint(y = lineIndex, x = charIndex, height = 'a'.code).also { starts += it }
                    'E' -> GeoPoint(y = lineIndex, x = charIndex, height = 'z'.code).also { end = it }
                    else -> GeoPoint(y = lineIndex, x = charIndex, height = char.code)
                }
            }
        }
        return starts.minOf { findShortestPath(geoMap = geoMap, start = it, end = end) }
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

private fun GeoPoint.getAvailableNeighbors(geoMap: List<List<GeoPoint>>, visitedPoints: Set<GeoPoint>) = sequenceOf(
    geoMap[(y - 1).coerceAtLeast(0)][x],
    geoMap[y][(x - 1).coerceAtLeast(0)],
    geoMap[y][(x + 1).coerceAtMost(geoMap[y].lastIndex)],
    geoMap[(y + 1).coerceAtMost(geoMap.lastIndex)][x]
).filter { it !in visitedPoints && it.height <= this.height + 1 }.toList()

private fun findShortestPath(geoMap: List<List<GeoPoint>>, start: GeoPoint, end: GeoPoint): Int {
    val visitedNodes = mutableSetOf<GeoPoint>()
    val unvisitedNodes = geoMap.flatten().toMutableList()
    val pointsMap = mutableMapOf(start to 0)

    while (unvisitedNodes.isNotEmpty()) {
        val currentNode = unvisitedNodes.minBy { pointsMap[it] ?: Int.MAX_VALUE }
        unvisitedNodes -= currentNode
        visitedNodes += currentNode
        val currentLength = pointsMap[currentNode] ?: return Int.MAX_VALUE
        currentNode.getAvailableNeighbors(geoMap, visitedNodes).forEach { point ->
            pointsMap.merge(point, currentLength + 1) { oldValue, newValue ->
                oldValue.takeIf { it <= currentLength + 1 } ?: newValue
            }
        }
        pointsMap[end]?.let { return it }
    }
    error("no value returned from sequence")
}