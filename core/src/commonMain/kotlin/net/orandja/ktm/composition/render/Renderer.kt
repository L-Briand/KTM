package net.orandja.ktm.composition.render

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
import net.orandja.ktm.base.MDocument.NewLine
import net.orandja.ktm.base.MPool
import net.orandja.ktm.composition.NodeContext

/**
 * Renderer is a class that provides methods for rendering Mustache templates.
 *
 * @property renderToString Shortcut method to render the document as a string.
 * @property render Shortcut method to render the document without a node context.
 */
@Suppress("NOTHING_TO_INLINE")
open class Renderer {

    /** Shortcut method to render the document as string. */
    fun renderToString(document: MDocument, context: MContext, partial: MPool): String {
        val result = StringBuilder(128)
        render(document, NodeContext(context), partial) { result.append(it) }
        return result.toString()
    }

    /**
     * Shortcut method to render the document without a node context.
     */
    fun render(document: MDocument, context: MContext, pool: MPool, writer: (CharSequence) -> Unit) =
        render(document, NodeContext(context), pool, writer)

    /**
     * Render the given mustache [document] into [writer].
     *
     * @param document Parsed representation of a mustache document.
     * @param context You need to have scoped context when rendering a section.
     *             If you don't find it inside the current context, maybe the parent have it.
     * @param pool Where you find other documents when a partial occurs.
     * @param writer Where you write parts of the rendered document.
     */
    open fun render(
        document: MDocument,
        context: NodeContext,
        pool: MPool,
        writer: (CharSequence) -> Unit,
    ) = when (document) {
        MDocument.Empty, MDocument.Comment, MDocument.Delimiter -> Unit
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
                    is MContext.List -> !newNode.current.iterator(newNode).hasNext()
                    is MContext.Map, is MContext.Value, MContext.Yes -> false
                    is MContext.Delegate -> error("unreachable")
                }
                if (shouldRenderInverted) {
                    renderSectionItems(document, pool, NodeContext(newNode.current, node), writer)
                }
            } else {
                // Not inverted context
                when (newNode.current) {
                    is MContext.Map -> {
                        val nextNode = if (node == newNode) newNode else NodeContext(newNode.current, node)
                        renderSectionItems(document, pool, nextNode, writer)
                    }

                    is MContext.Value, MContext.Yes -> renderSectionItems(document, pool, newNode, writer)
                    is MContext.List -> {
                        for (ctx in newNode.current.iterator(newNode)) {
                            renderSectionItems(document, pool, NodeContext(ctx, newNode), writer)
                        }
                    }

                    MContext.No -> Unit
                    is MContext.Delegate -> error("unreachable")
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
                is MContext.List -> {
                    iterate(newNode.current, newNode) {
                        if (it is MContext.Value) writer(it.get(newNode))
                    }
                    NodeContext.STOP
                }

                is MContext.Value -> {
                    writer(newNode.current.get(newNode))
                    NodeContext.STOP
                }

                is MContext.Map, MContext.No, MContext.Yes -> {
                    NodeContext.CONTINUE
                }

                is MContext.Delegate -> error("unreachable")
            }
        }
    }

    private fun iterate(context: MContext.List, node: NodeContext, item: (MContext) -> Unit) {
        for (ctx in context.iterator(node)) {
            if (ctx is MContext.List) iterate(ctx, NodeContext(ctx, node), item)
            else item(ctx)
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

    private inline fun renderNewLine(
        document: NewLine,
        writer: (CharSequence) -> Unit,
    ) {
        if (document.render) writer(document.kind.representation)
    }

    // endregion
}