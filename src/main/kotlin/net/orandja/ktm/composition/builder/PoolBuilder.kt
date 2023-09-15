package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.base.pool.MapPool
import net.orandja.ktm.base.pool.MultiPool

class PoolBuilder(
    private val backing: MutableMap<String, MDocument>? = null,
) : DocumentBuilder() {

    fun get(name: String): MDocument? = backing!![name]

    infix fun String.by(value: CharSequence?) {
        value ?: return
        backing!![this] = string(value.toString())
    }

    infix fun String.by(value: MDocument?) {
        value ?: return
        backing!![this] = value
    }

    fun build(): MPool = if (backing!!.isEmpty()) empty else MapPool(backing.toMap())

    // Creation function

    val empty: MPool = MPool.Empty

    inline fun make(configuration: PoolBuilder.() -> Unit): MPool =
        PoolBuilder(mutableMapOf()).apply(configuration).build()

    fun delegate(configuration: DocumentBuilder.(String) -> MDocument?) = object : MPool {
        override fun get(name: String): MDocument? = configuration(DocumentBuilder(), name)
    }

    fun merge(vararg pools: MPool) = merge(pools.toList())
    fun merge(pools: List<MPool>) = MultiPool(pools)
}

