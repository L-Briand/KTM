package net.orandja.ktm.adapters

import net.orandja.ktm.Ktm

abstract class KtmAdapterModule {

    fun createAdapters(vararg modules: KtmAdapterModule) = Ktm.adapters.make {
        configure()
        for (module in modules) with(module) { configure() }
    }

    abstract fun KtmAdapterProviderBuilder.configure()
}