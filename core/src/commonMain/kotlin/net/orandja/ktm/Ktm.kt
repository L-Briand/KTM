package net.orandja.ktm

import net.orandja.ktm.adapters.BaseKtmAdapterProvider
import net.orandja.ktm.adapters.KtmAdapterModule
import net.orandja.ktm.adapters.KtmAdapterProviderBuilder
import net.orandja.ktm.composition.builder.ContextFactory
import net.orandja.ktm.composition.builder.DocumentFactory
import net.orandja.ktm.composition.builder.PoolFactory
import net.orandja.ktm.composition.parser.Parser
import net.orandja.ktm.composition.render.Renderer
import kotlin.jvm.JvmStatic

/**
 * Ktm is a utility class that provides access to various components.
 */
object Ktm {

    /**
     * The main Mustache document parser.
     */
    @JvmStatic
    val parser = Parser

    /**
     * Provides methods for rendering Mustache templates.
     */
    @JvmStatic
    val renderer = Renderer()

    /**
     * Factory for creating Mustache context.
     */
    @JvmStatic
    val ctx = ContextFactory()

    /**
     * Factory for parsing Mustache documents.
     */
    @JvmStatic
    val doc = DocumentFactory(parser)

    /**
     * Factory for creating partials
     */
    @JvmStatic
    val pool = PoolFactory(parser)

    /**
     * Provider for getting Contexts adapter
     *
     * @see BaseKtmAdapterProvider
     */
    @JvmStatic
    var adapters = BaseKtmAdapterProvider()


    // TODO: Documentation
    fun setDefaultAdapters(vararg modules: KtmAdapterModule, builder: KtmAdapterProviderBuilder.() -> Unit = {}) {
        adapters = adapters.make {
            for (module in modules) with(module) { configure() }
            builder()
        }
    }
}
