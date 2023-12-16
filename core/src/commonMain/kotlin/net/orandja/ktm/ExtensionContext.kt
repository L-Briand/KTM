package net.orandja.ktm

import net.orandja.ktm.adapters.AnyKtmAdapter
import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.base.MContext

/**
 * Converts the receiver object [T] to a Mustache context by using the provided [KtmAdapter.Provider].
 *
 * @receiver the object to be converted to a Mustache context.
 * @param adapters provider containing the adapter for [T].
 * @return the Mustache context of the given object.
 */
inline fun <reified T> T.toMustacheContext(
    adapters: KtmAdapter.Provider = Ktm.adapters
) = adapters.contextOf(this)

/**
 * Converts the enum element [T] to a Mustache context by using the provided [KtmAdapter.Provider].
 *
 * @receiver the enum [T] to be converted to a Mustache context.
 * @param adapters provider containing the adapter for [T] if any
 * @return The Mustache context for the enum element provided by [adapters] or new Context created with [EnumMustacheContext].
 *
 * @see EnumMustacheContext
 * @see EnumKtmAdapter
 */
inline fun <reified T : Enum<T>> T.toMustacheContext(
    adapters: KtmAdapter.Provider = Ktm.adapters
): MContext {
    val enumAdapter = adapters.get<T>() ?: return this.EnumMustacheContext(adapters)
    val any: KtmAdapter<*> = AnyKtmAdapter
    if (any == enumAdapter) return this.EnumMustacheContext(adapters)
    return enumAdapter.toMustacheContext(this)
}