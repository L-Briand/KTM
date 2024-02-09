package net.orandja.ktm

import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.builder.ContextMapBuilder


/**
 * Renders the given MDocument using the provided MContext.
 *
 * @param context The object used for rendering.
 * @param adapters The KtmAdapter.Provider used to convert [context] to MContext.
 * @return The rendered MDocument as a String.
 */
inline fun <reified T> MDocument.render(
    context: T,
    adapters: KtmAdapter.Provider = Ktm.adapters,
) = render(adapters.contextOf(context))


/**
 * Renders the given MDocument using the provided MContext
 *
 * @param context The MContext used for rendering.
 * @return The rendered MDocument as a String.
 */
fun MDocument.render(context: MContext) = Ktm.renderer.renderToString(this, context)

/**
 * Renders the given mustache document to a string.
 *
 * @param builder The context map builder function to build the MContext object for rendering.
 *                This function is used to define the values to be used in the mustache document.
 * @return The rendered string representation of the mustache document.
 */
inline fun MDocument.render(
    adapters: KtmAdapter.Provider = Ktm.adapters,
    builder: ContextMapBuilder.() -> Unit,
) = Ktm.renderer.renderToString(this, Ktm.ctx.make(adapters) { builder() })

/**
 * Renders the given `MDocument` with the provided `MContext`, and `writer` function.
 *
 * @param context The context to render the document with.
 * @param writer The function used to write the rendered output.
 */
fun MDocument.streamRender(
    context: MContext,
    writer: (CharSequence) -> Unit,
) = Ktm.renderer.render(this, context, writer)