package net.orandja.ktm.composition.pool

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.composition.document.MDocumentBuilder

class PoolDelegateCached(
    parent: MPool?,
    delegate: MDocumentBuilder.(name: String) -> MDocument?
) : PoolDelegate(parent, delegate) {
    private val documents = mutableMapOf<String, MDocument>()
    override fun get(name: String): MDocument? =
        documents[name] ?: super.get(name)?.also { documents[name] = it }

    fun reset() = documents.clear()
}