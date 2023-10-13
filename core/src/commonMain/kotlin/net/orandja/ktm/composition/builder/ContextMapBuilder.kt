package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MContext

class ContextMapBuilder : ContextFactory() {

    private val backing: MutableMap<String, MContext> = mutableMapOf()
    fun build(): MContext = if (backing.isEmpty()) yes else ctxMap(backing)

    infix fun String.by(value: CharSequence?) {
        backing[this] = value?.let(::string) ?: no
    }

    infix fun String.by(value: Boolean) {
        backing[this] = if (value) yes else no
    }

    infix fun String.by(value: Iterable<String>?) {
        backing[this] = value?.let { list(it) } ?: no
    }

    infix fun String.by(value: Map<String, String>?) {
        backing[this] = value?.let { map(it) } ?: no
    }

    infix fun String.by(value: MContext?) {
        backing[this] = value ?: no
    }
}
