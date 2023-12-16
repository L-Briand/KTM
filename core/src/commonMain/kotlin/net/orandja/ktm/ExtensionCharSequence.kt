package net.orandja.ktm

import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.composition.builder.ContextMapBuilder


/**
 * Converts a [CharSequence] to a Mustache document.
 *
 * @receiver The [CharSequence] to convert.
 * @return The Mustache document.
 */
fun CharSequence.toMustacheDocument(): MDocument = Ktm.doc.string(this)

/**
 * Renders the given `CharSequence` like a [MDocument] with the provided [MContext], [MPool], and [writer] function.
 *
 * @receiver The `CharSequence` to render.
 * @param context The context to render the document with.
 * @param pool The pool of mustache documents used for partial search during rendering.
 * @param writer The function used to write the rendered output.
 */
fun CharSequence.render(
    context: MContext,
    pool: MPool = MPool.Empty,
) = toMustacheDocument().render(context, pool)


/**
 * Use the given `CharSequence` as a mustache document and renders it to a string.
 *
 * @receiver The `CharSequence` to render.
 * @param pool The [MPool] object that holds mustache documents. Default is an empty [MPool].
 * @param builder The context map builder function to build the [MContext] object for rendering.
 *                This function is used to define the values to be used in the mustache document.
 * @return The rendered string representation of the mustache document.
 */
inline fun CharSequence.render(
    pool: MPool = MPool.Empty,
    adapters: KtmAdapter.Provider = Ktm.adapters,
    builder: ContextMapBuilder.() -> Unit,
) = toMustacheDocument().render(pool, adapters, builder)

/**
 * Renders the given `CharSequence` like a [MDocument] with the provided [MContext], [MPool], and [writer] function.
 *
 * @receiver The `CharSequence` to render.
 * @param context The context to render the document with.
 * @param pool The pool of mustache documents used for partial search during rendering.
 * @param writer The function used to write the rendered output.
 */
fun CharSequence.streamRender(
    context: MContext,
    pool: MPool = MPool.Empty,
    writer: (CharSequence) -> Unit,
) = toMustacheDocument().streamRender(context, pool, writer)

