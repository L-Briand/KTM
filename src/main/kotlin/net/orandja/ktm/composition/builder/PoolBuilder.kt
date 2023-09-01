package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.base.pool.MapPool

class PoolBuilder(
    private val backing: MutableMap<String, MDocument>? = null,
) : DocumentBuilder() {

    fun get(name: String): MDocument? = backing!![name]

    infix fun String.by(value: CharSequence?) {
        value ?: return
        backing!![this] = charSequence(value)
    }

    infix fun String.by(value: MDocument?) {
        value ?: return
        backing!![this] = value
    }

    private fun build(): MPool = if (backing!!.isEmpty()) empty else MapPool(backing.toMap())

    // Creation function

    fun make(configuration: PoolBuilder.() -> Unit): MPool =
        PoolBuilder(backing ?: mutableMapOf()).apply(configuration).build()

    val empty: MPool = MPool.Empty

    fun delegate(configuration: DocumentBuilder.(String) -> MDocument?) = object : MPool {
        override fun get(name: String): MDocument? = configuration(DocumentBuilder(), name)
    }

    fun merge(vararg pools: MPool) = object : MPool {
        override fun get(name: String): MDocument? {
            for (pool in pools) {
                pool[name]?.let { return it }
            }
            return null
        }
    }
}

