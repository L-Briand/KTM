package net.orandja.ktm.composition.pool

import net.orandja.ktm.KTM
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.composition.document.MDocumentBuilder

open class PoolDelegate(
    private val parent: MPool?,
    private val delegate: MDocumentBuilder.(name: String) -> MDocument?,
) : MPool {
    override fun get(name: String): MDocument? = delegate.invoke(KTM.document, name) ?: parent?.get(name)
}
