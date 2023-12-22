package net.orandja.ktm

import net.orandja.ktm.Ktm.ctx
import net.orandja.ktm.Ktm.doc
import net.orandja.ktm.Ktm.parser
import net.orandja.ktm.Ktm.pool
import net.orandja.ktm.Ktm.renderer
import net.orandja.ktm.composition.builder.ContextFactory
import net.orandja.ktm.composition.builder.DocumentBuilder
import net.orandja.ktm.composition.builder.PoolFactory
import net.orandja.ktm.composition.parser.Parser
import net.orandja.ktm.composition.render.Renderer
import kotlin.jvm.JvmStatic

/**
 * Ktm is a utility class that provides access to various components related to Mustache templating.
 *
 * @property parser The main Mustache document parser.
 * @property renderer Provides methods for rendering Mustache templates.
 * @property ctx Factory for creating Mustache context.
 * @property doc Builder for constructing Mustache documents.
 * @property pool Factory for creating partials used in mustache templates.
 */
object Ktm {
    @JvmStatic
    val parser = Parser()

    @JvmStatic
    val renderer = Renderer()

    @JvmStatic
    val ctx = ContextFactory()

    @JvmStatic
    val doc = DocumentBuilder(parser)

    @JvmStatic
    val pool = PoolFactory(parser)
}