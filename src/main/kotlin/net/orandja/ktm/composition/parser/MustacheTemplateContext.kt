package net.orandja.ktm.composition.parser

import java.io.InputStream

@Suppress("NOTHING_TO_INLINE")
class MustacheTemplateContext(val reader: InputStream) {

    var searching: Int = MustacheTemplateParser.TOKEN_START
    var idx: Long = 0
    var renderIdx: Long = 0
    var tagType: Char? = null

    var tokIdx: Int = 0
    var tokStart: CharSequence = "{{"
    var tokStop: CharSequence = "}}"
        set(value) {
            field = value
            updateTeSpecial()
        }

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

    var inTagBuffer = StringBuilder(128)

    inline fun getInTag(): CharSequence {
        val name = inTagBuffer.slice(0 until inTagBuffer.length - tokStop.length)
        inTagBuffer.clear()
        return name
    }

    // Dictate if tag end is special and should be detected early
    // examples:
    //     endTag='}}', Sequence : '}}}'
    //     endTag='==', Sequence : '==='
    var teSpecial = true
    private inline fun updateTeSpecial() {
        if (!((tokStop[0] == MustacheTemplateParser.TAG_UNESCAPED_1_END) xor (tokStop[0] == MustacheTemplateParser.TAG_DELIMITER_END))) return
        teSpecial = if (tokStop.length < 2) {
            true
        } else {
            tokStop.subSequence(0, tokStop.length - 1).match(tokStop.subSequence(1, tokStop.length))
        }
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
