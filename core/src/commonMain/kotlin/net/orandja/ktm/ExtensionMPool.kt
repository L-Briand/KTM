package net.orandja.ktm

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MPool
import net.orandja.ktm.composition.builder.ContextMapBuilder

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
 * Renders the specified mustache document using the provided context builder and pool.
 *
 * @param name The name of the document to render.
 * @param builder The builder function used to configure the context map.
 * @return The rendered document as a string.
 */
inline fun MPool.render(
    name: String,
    builder: ContextMapBuilder.() -> Unit,
) = get(name)?.render(Ktm.ctx.make { builder() }, this)

/**
 * Renders the associated mustache document with the provided context, using the provided writer function.
 *
 * @param tag The tag or name used to retrieve the associated mustache document from the pool.
 * @param context The context used to render the mustache document.
 * @param writer The function used to write the rendered output.
 */
fun MPool.streamRender(
    tag: String,
    context: MContext,
    writer: (CharSequence) -> Unit,
) = get(tag)?.streamRender(context, this, writer)
