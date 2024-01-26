@file:Suppress("ReplaceSizeZeroCheckWithIsEmpty")

package net.orandja.ktm.composition.render

import net.orandja.ktm.base.*

/**
 * Renderer is a class that provides methods for rendering Mustache templates.
 *
 * @property renderToString Shortcut method to render the document as a string.
 * @property render Shortcut method to render the document without a node context.
 */
@Suppress("NOTHING_TO_INLINE")
open class Renderer {

    fun renderToString(document: MDocument, context: MContext, partial: MPool): String {
        val result = StringBuilder(128)
        render(document, NodeContext(context), partial) { result.append(it) }
        return result.toString()
    }

    fun render(
        document: MDocument,
        context: MContext,
        pool: MPool,
        writer: (CharSequence) -> Unit,
    ) = render(document, NodeContext(context), pool, writer)

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
    ) {
        when (document) {
            is MDocument.Section -> renderSection(document, context, pool, writer)
            is MDocument.Static -> renderStatic(document, writer)
            is MDocument.Tag -> renderTag(document, context, writer)
            is MDocument.Partial -> renderPartial(document, context, pool, writer)
        }
    }

    protected open fun renderSection(
        document: MDocument.Section,
        context: NodeContext,
        pool: MPool,
        writer: (CharSequence) -> Unit,
    ) {
        val node = context.find(document.name)
        when {
            document.inverted -> {
                if (node == null) for (part in document.parts) render(part, context, pool, writer)
                else if (node.accept(context, UseInvertedVisitor)) {
                    val newNode = NodeContext(node, context)
                    for (part: MDocument in document.parts) render(part, newNode, pool, writer)
                }
            }

            node != null -> {
                node.accept(context, SectionVisitor { newNode ->
                    for (part in document.parts) render(part, newNode, pool, writer)
                })
            }
        }
    }

    protected open fun renderPartial(
        document: MDocument.Partial,
        context: NodeContext,
        pool: MPool,
        writer: (CharSequence) -> Unit
    ) {
        val partialDocument = pool[document.name.toString()] ?: return
        val spaces = document.padding
        if (spaces.length == 0) render(partialDocument, context, pool, writer)
        else {
            writer(spaces)
            PartialRenderer(spaces).render(partialDocument, context, pool, writer)
        }
    }

    protected open fun renderTag(
        document: MDocument.Tag,
        context: NodeContext,
        writer: (CharSequence) -> Unit,
    ) {
        val node = context.find(document.name) ?: return
        val toPrint = node.accept(context, TagRenderVisitor)
        if (toPrint != null) {
            if (document.escapeHtml) MustacheEscape.escape(toPrint, writer)
            else writer(toPrint)
        }
    }

    protected open fun renderStatic(document: MDocument.Static, writer: (CharSequence) -> Unit) =
        writer(document.content)


    object UseInvertedVisitor : MContext.Visitor.Default<NodeContext, Boolean>(false) {
        override fun no(data: NodeContext, value: MContext.No) = true
        override fun list(data: NodeContext, list: MContext.List): Boolean {
            return !list.iterator(data).hasNext()
        }

        override fun delegate(data: NodeContext, delegate: MContext.Delegate): Boolean =
            delegate.get(data).accept(data, this)
    }

    class SectionVisitor(
        val onNewNode: (NodeContext) -> Unit,
    ) : MContext.Visitor<NodeContext, Unit> {
        override fun yes(data: NodeContext, value: MContext.Yes) = onNewNode(data)
        override fun value(data: NodeContext, value: MContext.Value) =
            if (data.current == value) onNewNode(data) else onNewNode(NodeContext(value, data))

        override fun map(data: NodeContext, map: MContext.Map) =
            if (data.current == map) onNewNode(data) else onNewNode(NodeContext(map, data))

        override fun list(data: NodeContext, list: MContext.List) {
            for (context in list.iterator(data)) onNewNode(NodeContext(context, data))
        }

        override fun delegate(data: NodeContext, delegate: MContext.Delegate) = delegate.get(data).accept(data, this)
        override fun no(data: NodeContext, value: MContext.No) {}
    }
}