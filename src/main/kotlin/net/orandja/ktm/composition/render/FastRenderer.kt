package net.orandja.ktm.composition.render

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MPool
import net.orandja.ktm.base.MRenderer
import net.orandja.ktm.base.NodeContext

@Suppress("NOTHING_TO_INLINE")
class FastRenderer : MRenderer {

    override fun render(
        document: MDocument,
        node: NodeContext,
        pool: MPool,
        writer: (CharSequence) -> Unit,
    ) = when (document) {
        MDocument.Comment -> renderComment(writer)
        MDocument.NewLine -> renderNewLine(writer)
        is MDocument.Static -> renderStatic(document, writer)
        is MDocument.Partial -> renderPartial(document, node, pool, writer)
        is MDocument.Tag -> renderTag(document, node, writer)
        is MDocument.Section -> renderSection(document, node, pool, writer)
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
        node: NodeContext,
        pool: MPool,
        noinline writer: (CharSequence) -> Unit,
    ) {
        val partDocument = pool[document.name] ?: return
        this.render(partDocument, node, pool, writer)
    }

    private inline fun renderStatic(
        document: MDocument.Static,
        writer: (CharSequence) -> Unit,
    ) = writer(document.content)

    private inline fun renderNewLine(writer: (CharSequence) -> Unit) = writer("\n")
    private inline fun renderComment(writer: (CharSequence) -> Unit) = Unit

    // endregion
}