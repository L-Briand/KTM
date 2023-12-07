package net.orandja.ktm

import net.orandja.ktm.Ktm.ctx
import net.orandja.ktm.Ktm.doc
import net.orandja.ktm.Ktm.parser
import net.orandja.ktm.Ktm.pool
import net.orandja.ktm.Ktm.renderer
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
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

/**
 * Converts a [CharSequence] to a Mustache document.
 *
 * @receiver The [CharSequence] to convert.
 * @return The Mustache document.
 */
fun CharSequence.toMustacheDocument() = Ktm.doc.string(this)

/**
 * Renders the given `MDocument` with the provided `MContext`, `MPool`, and `writer` function.
 *
 * @param context The context to render the document with.
 * @param pool The pool of mustache documents used for partial search during rendering.
 * @param writer The function used to write the rendered output.
 */
fun MDocument.render(
    context: MContext,
    pool: MPool = MPool.Empty,
    writer: (CharSequence) -> Unit,
) = Ktm.renderer.render(this, context, pool, writer)

/**
 * Renders the given MDocument using the provided MContext and MPool.
 *
 * @param context The MContext used for rendering.
 * @param pool The MPool used for partial search during rendering.
 * @return The rendered MDocument as a String.
 */
fun MDocument.render(
    context: MContext,
    pool: MPool = MPool.Empty,
) = Ktm.renderer.renderToString(this, context, pool)

/**
 * Renders the specified mustache document using the provided context and pool.
 *
 * @param name The name of the document to render.
 * @param context The MContext used for rendering.
 * @return The rendered document as a String.
 */
fun MPool.render(
    name: String,
    context: MContext,
) = get(name)?.render(context, this)

/**
 * Renders the associated mustache document with the provided context, using the provided writer function.
 *
 * @param tag The tag or name used to retrieve the associated mustache document from the pool.
 * @param context The context used to render the mustache document.
 * @param writer The function used to write the rendered output.
 */
fun MPool.render(
    tag: String,
    context: MContext,
    writer: (CharSequence) -> Unit,
) = get(tag)?.render(context, this, writer)
