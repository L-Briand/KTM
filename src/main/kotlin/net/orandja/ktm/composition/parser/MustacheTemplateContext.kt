package net.orandja.ktm.composition.parser

import java.io.InputStream

@Suppress("NOTHING_TO_INLINE")
class MustacheTemplateContext(val reader: InputStream) {

    var searching: Int = MustacheTemplateParser.TOKEN_START // Token to find
    var idx: Long = 0 // Where the parser is in the mustache document
    var renderIdx: Long = 0 // Up to where we have rendered
    var tagType: Char? = null // What kind of marker we're in '>', '#', etc...

    var tkIdxCount: Int = 0 // increment every time we found next char of tkBegin or tkEnd
    var tkBegin: CharSequence = "{{"
    var tkEnd: CharSequence = "}}"
        set(value) {
            field = value
            updateTkEndSpecial()
        }

    // Search

    var peeked: Char = Char.MIN_VALUE

    inline fun next(): Char? {
        val peeked = peek()
        consume()
        return peeked
    }

    inline fun peek(): Char? {
        if (peeked != Char.MIN_VALUE) return peeked
        val peek = reader.read()
        if (peek == -1) return null
        peeked = peek.toChar()
        return peeked
    }

    inline fun consume() {
        if (peeked == Char.MIN_VALUE) return
        peeked = Char.MIN_VALUE
        idx++
    }

    var inTagBuffer = StringBuilder(64)

    inline fun getInTag(): CharSequence {
        val name = inTagBuffer.slice(0 ..< (inTagBuffer.length - tkEnd.length))
        inTagBuffer.clear()
        return name
    }

    // To handle special cases

    // Dictate if tag end is special and should be detected early
    // examples:
    //     if tkEnd is '}}', Sequence '}}}' is valid for un-escaped html closing tag.
    //     if tkEnd is '==', Sequence '===' is valid for delimiter change closing tag.
    var tkEndSpecial = true
    private inline fun updateTkEndSpecial() {
        val specialUnescape = tkEnd[0] == MustacheTemplateParser.TAG_UNESCAPED_1_END
        val specialDelimiter = tkEnd[0] == MustacheTemplateParser.TAG_DELIMITER_END
        if (!(specialUnescape xor specialDelimiter)) return

        tkEndSpecial = if (tkEnd.length < 2) true
        else tkEnd.subSequence(0, tkEnd.length - 1).match(tkEnd.subSequence(1, tkEnd.length))
    }

    private inline fun CharSequence.match(other: CharSequence): Boolean {
        if (length != other.length) return false
        if (isEmpty()) return true
        var idx = 0
        while (idx < length) {
            if (get(idx) != other[idx]) return false
            idx++
        }
        return true
    }
}
