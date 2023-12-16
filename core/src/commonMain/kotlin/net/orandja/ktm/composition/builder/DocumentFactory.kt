package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.parser.CharStream
import net.orandja.ktm.composition.parser.Parser

open class DocumentFactory(val parser: Parser) {
    /** Create a document from a [CharStream] implementation */
    fun charStream(streamReader: CharStream) = parser.parse(streamReader)

    /** Create a document from any [CharSequence] */
    @Suppress("NOTHING_TO_INLINE")
    inline fun string(source: CharSequence): MDocument = charStream(StringCharStream(source))
}