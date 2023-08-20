package net.orandja.ktm.composition.pool

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.composition.MustacheDocument
import net.orandja.ktm.composition.document.MDocumentBuilder

class PoolBuilder constructor(
    private val parent: MPool?,
) : MPool, MDocumentBuilder by MustacheDocument {

    private val documents = mutableMapOf<String, MDocument>()

    override fun get(name: String): MDocument? = documents[name]

    infix fun String.by(value: CharSequence?) {
        value ?: return
        documents[this] = string(value)
    }

    infix fun String.by(value: MDocument?) {
        value ?: return
        documents[this] = value
    }

    fun parent(name: String): MDocument? = parent?.get(name)

    override fun toString(): String = "MustachePool(${documents.keys})"
}
