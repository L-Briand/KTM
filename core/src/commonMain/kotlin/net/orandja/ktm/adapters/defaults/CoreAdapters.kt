package net.orandja.ktm.adapters.defaults

import net.orandja.ktm.adapters.KtmAdapter
import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.composition.builder.context.ContextDocument

/**
 * An adapter implementation for transforming an `MContext` instance into itself.
 *
 * This class serves as a no-op adapter, directly returning the provided `MContext` without any modifications.
 */
object MContextAdapter : KtmAdapter<MContext> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: MContext): MContext = value
}

object MDocumentAdapter : KtmAdapter<MDocument> {
    override fun toMustacheContext(adapters: KtmAdapter.Provider, value: MDocument): MContext = ContextDocument(value)
}