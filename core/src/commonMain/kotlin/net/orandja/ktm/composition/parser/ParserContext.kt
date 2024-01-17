package net.orandja.ktm.composition.parser

import net.orandja.ktm.base.MDocument

@Suppress("NOTHING_TO_INLINE")
internal class ParserContext(
    val tokens: Iterator<Token>
) {
    var isStantaloneLine = true
    var containPartial = false
    var partialPadding = StringBuilder()

    val nodes = mutableListOf(MDocument.Section(emptyArray(), false))

    var peeked = ArrayList<Token>(5)

    inline fun peekNext(): Token? {
        if (!tokens.hasNext()) return null
        peeked.add(tokens.next())
        return peeked[peeked.size - 1]
    }

    inline fun drop() {
        peeked.clear()
    }

}