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
        adapters[kType]?.let { return it }
        if (kType.arguments.isNotEmpty()) {
            // Get a matching type with star projection on all type argument
            val match = adapters.entries.find {
                it.key.classifier == kType.classifier && it.key.arguments.all { it.variance == null }
            }
            if (match != null) return match.value
        }
        return backing?.get(kType) ?: super.get(kType)
    }
}