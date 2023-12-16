package net.orandja.ktm.adapters

import kotlin.reflect.KType

/**
 * Implementation of `KtmAdapter.Provider` with underlying provider ([base]) as backup.
 */
class KtmAdapterProvider(
    private val base: KtmAdapter.Provider?,
    private val adapters: Map<KType, KtmAdapter<*>>
) : BaseKtmAdapterProvider() {
    override fun get(kType: KType): KtmAdapter<*>? {
        return adapters[kType] ?: base?.get(kType) ?: super.get(kType)
    }
}