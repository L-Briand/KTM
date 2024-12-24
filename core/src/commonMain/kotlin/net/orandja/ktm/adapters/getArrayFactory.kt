package net.orandja.ktm.adapters

/**
 * This is only useful for java's arrays, which are not represented like you would think.
 *
 * For other platforms, the [Array] factory is in the [DefaultKtmAdapterProvider.defaultFactories].
 */
internal expect inline fun getArrayFactory(type: TypeKey): KtmAdapter.Factory?