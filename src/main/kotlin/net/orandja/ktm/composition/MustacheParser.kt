package net.orandja.ktm.composition

import net.orandja.ktm.base.MToken
import net.orandja.ktm.composition.parser.MustacheTemplateContext
import net.orandja.ktm.composition.parser.MustacheTemplateParser
import java.io.InputStream

/** Hold all methods to parse a mustache document. */
object MustacheParser {

    /** Default to parse file */
    fun parse(source: CharSequence): MToken.Section = parse(CharSequenceInputStream(source))
    fun parse(source: String): MToken.Section = parse(source.byteInputStream())
    fun parse(source: InputStream): MToken.Section = MustacheTemplateParser.parse(MustacheTemplateContext(source))

    // MustacheTemplateParser do not use other methods than read
    private class CharSequenceInputStream(private val source: CharSequence) : InputStream() {
        private var idx = 0
        override fun read(): Int {
            if (idx >= source.length) return -1
            val char = source[idx]
            idx++
            return char.code
        }
    }
}
