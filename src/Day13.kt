fun main() {
    fun part1(input: List<String>) = parseInput(input)
        .foldIndexed(0) { index, acc, (first, second) ->
            if (arePacketsInTheRightOrder(first, second)) acc + index + 1 else acc
        }

    fun part2(input: String): Int {
        TODO()
    }

    val input = readInput("Day13")
    println(part1(input))
}

private fun arePacketsInTheRightOrder(first: List<PacketUnit>, second: List<PacketUnit>): Boolean {
    first.zip(second).forEach { (firstUnit, secondUnit) ->
        when (firstUnit) {
            is IntUnit -> when (secondUnit) {
                is IntUnit -> if (firstUnit.value != secondUnit.value) return firstUnit.value < secondUnit.value
                is ListUnit -> if (listOf(firstUnit) != secondUnit.values)
                    return arePacketsInTheRightOrder(listOf(firstUnit), secondUnit.values)
            }

            is ListUnit -> when (secondUnit) {
                is IntUnit -> if (firstUnit.values != listOf(secondUnit))
                    return arePacketsInTheRightOrder(firstUnit.values, listOf(secondUnit))

                is ListUnit -> if (firstUnit != secondUnit)
                    return arePacketsInTheRightOrder(firstUnit.values, secondUnit.values)
            }
        }
    }
    return first.size <= second.size
}

private fun parseInput(input: List<String>) = input
    .chunkedBy { it.isBlank() }
    .map { (first, second) ->
        DataPacketPair(
            parsePacketLine(first),
            parsePacketLine(second)
        )
    }

private fun parsePacketLine(packetLine: String): List<PacketUnit> {
    val rootNode = ListPacketNode()
    var currentListPacketNode = rootNode
    val digitBuilder = StringBuilder()

    packetLine.substring(1).forEach { char ->
        if (char in '0'..'9') digitBuilder.append(char)
        else {
            if (digitBuilder.isNotEmpty()) currentListPacketNode.value += IntUnit(digitBuilder.toString().toInt())
            digitBuilder.clear()
            when (char) {
                '[' -> {
                    ListPacketNode(upperNode = currentListPacketNode)
                    val listPacketNode = ListPacketNode(upperNode = currentListPacketNode)
                    currentListPacketNode.value += ListUnit(listPacketNode.value)
                    currentListPacketNode = listPacketNode
                }

                ']' -> if (currentListPacketNode != rootNode) currentListPacketNode = currentListPacketNode.upperNode!!
            }
        }
    }
    return rootNode.value
}

private data class ListPacketNode(
    val value: MutableList<PacketUnit> = mutableListOf(),
    val upperNode: ListPacketNode? = null
)

private sealed interface PacketUnit

@JvmInline
private value class IntUnit(val value: Int) : PacketUnit

@JvmInline
private value class ListUnit(val values: List<PacketUnit>) : PacketUnit

private data class DataPacketPair(
    val firstPacket: List<PacketUnit>,
    val secondPacket: List<PacketUnit>
)