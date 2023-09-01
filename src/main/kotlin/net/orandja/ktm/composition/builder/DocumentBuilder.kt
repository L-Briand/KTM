package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import java.io.File
import java.nio.file.Path

open class DocumentBuilder {
    fun charSequence(source: CharSequence): MDocument = CharSequenceDocument(source)
    fun string(source: String): MDocument = CharSequenceDocument(source)
    fun file(source: File): MDocument = charSequence(source.inputStream().bufferedReader().readText())
    fun path(source: Path) = file(source.toFile())
    fun path(source: String) = path(Path.of(source))
}