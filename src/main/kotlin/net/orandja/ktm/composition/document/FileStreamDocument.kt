package net.orandja.ktm.composition.document

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MProvider
import net.orandja.ktm.base.MToken
import net.orandja.ktm.composition.MustacheParser
import java.io.File

class FileStreamDocument(val file: File) : MDocument {

    override val provider: MProvider
        get() = if (!file.exists()) emptyProvider else fileProvider

    private val fileProvider get() = object : MProvider {
        val stream = file.inputStream()
        val channel = stream.channel
        override fun subSequence(from: Long, to: Long): CharSequence {
            channel.position(from)
            return String(stream.readNBytes((to - from).toInt()))
        }

        override fun close() {
            stream.close()
        }
    }

    private var lastUpdated = Long.MIN_VALUE
    private var toks: MToken.Section? = null

    override val tokens: MToken.Section
        get() {
            if (!file.exists()) return emptySection
            if (lastUpdated != file.lastModified() || toks == null)
                file.inputStream().use { toks = MustacheParser.parse(it) }
            return toks!!
        }
}