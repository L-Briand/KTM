package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.parser.CharStreamReader
import net.orandja.ktm.composition.parser.Parser

open class DocumentBuilder(val parser: Parser = Parser()) {

    private class StringReader(val content: String) : CharStreamReader {
        var index = 0
        override fun read(): Int =
            if (index < content.length) {
                val result = content[index].code
                index += 1
                result
            } else -1
    }

    fun string(source: String): MDocument = parser.parse(StringReader(source))
}