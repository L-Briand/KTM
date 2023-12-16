package net.orandja.ktm.composition.render

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.composition.NodeContext

/**
 * PartialRenderer is a class that extends the Renderer class
 * and provides additional functionality for rendering Mustache templates with partials.
 *
 * @property spaces The spaces to be added before rendering the template.
 */
class PartialRenderer(private val spaces: String) : Renderer() {
    override fun render(
        document: MDocument,
        context: NodeContext,
        pool: MPool,
        writer: (CharSequence) -> Unit,
    ) {
        super.render(document, context, pool, writer)
        if (document is MDocument.NewLine && !document.last) writer(spaces)
    }
}