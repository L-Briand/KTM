package net.orandja.ktm.composition

internal const val TAG_DELIMITER = '.'

/**
 * Transform the given [name] to tokens which the parser and NodeContext can interpret.
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun tokenizeTagName(name: CharSequence): Array<String> {
    if (name.length == 1 && name[0] == TAG_DELIMITER) return emptyArray()

    var currentOffset = 0
    var nextIndex = name.indexOf(TAG_DELIMITER, currentOffset, true)
    if (nextIndex == -1) return arrayOf(name.toString())

    val result = mutableListOf<String>()
    do {
        if (currentOffset != nextIndex) result.add(name.substring(currentOffset, nextIndex))
        currentOffset = nextIndex + 1
        nextIndex = name.indexOf(TAG_DELIMITER, currentOffset, true)
    } while (nextIndex != -1)
    if (currentOffset != name.length) {
        result.add(name.substring(currentOffset))
    }
    return result.toTypedArray()
}

