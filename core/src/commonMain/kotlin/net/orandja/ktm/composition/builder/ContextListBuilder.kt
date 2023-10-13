package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MContext

class ContextListBuilder : ContextFactory() {
    private val backing: MutableList<MContext> = mutableListOf()
    fun build(): MContext = if (backing.isEmpty()) yes else ctxList(backing)

    operator fun CharSequence?.unaryPlus() {
        backing += this?.let(::string) ?: no
    }

    operator fun Boolean.unaryPlus() {
        backing += if (this) yes else no
    }

    operator fun Iterable<String>?.unaryPlus() {
        backing += this?.let(::list) ?: no
    }

    operator fun Map<String, String>?.unaryPlus() {
        backing += this?.let(::map) ?: no
    }

    operator fun MContext?.unaryPlus() {
        backing += this ?: no
    }

}