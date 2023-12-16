package net.orandja.ktm.composition.builder

import net.orandja.ktm.base.MContext

class ContextListBuilder : ContextFactory() {
    private val backing: MutableList<MContext> = mutableListOf()
    fun build(): MContext = if (backing.isEmpty()) yes else ctxList(backing)

    operator fun CharSequence?.unaryPlus() {
        backing += string(this)
    }

    operator fun Boolean.unaryPlus() {
        backing += if (this) yes else no
    }

    operator fun Iterable<String?>?.unaryPlus() {
        backing += list(this)
    }

    operator fun Map<String, String?>?.unaryPlus() {
        backing += map(this)
    }

    operator fun MContext?.unaryPlus() {
        backing += this ?: no
    }

}