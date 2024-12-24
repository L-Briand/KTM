package net.orandja.ktm.composition

/**
 * Transform the given string [str] to a sequence of string given the delimiter character.
 *
 * Example with the dot (`.`) delimiter:
 *
 * `"key.value"` -> `["key", "value"]`
 *
 * - All values are trimmed of whitespaces. `" a. b .c "` -> `["a", "b", "c"]`
 * - An single delimiter `"."`, multiple delimiter `".."` produces nothing. -> `[]`
 * - Leading delimiter(s) `".key"`, `"..key"` or trailing delimiter(s) `"key."`, `"key.."` are omitted -> `["key"]`
 * - Multi-delimiter between elements `"key..value"` are like single delimiter -> `["key", "value"]`
 *
 * @param str The dotted string to parse
 * @param delimiter The character to split
 *
 * @return A sequence of trimmed strings.
 */
internal fun tokenizeDelimitedString(str: CharSequence, delimiter: Char = '.'): Sequence<CharSequence> = sequence {
    if (str.length == 0) return@sequence

    var index = 0
    var wordStart: Int = -1
    var wordEnd = 0

    while (index < str.length) {
        if (str[index] == delimiter) {
            if (wordStart != -1) yield(str.subSequence(wordStart, wordEnd))
            wordStart = -1
        } else if (!str[index].isWhitespace()) {
            if (wordStart == -1) wordStart = index
            wordEnd = index + 1
        }
        index++
    }
    if (wordStart != -1) yield(str.subSequence(wordStart, wordEnd))
}

private class IterableDottedString(source: CharSequence) : Iterable<CharSequence> {
    // Caching the tokenized source for better performances.
    private val tokens = tokenizeDelimitedString(source).toList().toTypedArray()
    override fun iterator(): Iterator<CharSequence> = tokens.iterator()
}

internal fun CharSequence.iterableDottedString(): Iterable<CharSequence> = IterableDottedString(this)
