package net.orandja.ktm.composition.document

import net.orandja.ktm.base.MProvider
import net.orandja.ktm.base.MToken
import java.io.File
import java.io.InputStream

interface StreamProvider {
    fun inputStream(): InputStream
}

class FileStreamProvider(
    private val file: File,
) : StreamProvider {
    override fun inputStream(): InputStream = file.inputStream()
}

class ResourceProvider(
    private val loader: ClassLoader,
    private val resourceName: String,
) : StreamProvider {
    override fun inputStream(): InputStream = loader.getResourceAsStream(resourceName) ?: InputStream.nullInputStream()
}

internal val emptyProvider = object : MProvider {
    override fun subSequence(from: Long, to: Long): CharSequence = ""
    override fun close() = Unit
}
internal val emptySection = MToken.Section(null, 0L..0L, false, emptyList())
