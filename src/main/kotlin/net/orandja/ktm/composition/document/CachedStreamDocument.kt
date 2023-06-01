package net.orandja.ktm.composition.document

import net.orandja.ktm.base.MProvider
import net.orandja.ktm.base.MToken

class CachedStreamDocument(
    private val streamProvider: StreamProvider,
) : MDocumentCached {

    private var current = CharSequenceDocument(streamProvider.inputStream().bufferedReader().use { it.readText() })

    override fun reload() {
        current = CharSequenceDocument(streamProvider.inputStream().bufferedReader().use { it.readText() })
    }

    override val provider: MProvider get() = current.provider
    override val tokens: MToken.Section get() = current.tokens
}
