package net.orandja.ktm.base.pool

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool

class MultiPool(val pools: List<MPool>) : MPool {
    override fun get(name: String): MDocument? {
        for (pool in pools) {
            pool[name]?.let { return it }
        }
        return null
    }
}