package net.orandja.ktm.composition.builder

import net.orandja.ktm.composition.parser.CharStream

/** Implementation of [CharStream] for any kind of [CharSequence] */
class StringCharStream(val content: CharSequence) : CharStream {
    private var index = 0
    override fun read(): Char? = if (index >= content.length) null else {
        val result = content[index]
        index += 1
        result
    }
}