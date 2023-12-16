package net.orandja.ktm.adapters

import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MContext
import net.orandja.ktm.composition.builder.ContextMapBuilder
import kotlin.reflect.KType

/**
 * An interface that can be used to convert [T] to a [MContext].
 *
 * ```kotlin
 * val stringAsList = KtmAdapter<String> { _, value ->
 *     Ktm.ctx.list(value.split("-"))
 * }
 *
 * val context = stringAsList.toMustacheContext("Hello-World")
 * "({{.}})".render(context) // (Hello)(World)
 * ```
 */
fun interface KtmAdapter<T : Any?> {
    fun toMustacheContext(value: T): MContext = toMustacheContext(Ktm.adapters, value)
    fun toMustacheContext(adapters: Provider, value: T): MContext

    /**
     * An interface that provides instances of [KtmAdapter] given its type.
     */
    fun interface Provider {
        fun get(kType: KType): KtmAdapter<*>?
    }
}

/**
 * Extension of [KtmAdapter] where the superclass only need to configure the content of the context.
 *
 * ```kotlin
 * data class User(val name: String)
 *
 * val adapters = ktm.adapter.make {
 *     + KtmMapAdapter<User> { value -> "name" by value.name }
 * }
 *
 * val context = adapters.contextOf(User("John"))
 * // val context = User("John").toMustacheContext(adapters)
 * "Hello {{ name }}".render(context) // Hello John
 * ```
 * @see KtmAdapter
 */
fun interface KtmMapAdapter<T> : KtmAdapter<T> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: T): MContext =
        Ktm.ctx.make(adapters) { configure(value) }

    fun ContextMapBuilder.configure(value: T)
}
