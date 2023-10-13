package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.isRegularFile

fun DocumentBuilder.reader(reader: Reader): MDocument {
    return parser.parse(ReaderCharStream(reader))
}

fun DocumentBuilder.inputStream(stream: InputStream): MDocument {
    return parser.parse(InputStreamCharStream(stream))
}

fun DocumentBuilder.file(file: File, charset: Charset = Charset.defaultCharset()): MDocument? {
    if (! file.exists()) return null
    return file.bufferedReader(charset).use { reader(it) }
}

fun DocumentBuilder.path(path: Path, charset: Charset = Charset.defaultCharset()): MDocument? {
    if (! path.isRegularFile()) return null
    return path.bufferedReader(charset).use { reader(it) }
}

fun DocumentBuilder.resource(name: String, charset: Charset = Charset.defaultCharset()): MDocument? {
    val stream = this::class.java.classLoader.getResourceAsStream(name) ?: return null
    return stream.bufferedReader(charset).use { reader(it) }
}
