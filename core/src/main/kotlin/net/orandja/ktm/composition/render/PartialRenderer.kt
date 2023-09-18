package net.orandja.ktm.composition.render

import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.base.NodeContext

class PartialRenderer(private val spaces: String) : Renderer() {
    override fun render(
        document: MDocument,
        context: NodeContext,
        pool: MPool,
        writer: (CharSequence) -> Unit,
    ) {
        if (document is MDocument.NewLine) {
            renderNewLine(document, writer)
            if (!document.last) writer(spaces)
        } else super.render(document, context, pool, writer)
    }
}