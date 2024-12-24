package net.orandja.ktm

import net.orandja.ktm.adapters.DefaultKtmAdapterProvider
import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.adapters.TypeKey
import net.orandja.ktm.adapters.typeKey
import net.orandja.ktm.base.MContext
import kotlin.reflect.KType
import kotlin.reflect.typeOf


/** Thrown by extension function [getOrThrow], [getAsOrThrow] when no adapter is found for a given type. */
class NoKtmAdapterException(type: KType) : IllegalArgumentException("Failed to get adapter for type: $type") {
    constructor(typeKey: TypeKey) : this(typeKey.type)
}

// region GETTERS

/**
 * Get the corresponding [KtmAdapter] of the given type [T] from the adapter provider.
 *
 * @receiver The [KtmAdapter.Provider] to search the [KtmAdapter] from.
 * @return a [KtmAdapter] for the specified type [T].
 */

@Suppress("UNCHECKED_CAST")
inline fun <reified T> KtmAdapter.Provider.get(): KtmAdapter<T>? = get(typeKey<T>()) as? KtmAdapter<T>

@Throws(NoKtmAdapterException::class)
inline fun <reified T> KtmAdapter.Provider.getOrThrow(): KtmAdapter<T> =
    get<T>() ?: throw NoKtmAdapterException(typeOf<T>())

/**
 * Same as [get] but with [TypeKey] as input instead.
 *
 * @receiver The [KtmAdapter.Provider] to search the [KtmAdapter] from.
 * @return KtmAdapter for the specified type [T] if found.
 */
@Suppress("UNCHECKED_CAST")
fun <T> KtmAdapter.Provider.getAs(key: TypeKey): KtmAdapter<T>? = get(key) as? KtmAdapter<T>

@Throws(NoKtmAdapterException::class)
fun <T> KtmAdapter.Provider.getAsOrThrow(key: TypeKey): KtmAdapter<T> = getAs(key) ?: throw NoKtmAdapterException(key)

// endregion

// region CONTEXT

/**
 * Returns a [MContext] given the value type [T].
 *
 * @param value the value for which to get the [MContext].
 * @return the [MContext] of the given value.
 * @throws NoKtmAdapterException if no provider is found for the given type [T].
 */
@Throws(NoKtmAdapterException::class)
inline fun <reified T> KtmAdapter.Provider.contextOf(value: T): MContext =
    getOrThrow<T>().toMustacheContext(this, value)

/**
 * Returns a [MContext] of the given callable value (`() -> T`).
 *
 * @param value the callable value for which to get the [MContext].
 * @return the [MContext] of the given callable value.
 */
inline fun <reified T : () -> R, reified R> KtmAdapter.Provider.contextOfCallable(value: T): MContext =
    Ktm.ctx.delegate { contextOf<R>(value.invoke()) }

/**
 * Returns a [MContext] of the given callable value (`(MContext.Node) -> T`).
 *
 * @param value the callable value for which to get the [MContext].
 * @return the [MContext] of the given callable value.
 */
inline fun <reified T : (MContext.Node) -> R, reified R> KtmAdapter.Provider.contextOfNodeCallable(value: T): MContext =
    Ktm.ctx.delegate { contextOf<R>(value.invoke(this)) }

// endregion