import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

fun readInputAsString(name: String) = File("src", "$name.txt").readText().trimEnd()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

inline fun <T> List<T>.chunkedBy(separatorPredicate: (T) -> Boolean): List<List<T>> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()
    val outerList = mutableListOf<List<T>>()
    var currentInnerList = mutableListOf(iterator.next())

    for (element in iterator) {
        if (separatorPredicate(element)) {
            outerList += currentInnerList
            currentInnerList = mutableListOf()
        } else currentInnerList += element
    }
    if (currentInnerList.isNotEmpty()) outerList += currentInnerList
    return outerList
}

fun String.cutExcluding(delimiter: String) = substringBefore(delimiter) to substringAfter(delimiter)

inline fun <T, R> Pair<T, T>.map(transform: (T) -> R): Pair<R, R> = transform(first) to transform(second)

fun <E> ArrayDeque<E>.removeLast(n: Int): List<E> =
    this.slice(size - n..lastIndex)
        .also { sliceToBeRemoved ->
            repeat(sliceToBeRemoved.size) { this.removeLast() }
        }
