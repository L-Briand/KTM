package net.orandja.ktm.composition.parser

/**
 * Class representing the context for token parsing.
 *
 * @see TokenParser
 */
internal class TokenParserContext(
    val reader: CharStream,
    defaultStartDelimiter: CharSequence = "{{",
    defaultStopDelimiter: CharSequence = "}}",
) {
    /** Current delimiter to find. true: '{{', false '}}' */
    var searchingDelimStart: Boolean = true

    /** The number of characters matching between the delimiter and current reading. */
    var delimMatchIdx: Int = 0

    /** Start tag delimiter currently used while parsing the document */
    var startDelim: CharSequence = defaultStartDelimiter

    /** Stop tag delimiter currently used while parsing the document */
    var stopDelim: CharSequence = defaultStopDelimiter
        set(value) {
            field = value
            if (value[0] == Token.TAG[Token.TAG_ESCAPE_2 + 1] || value[0] == Token.TAG[Token.TAG_DELIMITER + 1]) {
                stopDelimiterBewareChar = value[0]
            }
        }

    var stopDelimiterBewareChar = '}'

    // Current char reader

    var peeked: Char = Char.MIN_VALUE

    inline fun next(): Char {
        val peeked = peek()
        consume()
        return peeked
    }

    inline fun peek(): Char {
        if (peeked != Char.MIN_VALUE) return peeked
        peeked = reader.read()
        return peeked
    }

    inline fun consume() {
        if (peeked == Char.MIN_VALUE) return
        readBuffer.append(peeked)
        peeked = Char.MIN_VALUE
    }

    var readBuffer = StringBuilder(128)

    inline fun getBuffer(trimEndBy: Int): CharSequence {
        val result = readBuffer.substring(0, (readBuffer.length - trimEndBy))
        readBuffer.clear()
        return result
    }

    inline fun drop(count: Int = 1) = readBuffer.setLength(readBuffer.length - count)

    var tagType: Int = -1

    var isWhiteContent: Boolean = true
}