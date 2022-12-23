fun main() {
    fun part1(input: List<String>): Int {
        TODO()
    }

    fun part2(input: String): Int {
        TODO()
    }

    val testInput = readInput("Day13_test")
    println(parseInput(testInput))
}

private fun parseInput(input: List<String>) = input
    .chunkedBy { it.isBlank() }
    .map { (first, second) ->
        DataPacketPair(
            parsePacketLine(first),
            parsePacketLine(second)
        )
    }

private fun parsePacketLine(packetLine: String) = buildList<PacketUnit> {
    var currentListPacketNode: ListPacketNode? = null
    packetLine.substring(1, packetLine.lastIndex).forEach { char ->
        when (char) {
            in '0'..'9' -> {
                val intUnit = IntUnit(char.digitToInt())
                when (val listPacket = currentListPacketNode) {
                    null -> this += intUnit
                    else -> listPacket.value += intUnit
                }
            }

            '[' -> {
                val listPacketNode = ListPacketNode(upperNode = currentListPacketNode)
                if (currentListPacketNode == null) {
                    this += listPacketNode.value
                    currentListPacketNode = listPacketNode
                }
                currentListPacketNode!!.value += listPacketNode.value
                currentListPacketNode = listPacketNode
            }

            ']' -> {
                currentListPacketNode = currentListPacketNode?.upperNode
            }

            ',' -> Unit

        }
    }
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