package net.orandja.ktm.composition

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.document.*
import java.io.File
import java.nio.file.Path

object MustacheDocument : MDocumentBuilder {
    override fun string(source: CharSequence): MDocument = CharSequenceDocument(source)
    override fun streamedFile(source: File): MDocument = FileStreamDocument(source)
    override fun streamedFile(source: String): MDocument = streamedFile(File(source))
    override fun streamedFile(source: Path): MDocument = streamedFile(source.toFile())
    override fun cached(source: StreamProvider): MDocumentCached = CachedStreamDocument(source)
    override fun cachedFile(source: File): MDocumentCached = cached(FileStreamProvider(source))
    override fun cachedFile(source: String): MDocumentCached = cachedFile(File(source))
    override fun cachedFile(source: Path): MDocumentCached = cachedFile(source.toFile())
    override fun cachedResource(classLoader: ClassLoader, name: String): MDocumentCached =
        cached(ResourceProvider(classLoader, name))
}