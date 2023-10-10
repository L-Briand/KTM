package net.orandja.ktm.composition.builder

import net.orandja.ktm.composition.parser.CharStream
import java.io.Reader

class ReaderCharStream(
    private val reader: Reader
) : CharStream {
    override fun read(): Char? {
        val result = reader.read()
        return if (result == -1) null
        else result.toChar()
    }
}