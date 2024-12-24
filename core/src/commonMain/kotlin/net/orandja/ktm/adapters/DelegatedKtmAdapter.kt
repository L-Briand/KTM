package net.orandja.ktm.adapters

import net.orandja.ktm.base.MContext
import net.orandja.ktm.getAsOrThrow

/**
 * A `KtmAdapter` implementation that delegates the conversion process to another adapter identified by a [TypeKey].
 *
 * @param T The type of the value to be converted to an [MContext].
 * @property delegatedKey The [TypeKey] used to identify the delegated adapter.
 */
class DelegatedKtmAdapter<T>(
    private val delegatedKey: TypeKey,
) : KtmAdapter<T> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: T): MContext =
        adapters.getAsOrThrow<T>(delegatedKey).toMustacheContext(value)

    override fun toString(): String = "DelegatedKtmAdapter($delegatedKey)"
}
