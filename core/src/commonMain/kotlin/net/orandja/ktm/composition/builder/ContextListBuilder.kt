package net.orandja.ktm.composition.builder

import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.base.MContext
import net.orandja.ktm.contextOf

class ContextListBuilder(
    private val adapters: KtmAdapter.Provider
) : ContextFactory(), KtmAdapter.Provider by adapters {
    private val backing: MutableList<MContext> = mutableListOf()
    fun build(): MContext = if (backing.isEmpty()) yes else ctxList(backing)

    operator fun CharSequence?.unaryPlus() {
        backing += string(this)
    }

    operator fun Boolean.unaryPlus() {
        backing += if (this) yes else no
    }

    operator fun MContext?.unaryPlus() {
        backing += this ?: no
    }

    inline infix fun <reified T> T.unaryPlus(value: T) = contextOf(value)

}