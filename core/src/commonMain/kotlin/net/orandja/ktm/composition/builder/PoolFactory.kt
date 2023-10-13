package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.composition.builder.pool.MultiPool
import net.orandja.ktm.composition.parser.Parser

class PoolFactory(val parser: Parser) {

    val empty: MPool = MPool.Empty

    fun make(configuration: PoolBuilder.() -> Unit): MPool =
        PoolBuilder(parser).apply(configuration).build()

    fun delegate(configuration: DocumentBuilder.(String) -> MDocument?) = object : MPool {
        override fun get(name: String): MDocument? = configuration(DocumentBuilder(parser), name)
    }

    fun merge(vararg pools: MPool) = merge(pools.toList())
    fun merge(pools: List<MPool>) = MultiPool(pools)
}