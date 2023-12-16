package net.orandja.ktm

import net.orandja.ktm.Ktm.ctx
import net.orandja.ktm.Ktm.doc
import net.orandja.ktm.Ktm.parser
import net.orandja.ktm.Ktm.pool
import net.orandja.ktm.Ktm.renderer
import net.orandja.ktm.adapters.BaseKtmAdapterProvider
import net.orandja.ktm.adapters.KtmAdapterModule
import net.orandja.ktm.composition.builder.ContextFactory
import net.orandja.ktm.composition.builder.DocumentFactory
import net.orandja.ktm.composition.builder.PoolFactory
import net.orandja.ktm.composition.parser.Parser
import net.orandja.ktm.composition.render.Renderer
import kotlin.jvm.JvmStatic

/**
 * Ktm is a utility class that provides access to various components related to Mustache templating.
 *
 * @property parser
 * @property renderer .
 * @property ctx .
 * @property doc .
 * @property pool Factory for creating partials used in mustache templates.
 * @property adapters Provider for getting contexts given a type
 */
object Ktm {

    /**
     * The main Mustache document parser.
     */
    @JvmStatic
    val parser = Parser()

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
     */
    @JvmStatic
    var adapters = BaseKtmAdapterProvider()


    fun setDefaultAdapters(vararg modules: KtmAdapterModule) {
        adapters = adapters.make {
            for (module in modules) with(module) { configure() }
        }
    }
}
