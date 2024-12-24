package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.parser.Parser
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.isRegularFile

fun Parser.reader(reader: Reader): MDocument {
    return parse(ReaderCharStream(reader))
}

fun Parser.inputStream(stream: InputStream): MDocument {
    return parse(InputStreamCharStream(stream))
}

fun Parser.file(file: File, charset: Charset = Charset.defaultCharset()): MDocument? {
    if (!file.exists() && !file.isFile) return null
    return file.bufferedReader(charset).use { reader(it) }
}

fun Parser.path(path: Path, charset: Charset = Charset.defaultCharset()): MDocument? {
    if (!path.isRegularFile()) return null
    return path.bufferedReader(charset).use { reader(it) }
}

fun Parser.resource(
    name: String,
    classLoader: ClassLoader = this::class.java.classLoader,
    charset: Charset = Charset.defaultCharset(),
): MDocument? {
    val stream = classLoader.getResourceAsStream(name) ?: return null
    return stream.bufferedReader(charset).use { reader(it) }
}
