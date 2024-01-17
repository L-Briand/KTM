package net.orandja.ktm.composition.builder

import net.orandja.ktm.composition.parser.CharStream
import java.io.InputStream

class InputStreamCharStream(
    private val stream: InputStream,
) : CharStream {
    override fun read(): Char {
        val result = stream.read()
        return if (result == -1) Char.MAX_VALUE
        else result.toChar()
    }
}