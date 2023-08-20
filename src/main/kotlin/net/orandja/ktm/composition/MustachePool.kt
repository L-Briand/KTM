package net.orandja.ktm.composition

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.composition.document.MDocumentBuilder
import net.orandja.ktm.composition.pool.PoolBuilder
import net.orandja.ktm.composition.pool.PoolDelegate
import net.orandja.ktm.composition.pool.PoolDelegateCached

object MustachePool {
    val empty: MPool = MPool.Empty
    inline fun build(parent: MPool? = null, configuration: PoolBuilder.() -> Unit) =
        PoolBuilder(parent).apply(configuration)

    fun delegate(parent: MPool? = null, configuration: MDocumentBuilder.(name: String) -> MDocument?) =
        PoolDelegate(parent, configuration)

    fun delegateCached(parent: MPool? = null, configuration: MDocumentBuilder.(name: String) -> MDocument?) =
        PoolDelegateCached(parent, configuration)

    fun merge(vararg pools: MPool) = object : MPool {
        override fun get(name: String): MDocument? {
            for (pool in pools) {
                pool[name]?.let { return it }
            }
            return null
        }
    }
}
