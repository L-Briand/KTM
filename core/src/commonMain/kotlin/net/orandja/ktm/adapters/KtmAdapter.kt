package net.orandja.ktm.adapters

import net.orandja.ktm.Ktm
import net.orandja.ktm.base.MContext
import kotlin.reflect.KType

/**
 * An interface that can be used to convert [T] types to a [MContext].
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
        fun get(kType: TypeKey): KtmAdapter<*>?
        fun get(kType: KType): KtmAdapter<*>? = get(TypeKey(kType))
    }
}
