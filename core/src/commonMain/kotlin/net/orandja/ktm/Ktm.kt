package net.orandja.ktm

import net.orandja.ktm.adapters.DefaultKtmAdapterProvider
import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.adapters.KtmAdapter.Module
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.builder.ContextFactory
import net.orandja.ktm.composition.parser.Parser
import net.orandja.ktm.composition.render.Renderer
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * Ktm is a utility class that provides access to various components.
 */
object Ktm {

    /**
     * Provides methods to parse text content into [MDocument].
     *
     * @see MDocument
     */
    @JvmStatic
    @get:JvmName("getParser")
    val parser = Parser

    /**
     * Provides methods for rendering Mustache templates [MDocument] with contextual elements [MContext].
     *
     * @see MContext
     */
    @JvmStatic
    @get:JvmName("getRenderer")
    val renderer = Renderer()

    /**
     * Factory for creating Mustache context [MContext].
     *
     * @see [MContext]
     */
    @JvmStatic
    @get:JvmName("getContextFactory")
    val ctx = ContextFactory()

    /**
     * Default [KtmAdapter.Provider] used to create [MContext] out of kotlin values
     *
     * @see DefaultKtmAdapterProvider
     */
    @JvmStatic
    @get:JvmName("getDefaultAdapters")
    var adapters = DefaultKtmAdapterProvider(null)
        private set

    /**
     * Sets the default adapters by combining the provided modules and optional builder configuration.
     *
     * @param modules A variable number of [KtmAdapter.Module] objects to be included in the default adapters set.
     * @param builder An optional lambda function to further configure the [DefaultKtmAdapterProvider.Builder].
     */
    fun setDefaultAdapters(vararg modules: Module, builder: DefaultKtmAdapterProvider.Builder.() -> Unit = {}) {
        adapters = adapters.create {
            for (module in modules) with(module) { configure() }
            builder()
        }
    }

    fun resetDefaultAdapters() {
        adapters = DefaultKtmAdapterProvider(null)
    }
}
