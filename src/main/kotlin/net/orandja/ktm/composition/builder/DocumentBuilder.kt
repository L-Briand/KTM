package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.parser.Parser
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path

open class DocumentBuilder(val parser: Parser = Parser()) {
    fun string(source: String): MDocument = parser.parse(source)
    fun file(source: File, charset: Charset = Charsets.UTF_8): MDocument = parser.parse(source.reader(charset))
    fun path(source: Path, charset: Charset = Charsets.UTF_8) = file(source.toFile(), charset)
    fun path(source: String, charset: Charset = Charsets.UTF_8) = path(Path.of(source), charset)
}