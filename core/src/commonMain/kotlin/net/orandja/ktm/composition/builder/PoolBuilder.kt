package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.base.pool.MapPool
import net.orandja.ktm.base.pool.MultiPool
import net.orandja.ktm.composition.parser.Parser

class PoolBuilder(
    parser: Parser,
    private val backing: MutableMap<String, MDocument>? = null,
) : DocumentBuilder(parser) {

    fun get(name: String): MDocument? = backing!![name]

    infix fun String.by(value: CharSequence?) {
        value ?: return
        backing!![this] = string(value.toString())
    }

    infix fun String.by(value: MDocument?) {
        value ?: return
        backing!![this] = value
    }

    fun build(): MPool = if (backing!!.isEmpty()) MPool.Empty else MapPool(backing.toMap())
}
