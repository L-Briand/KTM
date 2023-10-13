package net.orandja.ktm.composition.builder.pool

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import kotlin.jvm.JvmInline

@JvmInline
value class MultiPool(private val pools: List<MPool>) : MPool {
    override fun get(name: String): MDocument? {
        for (pool in pools) {
            pool[name]?.let { return it }
        }
        return null
    }
}