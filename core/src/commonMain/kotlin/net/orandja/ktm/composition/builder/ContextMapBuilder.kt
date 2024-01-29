package net.orandja.ktm.composition.builder

import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.adapters.KtmMapAdapter
import net.orandja.ktm.base.MContext
import net.orandja.ktm.composition.builder.context.ContextMap
import net.orandja.ktm.contextOf
import net.orandja.ktm.getOrThrow
import kotlin.collections.MutableList
import kotlin.collections.lastOrNull
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
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

    fun build(): MContext = if (backingContexts.size == 1) backingContexts[0] else merge(backingContexts)

    infix fun String.by(value: CharSequence?) {
        getUpdatableContext().value[this] = string(value)
    }

    infix fun String.by(value: Boolean) {
        getUpdatableContext().value[this] = if (value) yes else no
    }

    infix fun String.by(value: MContext?) {
        getUpdatableContext().value[this] = value ?: no
    }

    inline infix fun <reified T> String.by(value: T) {
        by(if (null is T) no else contextOf(value))
    }

    fun associate(key: String, value: CharSequence?) {
        getUpdatableContext().value[key] = string(value)
    }

    fun associate(key: String, value: Boolean?) {
        getUpdatableContext().value[key] = bool(value)
    }

    fun associate(key: String, value: MContext) {
        getUpdatableContext().value[key] = value
    }

    fun addBackingContext(context: MContext.Map) {
        backingContexts.add(context)
    }

    inline fun <reified T> configureLike(value: T, adapter: KtmAdapter<T> = getOrThrow()) {
        if (adapter is KtmMapAdapter<T>) with(adapter) { configure(value) }
        else {
            val backingMap = adapter.toMustacheContext(value)
            if (backingMap is MContext.Map) addBackingContext(backingMap)
        }
    }

    // default

}
