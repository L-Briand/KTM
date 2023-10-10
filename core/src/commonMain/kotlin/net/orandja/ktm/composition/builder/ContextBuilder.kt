package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MContext.*
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.NodeContext
import net.orandja.ktm.base.context.GroupContext
import net.orandja.ktm.base.context.MultiContext
import net.orandja.ktm.base.context.MultiGroupContext
import net.orandja.ktm.base.context.ValueContext

class ContextBuilder constructor(
    private val backing: MutableMap<String, MContext>? = null,
): ContextFactory() {

    infix fun String.by(value: CharSequence?) {
        backing!![this] = value?.let(::value) ?: no
    }

    infix fun String.by(value: Boolean) {
        backing!![this] = if (value) yes else no
    }

    infix fun String.by(value: MContext?) {
        backing!![this] = value ?: no
    }

    infix fun String.by(value: Iterable<MContext>) {
        backing!![this] = Multi { value.iterator() }
    }

    fun build(): MContext = if (backing!!.isEmpty()) yes else group(backing)
}
