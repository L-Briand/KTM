package net.orandja.ktm.adapters

import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

/**
 * Implementation of `KtmAdapter.Provider` with underlying provider ([backing]) as backup for unknown types.
 */
class KtmAdapterProvider(
    private val backing: KtmAdapter.Provider?,
    private val adapters: Map<TypeKey, KtmAdapter<*>>
) : BaseKtmAdapterProvider() {
    override fun get(kType: TypeKey): KtmAdapter<*>? {
        adapters[kType]?.let { return it }
        if (kType.type.arguments.isNotEmpty()) {
            // Get a matching type with star projection on all type argument
            val match = adapters.entries.find {
                it.key.type.classifier == kType.type.classifier && it.key.type.arguments.all { it.variance == null }
            }
            if (match != null) return match.value
        }
        return backing?.get(kType) ?: super.get(kType)
    }
}