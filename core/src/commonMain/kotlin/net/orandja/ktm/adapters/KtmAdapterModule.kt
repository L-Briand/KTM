package net.orandja.ktm.adapters

import net.orandja.ktm.Ktm

/**
 * Represent a set of [KtmAdapter] configured with [configure].
 * TODO: Documentation
 */
abstract class KtmAdapterModule {

    fun createAdapters(vararg modules: KtmAdapterModule) = Ktm.adapters.make {
        for (module in modules) with(module) { configure() }
        configure()
    }

    abstract fun KtmAdapterProviderBuilder.configure()
}