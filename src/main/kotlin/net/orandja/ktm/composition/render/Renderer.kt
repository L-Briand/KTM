package net.orandja.ktm.composition.render

import net.orandja.ktm.base.*
import net.orandja.ktm.base.MDocument.NewLine

@Suppress("NOTHING_TO_INLINE")
open class Renderer : MRenderer {

    override fun render(
        document: MDocument,
        context: NodeContext,
        pool: MPool,
        writer: (CharSequence) -> Unit,
    ) = when (document) {
        MDocument.Empty -> renderComment(writer)
        is NewLine -> renderNewLine(document, writer)
        is MDocument.Static -> renderStatic(document, writer)
        is MDocument.Partial -> renderPartial(document, context, pool, writer)
        is MDocument.Tag -> renderTag(document, context, writer)
        is MDocument.Section -> renderSection(document, context, pool, writer)
    }

    // region section

    private inline fun renderSection(
        document: MDocument.Section,
        node: NodeContext,
        pool: MPool,
        noinline writer: (CharSequence) -> Unit,
    ) {
        val render = node.collect(document.name) { newNode ->
            if (document.inverted) {
                // inverted context
                val shouldRenderInverted = when (newNode.current) {
                    MContext.No -> true
                    is MContext.Multi -> !newNode.current.iterator(newNode).hasNext()
                    is MContext.Group, is MContext.Value, MContext.Yes -> false
                }
                if (shouldRenderInverted) {
                    renderSectionItems(document, pool, NodeContext(newNode.current, node), writer)
                }
            } else {
                // Not inverted context
                when (newNode.current) {
                    is MContext.Group -> {
                        val nextNode = if (node == newNode) newNode else NodeContext(newNode.current, node)
                        renderSectionItems(document, pool, nextNode, writer)
                    }

                    is MContext.Value, MContext.Yes -> renderSectionItems(document, pool, newNode, writer)
                    is MContext.Multi -> {
                        val iterator = newNode.current.iterator(newNode)
                        while (iterator.hasNext()) {
                            val nextNode = NodeContext(iterator.next(), NodeContext(newNode.current, node))
                            renderSectionItems(document, pool, nextNode, writer)
                        }
                    }

                    MContext.No -> Unit
                }
            }
            NodeContext.STOP
        }
        // Broken context in {{.}} should be considered falsey
        if (!render && document.inverted) {
            renderSectionItems(document, pool, node, writer)
        }
    }

    private inline fun renderSectionItems(
        document: MDocument.Section,
        pool: MPool,
        node: NodeContext,
        noinline writer: (CharSequence) -> Unit,
    ) {
        for (part in document.parts) render(part, node, pool, writer)
    }

    // endregion

    // region tag

    private inline fun renderTag(
        document: MDocument.Tag,
        node: NodeContext,
        noinline writer: (CharSequence) -> Unit,
    ) {
        if (document.escapeHtml) {
            value(document, node) { escape(it, writer) }
        } else {
            value(document, node, writer)
        }
    }

    private inline fun value(
        document: MDocument.Tag,
        node: NodeContext,
        noinline writer: (CharSequence) -> Unit,
    ) {
        node.collect(document.name) { newNode ->
            when (newNode.current) {
                is MContext.Multi -> {
                    for (context in newNode.current.iterator(newNode)) {
                        if (context is MContext.Value) {
                            writer(context.get(newNode))
                        }
                    }
                    NodeContext.STOP
                }

                is MContext.Value -> {
                    writer(newNode.current.get(newNode))
                    NodeContext.STOP
                }

                is MContext.Group, MContext.No, MContext.Yes -> {
                    NodeContext.CONTINUE
                }
            }
        }
    }

    private inline fun escape(cs: CharSequence, writer: (CharSequence) -> Unit) {
        var start = 0
        var idx = 0
        while (idx < cs.length) {
            when (cs[idx]) {
                '&' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&amp;")
                    start = idx + 1
                }

                '<' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&lt;")
                    start = idx + 1
                }

                '>' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&gt;")
                    start = idx + 1
                }

                '"' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&quot;")
                    start = idx + 1
                }

                '\'' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&#x27;")
                    start = idx + 1
                }

                '`' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&#x60;")
                    start = idx + 1
                }

                '=' -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer("&#x3D;")
                    start = idx + 1
                }
            }
            idx++
        }
        if (start < idx) writer(cs.subSequence(start, idx))
    }

    // endregion

    // region others

    private inline fun renderPartial(
        document: MDocument.Partial,
        context: NodeContext,
        pool: MPool,
        noinline writer: (CharSequence) -> Unit,
    ) {
        val partDocument = pool[document.name] ?: return
        val spaces = document.spaces
        if (spaces == null) {
            render(partDocument, context, pool, writer)
            return
        }

        writer(spaces)
        PartialRenderer(spaces).render(partDocument, context, pool, writer)
    }

    private inline fun renderStatic(
        document: MDocument.Static,
        writer: (CharSequence) -> Unit,
    ) {
        if (document.render) writer(document.content)
    }

    protected inline fun renderNewLine(
        document: NewLine,
        writer: (CharSequence) -> Unit,
    ) {
        if (document.render) writer(document.kind.representation)
    }

    private inline fun renderComment(writer: (CharSequence) -> Unit) = Unit

    // endregion
}