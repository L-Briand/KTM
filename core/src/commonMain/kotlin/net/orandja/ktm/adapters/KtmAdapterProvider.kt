package net.orandja.ktm.adapters

import kotlin.reflect.KType

/**
 * Implementation of `KtmAdapter.Provider` with underlying provider ([backing]) as backup for unknown types.
 */
class KtmAdapterProvider(
    private val backing: KtmAdapter.Provider?,
    private val adapters: Map<KType, KtmAdapter<*>>
) : BaseKtmAdapterProvider() {
    override fun get(kType: KType): KtmAdapter<*>? {
        return adapters[kType] ?: backing?.get(kType) ?: super.get(kType)
    }
}