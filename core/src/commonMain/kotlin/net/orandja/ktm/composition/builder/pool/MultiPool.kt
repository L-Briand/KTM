package net.orandja.ktm.composition.builder.pool

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import kotlin.jvm.JvmInline

/**
 * A MultiPool is an implementation of the MPool interface that holds multiple MPool instances.
 * It retrieves documents from the MPool instances by searching through each pool until it finds a matching document.
 *
 * @property pools The list of MPool instances to search through.
 */
@JvmInline
value class MultiPool(private val pools: List<MPool>) : MPool {
    override fun get(name: String): MDocument? {
        for (pool in pools) {
            pool[name]?.let { return it }
        }
        return null
    }
}