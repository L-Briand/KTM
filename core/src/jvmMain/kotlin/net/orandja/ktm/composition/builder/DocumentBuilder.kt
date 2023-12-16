package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.isRegularFile

fun DocumentFactory.reader(reader: Reader): MDocument {
    return parser.parse(ReaderCharStream(reader))
}

fun DocumentFactory.inputStream(stream: InputStream): MDocument {
    return parser.parse(InputStreamCharStream(stream))
}

fun DocumentFactory.file(file: File, charset: Charset = Charset.defaultCharset()): MDocument? {
    if (!file.exists()) return null
    return file.bufferedReader(charset).use { reader(it) }
}

fun DocumentFactory.path(path: Path, charset: Charset = Charset.defaultCharset()): MDocument? {
    if (!path.isRegularFile()) return null
    return path.bufferedReader(charset).use { reader(it) }
}

fun DocumentFactory.resource(
    name: String,
    classLoader: ClassLoader = this::class.java.classLoader,
    charset: Charset = Charset.defaultCharset(),
): MDocument? {
    val stream = classLoader.getResourceAsStream(name) ?: return null
    return stream.bufferedReader(charset).use { reader(it) }
}
