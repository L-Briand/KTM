package net.orandja.ktm.composition.builder

import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.builder.context.ContextMap
import net.orandja.ktm.contextOf
import net.orandja.ktm.getOrThrow
import kotlin.collections.set

/**
 * Builder class for creating a contextual map used in Mustache rendering.
 *
 * @property adapters The provider of KtmAdapter instances.
 */
class ContextMapBuilder(
    private val adapters: KtmAdapter.Provider
) : ContextFactory(), KtmAdapter.Provider by adapters {

    private val backingContexts: MutableList<MContext.Map> = mutableListOf()

    private fun getUpdatableContext(): ContextMap {
        var lastCtx = backingContexts.lastOrNull()
        if (lastCtx is ContextMap) return lastCtx
        lastCtx = ContextMap(mutableMapOf())
        backingContexts.add(lastCtx)
        return lastCtx
    }

    fun build(): MContext = when (backingContexts.size) {
        0 -> MContext.Yes
        1 -> backingContexts[0]
        else -> merge(backingContexts)
    }


    infix fun String.by(value: CharSequence?) {
        getUpdatableContext().value[this] = value(value)
    }

    infix fun String.by(value: Boolean) {
        getUpdatableContext().value[this] = if (value) yes else no
    }

    infix fun String.by(value: MDocument?) {
        getUpdatableContext().value[this] = value?.let(::ctxDocument) ?: no
    }

    infix fun String.by(value: MContext?) {
        getUpdatableContext().value[this] = value ?: no
    }

    inline infix fun <reified T> String.by(value: T) {
        by(if (null is T) no else contextOf(value))
    }

    fun associate(key: String, value: CharSequence?) {
        key by value
    }

    fun associate(key: String, value: Boolean?) {
        key by value
    }

    fun associate(key: String, value: MDocument?) {
        key by value
    }

    fun associate(key: String, value: MContext) {
        key by value
    }

    fun like(context: MContext) {
        context.accept(Unit, backingContextAdder)
    }

    inline fun <reified T> like(value: T, adapter: KtmAdapter<T> = getOrThrow()) {
        like(adapter.toMustacheContext(this, value))
    }

    private val backingContextAdder = object : MContext.Visitor.Default<Unit, Unit>(Unit) {
        override fun map(data: Unit, map: MContext.Map) {
            if (map is ContextMap) {
                val updatableContext = getUpdatableContext()
                for (entry in map.value) {
                    updatableContext.value[entry.key] = entry.value
                }
            } else {
                backingContexts.add(map)
            }
        }
    }

}
