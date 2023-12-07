package net.orandja.ktm.composition.parser

/**
 * This class represents the context for parsing a mustache document at level 0.
 * It contains information about the current state of parsing, such as the delimiters, tags, and search progress.
 *
 * @param charReader The character stream used to read the mustache document.
 * @param defaultStartDelimiter The default start tag delimiter. Default value is "{{".
 * @param defaultStopDelimiter The default stop tag delimiter. Default value is "}}".
 *
 * @property searchingDelim The current delimiter being searched. Can be [Delimiter.START] or [Delimiter.STOP].
 * @property tagType The type of tag marker we're currently in (e.g., '>', '#', etc.).
 * @property delimMatchIdx The number of characters matching between the delimiter and current reading.
 * @property startDelim The start tag delimiter currently used while parsing the document.
 * @property stopDelim The stop tag delimiter currently used while parsing the document.
 *
 * @property peeked The next character that has been peeked but not consumed yet.
 *
 * @property buffer The StringBuilder for storing the parsed content. It is used to build names, tag content, etc.
 *
 * @property isStopDelimSpecial Specifies whether the end delimiter is special and should be detected early.
 *
 * @constructor Creates a new ParserLevel0Context with the given parameters.
 */
@Suppress("NOTHING_TO_INLINE")
internal class ParserLevel0Context(
    val charReader: CharStream,
    defaultStartDelimiter: CharSequence = "{{",
    defaultStopDelimiter: CharSequence = "}}",
) {

    /** Current delimiter to find. Can be [Parser.Delimiter.START] or [Parser.Delimiter.STOP] */
    var searchingDelim: Delimiter = Delimiter.START

    /**  What kind of in tag marker we're in '>', '#', etc... */
    var tagType: Tag? = null

    /** The number of characters matching between the delimiter and current reading. */
    var delimMatchIdx: Int = 0

    /** Start tag delimiter currently used while parsing the document */
    var startDelim: CharSequence = defaultStartDelimiter

    /** Stop tag delimiter currently used while parsing the document */
    var stopDelim: CharSequence = defaultStopDelimiter
        set(value) {
            field = value
            updateIsStopDelimSpecial()
        }

    // Search

    var peeked: Char = Char.MIN_VALUE

    inline fun next(): Char? {
        val peeked = peek()
        consume()
        return peeked
    }

    inline fun consume() {
        if (peeked == Char.MIN_VALUE) return
        buffer.append(peeked)
        peeked = Char.MIN_VALUE
    }

    inline fun peek(): Char? {
        if (peeked != Char.MIN_VALUE) return peeked
        peeked = charReader.read() ?: return null
        return peeked
    }


    var buffer = StringBuilder(64)

    inline fun getBuffer(trimEndBy: Int): CharSequence {
        val name = buffer.subSequence(0, (buffer.length - trimEndBy))
        buffer.clear()
        return name
    }

    // To handle special cases

    /**
     * Dictate if end delimiter is special and should be detected early
     * - if stopDelim is `'}}'`, Sequence `'}}}'` is valid for un-escaped html closing tag.
     * - if stopDelim is `'=='`, Sequence `'==='` is valid for delimiter change closing tag.
     */
    var isStopDelimSpecial = true
    private inline fun updateIsStopDelimSpecial() {
        // TODO: Generalize with all Tag.close
        val specialUnescape = stopDelim[0] == Tag.UNESCAPED_1.close
        val specialDelimiter = stopDelim[0] == Tag.DELIMITER.close
        if (! (specialUnescape xor specialDelimiter)) return

        isStopDelimSpecial = if (stopDelim.length < 2) true
        else stopDelim.subSequence(0, stopDelim.length - 1).match(stopDelim.subSequence(1, stopDelim.length))
    }

    /** Check whenever two [CharSequence] are identical. */
    private inline fun CharSequence.match(other: CharSequence): Boolean {
        if (length != other.length) return false
        if (isEmpty()) return true
        var idx = 0
        while (idx < length) {
            if (get(idx) != other[idx]) return false
            idx ++
        }
        return true
    }
}