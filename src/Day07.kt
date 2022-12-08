fun main() {
    fun part1(input: List<String>) = processInput(input).second.asSequence()
        .map { it.calculateTotalSize() }
        .filter { it < 100_000 }
        .sum()


    fun part2(input: List<String>): Long {
        val totalAvailableSpace = 70_000_000L
        val unusedSpaceAtLeast = 30_000_000
        val (rootDirectory, visitedDirectories) = processInput(input)
        val rootDirectorySize = rootDirectory.calculateTotalSize()
        return visitedDirectories
            .map { it.calculateTotalSize() }
            .filter { totalAvailableSpace - rootDirectorySize + it > unusedSpaceAtLeast }
            .min()
    }

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

private fun processInput(input: List<String>): Pair<RootDirectory, Set<DirectoryEntry>> {
    val rootDirectory = RootDirectory()
    var currentDirectory: DirectoryEntry = rootDirectory
    val visitedDirectories = mutableSetOf(currentDirectory)
    input.asSequence().map(::parseLine).forEach { output ->
        when (output) {
            is Command.CD -> {
                currentDirectory = currentDirectory.changeDirectory(output.location, rootDirectory)
                if (currentDirectory.children.isEmpty()) visitedDirectories += currentDirectory
            }

            Command.LS, is SystemEntryOutput.Directory -> Unit
            is SystemEntryOutput.File -> currentDirectory.children += FileEntry(
                name = output.name,
                parent = currentDirectory,
                size = output.size
            )
        }
    }
    return rootDirectory to visitedDirectories
}

private fun parseLine(line: String): Output {
    val split = line.split(" ")
    return when (split[0]) {
        "$" -> when (val command = split[1]) {
            "ls" -> Command.LS
            "cd" -> Command.CD(location = split[2])
            else -> error("Unsupported command $command")
        }

        else -> when (split[0]) {
            "dir" -> SystemEntryOutput.Directory(name = split[1])
            else -> SystemEntryOutput.File(name = split[1], size = split[0].toLong())
        }
    }
}


private fun DirectoryEntry.changeDirectory(outputDirectory: String, rootDirectory: RootDirectory): DirectoryEntry {
    val parent = parent ?: this
    return when (outputDirectory) {
        "/" -> rootDirectory
        ".." -> parent
        else -> DirectoryEntry(outputDirectory, this).also { this.children += it }
    }
}

private sealed interface Output

private sealed interface Command : Output {
    object LS : Command
    class CD(val location: String) : Command
}

private sealed interface SystemEntryOutput : Output {
    val name: String

    class File(override val name: String, val size: Long) : SystemEntryOutput
    class Directory(override val name: String) : SystemEntryOutput
}

private sealed class SystemEntry(parentPath: String) {
    abstract val name: String
    abstract val parent: DirectoryEntry?

    val fullPath by lazy { "$parentPath/$name" }

    abstract fun calculateTotalSize(): Long

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return (other as SystemEntry).fullPath == fullPath
    }

    final override fun hashCode(): Int = fullPath.hashCode()
}

private class FileEntry(
    override val name: String,
    override val parent: DirectoryEntry?,
    val size: Long,
    parentPath: String = parent?.fullPath.orEmpty()
) : SystemEntry(parentPath) {
    override fun calculateTotalSize() = size
}

private open class DirectoryEntry(
    override val name: String,
    override val parent: DirectoryEntry?,
    parentPath: String = parent?.fullPath.orEmpty()
) : SystemEntry(parentPath) {
    val children: MutableList<SystemEntry> = mutableListOf()
    override fun calculateTotalSize() = children.sumOf { it.calculateTotalSize() }
}

private class RootDirectory : DirectoryEntry("/", null, "")
