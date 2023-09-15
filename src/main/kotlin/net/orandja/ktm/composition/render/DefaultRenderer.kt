package net.orandja.ktm.composition.render

import net.orandja.ktm.base.*
import net.orandja.ktm.base.MDocument.*

@Suppress("NOTHING_TO_INLINE")
class DefaultRenderer : MRenderer {

    override fun render(
        document: MDocument,
        node: NodeContext,
        pool: MPool,
        writer: (CharSequence) -> Unit,
    ) = when (document) {
        is Section -> renderSectionItems(SectionNode(document, 0), node, pool, writer)
        else -> render(ElementNode(document), node, pool, writer)
    }

    /**
     * To properly render some parts of a document, the renderer needs to know
     * what is before and after the currently drawn [MDocument].
     */
    interface NodeDocument {
        val holder: MDocument
        val current: MDocument?
        fun move(offset: Int)
        fun getNearBy(offset: Int): MDocument?
    }

    /**
     * Only [MDocument.Section] can be a [NodeDocument].
     * Based on [index] determine [current].
     */
    data class SectionNode(override val holder: Section, var index: Int) : NodeDocument {
        override val current: MDocument?
            get() = if (0 <= index && index < holder.parts.size) holder.parts[index] else null

        override fun getNearBy(offset: Int): MDocument? =
            if (0 <= index + offset && index + offset < holder.parts.size) holder.parts[index] else null

        override fun move(offset: Int) {
            index += offset
        }
    }

    /**
     * A node that has no elements.
     * We can use this for all other kinds of [MDocument] than [MDocument.Section].
     */
    @JvmInline
    value class ElementNode<Kind : MDocument>(override val holder: Kind) : NodeDocument {
        override val current: MDocument? get() = null
        override fun getNearBy(offset: Int): MDocument? = null
        override fun move(offset: Int) = Unit
    }

    private fun render(
        document: NodeDocument,
        context: NodeContext,
        pool: MPool,
        writer: (CharSequence) -> Unit,
    ) {
        when (val doc = document.current) {
            Comment -> renderComment(document, writer)
            NewLine -> renderNewLine(document, writer)
            is Static -> renderStatic(document, writer)
            is Partial -> renderPartial(document, context, pool, writer)
            is Tag -> renderTag(document, context, writer)
            is Section -> renderSection(SectionNode(doc, 0), context, pool, writer)
            null -> return
        }
    }

    // region section

    private inline fun renderSection(
        document: SectionNode,
        context: NodeContext,
        pool: MPool,
        noinline writer: (CharSequence) -> Unit,
    ) {
        val render = context.collect(document.holder.name) { newNode ->
            if (document.holder.inverted) {
                // inverted context
                val shouldRenderInverted = when (newNode.current) {
                    MContext.No -> true
                    is MContext.Multi -> !newNode.current.iterator(newNode).hasNext()
                    is MContext.Group, is MContext.Value, MContext.Yes -> false
                }
                if (shouldRenderInverted) {
                    renderSectionItems(document, NodeContext(newNode.current, context), pool, writer)
                }
            } else {
                // Not inverted context
                when (newNode.current) {
                    is MContext.Group -> {
                        val nextNode = if (context == newNode) newNode else NodeContext(newNode.current, context)
                        renderSectionItems(document, nextNode, pool, writer)
                    }

                    is MContext.Value, MContext.Yes -> renderSectionItems(document, newNode, pool, writer)
                    is MContext.Multi -> {
                        val iterator = newNode.current.iterator(newNode)
                        while (iterator.hasNext()) {
                            val nextNode = NodeContext(iterator.next(), NodeContext(newNode.current, context))
                            renderSectionItems(document, nextNode, pool, writer)
                        }
                    }

                    MContext.No -> Unit
                }
            }
            NodeContext.STOP
        }
        // Broken context in {{.}} should be considered falsey
        if (!render && document.holder.inverted) {
            renderSectionItems(document, context, pool, writer)
        }
    }

    private inline fun renderSectionItems(
        document: SectionNode,
        context: NodeContext,
        pool: MPool,
        noinline writer: (CharSequence) -> Unit,
    ) {
        while (true) {
            document.current ?: return
            render(document, context, pool, writer)
            document.index += 1
        }
    }

    // endregion

    // region tag

    private inline fun renderTag(
        document: NodeDocument,
        context: NodeContext,
        noinline writer: (CharSequence) -> Unit,
    ) {
        if (document.get<Tag>().escapeHtml) {
            value(document, context) { escape(it, writer) }
        } else {
            value(document, context, writer)
        }
    }

    private inline fun value(
        document: NodeDocument,
        context: NodeContext,
        noinline writer: (CharSequence) -> Unit,
    ) {
        context.collect(document.get<Tag>().name) { newNode ->
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
        document: NodeDocument,
        context: NodeContext,
        pool: MPool,
        noinline writer: (CharSequence) -> Unit,
    ) {
        val partDocument = pool[document.get<Partial>().name] ?: return
        this.render(partDocument, context, pool, writer)
    }

    private inline fun renderStatic(
        document: NodeDocument,
        writer: (CharSequence) -> Unit,
    ) = writer(document.get<Static>().content)

    private inline fun renderNewLine(
        document: NodeDocument,
        writer: (CharSequence) -> Unit,
    ) = writer("\n")

    private inline fun renderComment(
        document: NodeDocument,
        writer: (CharSequence) -> Unit,
    ) = Unit

    // endregion

    private inline fun <reified T : MDocument> NodeDocument.get() = (current ?: holder) as T
}
