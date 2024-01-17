@file:Suppress("ReplaceSizeZeroCheckWithIsEmpty")

package net.orandja.ktm.composition.render

import net.orandja.ktm.base.MContext
import net.orandja.ktm.base.MDocument
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
        is MDocument.Static -> renderStatic(document, writer)
        is MDocument.Partial -> renderPartial(document, context, pool, writer)
        is MDocument.Tag -> renderTag(document, context, writer)
        is MDocument.Section -> renderSection(document, context, pool, writer)
    }

    // region section

    protected open fun renderSection(
        document: MDocument.Section,
        node: NodeContext,
        pool: MPool,
        writer: (CharSequence) -> Unit,
    ) {
        val render = node.collect(document.name) { newNode ->
            if (document.inverted) {
                if (newNode.current is MContext.List && !newNode.current.iterator(newNode)
                        .hasNext() || newNode.current == MContext.No
                ) {
                    for (part in document.parts) render(part, node, pool, writer)
                }
                NodeContext.STOP
            } else {
                when (newNode.current) {
                    is MContext.Value, MContext.Yes -> {
                        for (part in document.parts) render(part, newNode, pool, writer)
                    }

                    is MContext.Map -> {
                        val nextNode = if (node == newNode) newNode else NodeContext(newNode.current, node)
                        for (part in document.parts) render(part, nextNode, pool, writer)
                    }

                    is MContext.List -> {
                        for (ctx in newNode.current.iterator(newNode)) {
                            for (part in document.parts) render(part, NodeContext(ctx, newNode), pool, writer)
                        }
                    }

                    else -> {}
                }
                NodeContext.STOP
            }
        }
        if (!render && document.inverted) {
            for (part in document.parts) render(part, node, pool, writer)
        }
    }

    // endregion

    // region tag

    protected open fun renderTag(
        document: MDocument.Tag,
        node: NodeContext,
        writer: (CharSequence) -> Unit,
    ) {
        if (document.escapeHtml) value(document, node) { escape(it, writer) }
        else value(document, node, writer)
    }

    protected open fun value(
        document: MDocument.Tag,
        node: NodeContext,
        writer: (CharSequence) -> Unit,
    ) {
        node.collect(document.name) { newNode ->
            if(newNode.current is MContext.Value) {
                writer(newNode.current.get(newNode))
                NodeContext.STOP
            } else NodeContext.CONTINUE
        }
    }
    // endregion

    // region others

    protected open fun renderPartial(
        document: MDocument.Partial,
        context: NodeContext,
        pool: MPool,
        writer: (CharSequence) -> Unit,
    ) {
        val partDocument = pool[document.name.toString()] ?: return
        val spaces = document.padding
        if (spaces.length == 0) {
            render(partDocument, context, pool, writer)
            return
        } else {
            writer(spaces)
            PartialRenderer(spaces).render(partDocument, context, pool, writer)
        }
    }

    protected open fun renderStatic(
        document: MDocument.Static,
        writer: (CharSequence) -> Unit,
    ) = writer(document.content)

    // endregion


    companion object {
        private const val AMP = '&'
        private const val LT = '<'
        private const val GT = '>'
        private const val D_QUOT = '"'
        private const val S_QUOT = '\''
        private const val B_QUOT = '`'
        private const val EQUAL = '='

        private const val AMP_REPLACE = "&amp;"
        private const val LT_REPLACE = "&lt;"
        private const val GT_REPLACE = "&gt;"
        private const val D_QUOT_REPLACE = "&quot;"
        private const val S_QUOT_REPLACE = "&#x27;"
        private const val B_QUOT_REPLACE = "&#x60;"
        private const val EQUAL_REPLACE = "&#x3D;"
    }

    private inline fun escape(cs: CharSequence, writer: (CharSequence) -> Unit) {
        var start = 0
        var idx = 0
        while (idx < cs.length) {
            when (cs[idx]) {
                AMP -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(AMP_REPLACE)
                    start = idx + 1
                }

                LT -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(LT_REPLACE)
                    start = idx + 1
                }

                GT -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(GT_REPLACE)
                    start = idx + 1
                }

                D_QUOT -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(D_QUOT_REPLACE)
                    start = idx + 1
                }

                S_QUOT -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(S_QUOT_REPLACE)
                    start = idx + 1
                }

                B_QUOT -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(B_QUOT_REPLACE)
                    start = idx + 1
                }

                EQUAL -> {
                    if (start < idx) writer(cs.subSequence(start, idx))
                    writer(EQUAL_REPLACE)
                    start = idx + 1
                }
            }
            idx++
        }
        if (start < idx) writer(cs.subSequence(start, idx))
    }

}