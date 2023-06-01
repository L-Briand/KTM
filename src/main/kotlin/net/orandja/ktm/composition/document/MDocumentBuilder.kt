package net.orandja.ktm.composition.document

import net.orandja.ktm.base.MDocument
import java.io.File
import java.nio.file.Path

interface MDocumentBuilder {
    fun string(source: CharSequence): MDocument
    fun streamedFile(source: File): MDocument
    fun streamedFile(source: String): MDocument
    fun streamedFile(source: Path): MDocument
    fun cached(source: StreamProvider): MDocumentCached
    fun cachedFile(source: File): MDocumentCached
    fun cachedFile(source: String): MDocumentCached
    fun cachedFile(source: Path): MDocumentCached
    fun cachedResource(classLoader: ClassLoader, name: String): MDocumentCached
}
