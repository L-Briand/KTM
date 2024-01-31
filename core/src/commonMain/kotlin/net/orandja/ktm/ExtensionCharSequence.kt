package net.orandja.ktm

import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.builder.ContextMapBuilder


/**
 * Converts a [CharSequence] to a Mustache document.
 *
 * @receiver The [CharSequence] to convert.
 * @return The Mustache document.
 */
fun CharSequence.toMustacheDocument(): MDocument = Ktm.doc.string(this)


/**
 * Renders the given `CharSequence` like a [MDocument] with the provided [context]
 * It will find the correct context in the default Ktm adapters
 */
inline fun <reified T> CharSequence.render(context: T, adapters: KtmAdapter.Provider = Ktm.adapters) =
    toMustacheDocument().render(adapters.contextOf(context))

/**
 * Renders the given `CharSequence` like a [MDocument] with the provided [MContext]
 *
 * @receiver The `CharSequence` to render.
 * @param context The context to render the document with.
 */
fun CharSequence.render(context: MContext) = toMustacheDocument().render(context)

inline fun CharSequence.render(
    adapters: KtmAdapter.Provider = Ktm.adapters,
    builder: ContextMapBuilder.() -> Unit,
) = toMustacheDocument().render(adapters, builder)

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
    writer: (CharSequence) -> Unit,
) = toMustacheDocument().streamRender(context, writer)

